# Order placement

What happens between a cart in the browser and a stored order.

The cart lives in the browser until an order is placed. Placing one takes the products and prices from the catalog,
not from the request, and reserves the stock line by line; when a line cannot be reserved, everything reserved so far is
released and no order is written. `FESTIVAL_CHECKOUT_ENABLED` switches ordering off without hiding the shop, and the
settings an admin changes start from `application.shop.initial-settings`.
