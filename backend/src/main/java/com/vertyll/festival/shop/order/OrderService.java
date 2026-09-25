package com.vertyll.festival.shop.order;

import java.time.Clock;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vertyll.festival.catalog.product.OrderableProduct;
import com.vertyll.festival.catalog.product.ProductCatalog;
import com.vertyll.festival.common.FeatureUnavailableException;
import com.vertyll.festival.common.InvalidRequestException;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.settings.ShopProperties;
import com.vertyll.festival.settings.ShopSettingsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class OrderService {

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "createdAt");

    private final OrderRepository repository;
    private final ProductCatalog catalog;
    private final ShopSettingsService settings;
    private final ShopProperties properties;
    private final OrderMapper mapper;
    private final Clock clock;

    OrderResponse place(String customerId, PlaceOrderRequest request) {
        if (!properties.checkoutEnabled()) {
            throw new FeatureUnavailableException(MessageKeys.CHECKOUT_DISABLED);
        }
        List<CartLine> cart = resolve(mergeItems(request.items()));
        List<CartLine> reserved = new ArrayList<>(cart.size());
        boolean placed = false;
        try {
            for (CartLine line : cart) {
                catalog.reserve(line.product(), line.valueCodes(), line.quantity());
                reserved.add(line);
            }
            OrderDocument order = OrderDocument.place(
                customerId,
                cart.stream().map(CartLine::toOrderLine).toList(),
                request.shipping(),
                settings.current().shippingPrice(),
                properties.currency(),
                clock.instant()
            );
            OrderResponse response = mapper.toResponse(repository.insert(order));
            placed = true;
            return response;
        } finally {
            if (!placed) {
                releaseAll(reserved);
            }
        }
    }

    List<OrderResponse> findByCustomer(String customerId) {
        return repository.findByCustomerId(customerId, NEWEST_FIRST).stream().map(mapper::toResponse).toList();
    }

    List<OrderResponse> findAll() {
        return repository.findAll(NEWEST_FIRST).stream().map(mapper::toResponse).toList();
    }

    private static Map<CartKey, Integer> mergeItems(List<PlaceOrderRequest.Item> items) {
        Map<CartKey, Integer> cart = items.stream()
            .collect(
                Collectors.toMap(
                    item -> new CartKey(item.productId(), item.selectedOptions()),
                    PlaceOrderRequest.Item::quantity,
                    Integer::sum,
                    LinkedHashMap::new
                )
            );
        if (cart.values().stream().anyMatch(quantity -> quantity > PlaceOrderRequest.MAX_QUANTITY)) {
            throw new InvalidRequestException(
                MessageKeys.CART_QUANTITY_TOO_LARGE,
                Map.of("max", PlaceOrderRequest.MAX_QUANTITY)
            );
        }
        return cart;
    }

    private List<CartLine> resolve(Map<CartKey, Integer> cart) {
        Map<ObjectId, OrderableProduct> products =
                catalog.findOrderable(cart.keySet().stream().map(CartKey::productId).toList());
        return cart.entrySet().stream().map(entry -> {
            OrderableProduct product = products.get(entry.getKey().productId());
            if (product == null) {
                throw new InvalidRequestException(MessageKeys.CART_PRODUCT_UNAVAILABLE);
            }
            return new CartLine(product, product.valueCodesFor(entry.getKey().selectedOptions()), entry.getValue());
        }).toList();
    }

    private void releaseAll(List<CartLine> reserved) {
        for (CartLine line : reserved) {
            try {
                catalog.release(line.product().id(), line.valueCodes(), line.quantity());
            } catch (DataAccessException | IllegalStateException releaseFailure) {
                log.error(
                    "[ERROR] Failed to return {} units of variant {} for product {}",
                    line.quantity(),
                    line.valueCodes(),
                    line.product().id(),
                    releaseFailure
                );
            }
        }
    }

    private record CartKey(ObjectId productId, Map<String, String> selectedOptions) {
    }

    private record CartLine(OrderableProduct product, List<String> valueCodes, int quantity) {

        OrderLine toOrderLine() {
            return OrderLine.of(product, valueCodes, quantity);
        }
    }
}
