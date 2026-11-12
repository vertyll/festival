package com.vertyll.festival.shop.order;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.bson.Document;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import com.vertyll.festival.IntegrationTest;
import com.vertyll.festival.catalog.product.InsufficientStockException;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.shop.ShippingDetailsRequest;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static com.vertyll.festival.TestUsers.customer;

@IntegrationTest
class CheckoutIT {

    private static final ShippingDetailsRequest SHIPPING =
            new ShippingDetailsRequest("Jan", "jan@example.com", "Długa 1", "80-001", "Gdańsk", "Polska");

    @Autowired
    OrderService orders;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MongoTemplate mongo;

    @Autowired
    MockMvc mvc;

    @Test
    void concurrentOrdersNeverOversell() throws InterruptedException, ExecutionException {
        ObjectId mug = insertProduct("Kubek", "19.99", List.of(), List.of(variant(List.of(), 5)));
        CountDownLatch start = new CountDownLatch(1);
        List<Callable<Boolean>> attempts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String customerId = "klient-" + i;
            attempts.add(() -> {
                start.await();
                try {
                    orders.place(customerId, orderOf(mug, Map.of(), 1));
                    return true;
                } catch (InsufficientStockException _) {
                    return false;
                }
            });
        }

        List<Boolean> results = new ArrayList<>();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Boolean>> futures = attempts.stream().map(executor::submit).toList();
            start.countDown();
            for (Future<Boolean> future : futures) {
                results.add(future.get());
            }
        }

        assertThat(results).filteredOn(Boolean::booleanValue).hasSize(5);
        assertThat(stockOf(mug)).containsExactly(0);
    }

    @Test
    void stockIsTakenFromTheSelectedVariantAndPricesComeFromTheDatabase() {
        ObjectId tShirt = insertProduct(
            "Koszulka",
            "49.99",
            List.of(
                new Document("code", "size").append("name", localized("Rozmiar"))
                    .append("values", List.of(optionValue("s", "S"), optionValue("m", "M")))
            ),
            List.of(variant(List.of("s"), 1), variant(List.of("m"), 2))
        );

        OrderResponse order = orders.place("klient", orderOf(tShirt, Map.of("size", "m"), 2));

        assertThat(stockOf(tShirt)).containsExactly(1, 0);
        OrderResponse.Line line = order.lines().getFirst();
        assertThat(line.productName()).isEqualTo(new LocalizedText("Koszulka", "Koszulka (en)"));
        assertThat(line.selection()).containsExactly(
            new OrderResponse.SelectedOption(
                new LocalizedText("Rozmiar", "Rozmiar (en)"),
                new LocalizedText("M", "M (en)")
            )
        );
        assertThat(line.unitPrice()).isEqualByComparingTo("49.99");
        assertThat(line.lineTotal()).isEqualByComparingTo("99.98");
        assertThat(order.total()).isEqualByComparingTo(order.itemsTotal().add(order.shippingPrice()));
        assertThat(order.status()).isEqualTo(OrderStatus.AWAITING_PAYMENT);
    }

    @Test
    void placedOrderIsAnsweredInTheShapeTheFrontEndsRead() {
        ObjectId tShirt = insertProduct(
            "Koszulka",
            "49.99",
            List.of(
                new Document("code", "size").append("name", localized("Rozmiar"))
                    .append("values", List.of(optionValue("s", "S"), optionValue("m", "M")))
            ),
            List.of(variant(List.of("s"), 1), variant(List.of("m"), 2))
        );

        MvcTestResult result = MockMvcTester.create(mvc)
            .perform(
                post("/api/orders").with(customer()).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                        {
                          "shipping": {
                            "name": " Jan ", "email": "jan@example.com", "streetAddress": "Długa 1",
                            "postalCode": "80-001", "city": "Gdańsk", "country": "Polska"
                          },
                          "items": [{"productId": "%s", "selectedOptions": {"size": "m"}, "quantity": 2}]
                        }
                        """.formatted(tShirt))
            );

        assertThat(result).hasStatus(HttpStatus.CREATED);
        String body = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        String id = JsonPath.read(body, "$.id");
        String createdAt = JsonPath.read(body, "$.createdAt");
        assertThat(result).bodyJson().isStrictlyEqualTo("""
                {
                  "id": "%s",
                  "status": "AWAITING_PAYMENT",
                  "lines": [{
                    "productId": "%s",
                    "productName": {"pl": "Koszulka", "en": "Koszulka (en)"},
                    "valueCodes": ["m"],
                    "selection": [{
                      "option": {"pl": "Rozmiar", "en": "Rozmiar (en)"},
                      "value": {"pl": "M", "en": "M (en)"}
                    }],
                    "unitPrice": 49.99,
                    "quantity": 2,
                    "lineTotal": 99.98
                  }],
                  "shipping": {
                    "name": "Jan", "email": "jan@example.com", "streetAddress": "Długa 1",
                    "postalCode": "80-001", "city": "Gdańsk", "country": "Polska"
                  },
                  "itemsTotal": 99.98,
                  "shippingPrice": 15.00,
                  "total": 114.98,
                  "currency": "PLN",
                  "createdAt": "%s"
                }
                """.formatted(id, tShirt, createdAt));
    }

    @Test
    void failedOrderReleasesEarlierReservations() {
        ObjectId available = insertProduct("Smycz", "5.00", List.of(), List.of(variant(List.of(), 3)));
        ObjectId soldOut = insertProduct("Czapka", "5.00", List.of(), List.of(variant(List.of(), 0)));
        long ordersBefore = orderRepository.count();

        PlaceOrderRequest request = new PlaceOrderRequest(
            SHIPPING,
            List.of(
                new PlaceOrderRequest.Item(available, Map.of(), 2),
                new PlaceOrderRequest.Item(soldOut, Map.of(), 1)
            )
        );

        assertThatThrownBy(() -> orders.place("klient", request)).isInstanceOf(InsufficientStockException.class);
        assertThat(stockOf(available)).containsExactly(3);
        assertThat(orderRepository.count()).isEqualTo(ordersBefore);
    }

    @Test
    void sameVariantInSeveralCartLinesIsReservedAsOneQuantity() {
        ObjectId badge = insertProduct("Przypinka", "3.00", List.of(), List.of(variant(List.of(), 3)));
        PlaceOrderRequest request = new PlaceOrderRequest(
            SHIPPING,
            List.of(new PlaceOrderRequest.Item(badge, Map.of(), 2), new PlaceOrderRequest.Item(badge, Map.of(), 2))
        );

        assertThatThrownBy(() -> orders.place("klient", request)).isInstanceOf(InsufficientStockException.class);
        assertThat(stockOf(badge)).containsExactly(3);
    }

    private ObjectId insertProduct(String name, String price, List<Document> options, List<Document> variants) {
        ObjectId id = new ObjectId();
        mongo.getCollection("products")
            .insertOne(
                new Document("_id", id).append("name", localized(name))
                    .append("description", null)
                    .append("price", new Decimal128(new BigDecimal(price)))
                    .append("categoryId", null)
                    .append("images", List.of())
                    .append("options", options)
                    .append("variants", variants)
                    .append("createdAt", Instant.now())
                    .append("updatedAt", Instant.now())
            );
        return id;
    }

    private List<Integer> stockOf(ObjectId productId) {
        Document product = mongo.findOne(query(where("_id").is(productId)), Document.class, "products");
        assertThat(product).isNotNull();
        return product.getList("variants", Document.class)
            .stream()
            .map(variant -> variant.getInteger("stock"))
            .toList();
    }

    private static Document variant(List<String> valueCodes, int stock) {
        return new Document("valueCodes", valueCodes).append("stock", stock);
    }

    private static Document optionValue(String code, String label) {
        return new Document("code", code).append("label", localized(label));
    }

    private static Document localized(String polish) {
        return new Document("pl", polish).append("en", polish + " (en)");
    }

    private static PlaceOrderRequest orderOf(ObjectId productId, Map<String, String> selection, int quantity) {
        return new PlaceOrderRequest(SHIPPING, List.of(new PlaceOrderRequest.Item(productId, selection, quantity)));
    }
}
