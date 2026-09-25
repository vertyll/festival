package com.vertyll.festival.common;

@SuppressWarnings("PMD.DataClass")
public final class ValidationLimits {

    public static final int NAME_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MAX_LENGTH = 10_000;
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int MAX_IMAGES = 20;
    public static final int ATTRIBUTE_VALUE_MAX_LENGTH = 25;
    public static final int URL_MAX_LENGTH = 2048;
    public static final int MAX_ATTRIBUTE_VALUES = 50;
    public static final int MAX_PRODUCT_OPTIONS = 5;
    public static final int MAX_OPTION_VALUES = 20;
    public static final int MAX_CART_LINES = 50;
    public static final int STREET_ADDRESS_MAX_LENGTH = 100;
    public static final int POSTAL_CODE_MAX_LENGTH = 20;
    public static final int MAX_STOCK = 1_000_000;
    public static final String MAX_PRICE = "99999999.99";
    public static final String MAX_SHIPPING_PRICE = "999999.99";

    public static final String CODE_PATTERN = "[A-Za-z0-9_-]{1,40}";

    private ValidationLimits() {
    }
}
