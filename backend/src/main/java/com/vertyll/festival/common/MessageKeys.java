package com.vertyll.festival.common;

@SuppressWarnings("PMD.DataClass")
public final class MessageKeys {

    public static final String REQUIRED = "validation.required";
    public static final String TOO_LONG = "validation.tooLong";
    public static final String TOO_LARGE = "validation.tooLarge";
    public static final String INVALID_VALUE = "validation.invalidValue";
    public static final String EMAIL_INVALID = "validation.email";
    public static final String URL_INVALID = "validation.url";
    public static final String MEDIA_URL_INVALID = "validation.mediaUrl";
    public static final String PRICE_INVALID = "validation.price";
    public static final String SHIPPING_PRICE_INVALID = "validation.shippingPrice";
    public static final String STOCK_INVALID = "validation.stock";
    public static final String CODE_INVALID = "validation.code";
    public static final String TOO_MANY_IMAGES = "validation.tooManyImages";
    public static final String TOO_MANY_ATTRIBUTE_VALUES = "validation.tooManyAttributeValues";
    public static final String TOO_MANY_PRODUCT_OPTIONS = "validation.tooManyProductOptions";
    public static final String TOO_MANY_OPTION_VALUES = "validation.tooManyOptionValues";
    public static final String OPTION_VALUES_REQUIRED = "validation.optionValuesRequired";
    public static final String VARIANTS_REQUIRED = "validation.variantsRequired";
    public static final String CART_EMPTY = "validation.cartEmpty";
    public static final String CART_TOO_MANY_LINES = "validation.cartTooManyLines";
    public static final String QUANTITY_TOO_SMALL = "validation.quantityTooSmall";
    public static final String QUANTITY_TOO_LARGE = "validation.quantityTooLarge";
    public static final String ICU_INVALID = "validation.icu";

    public static final String INVALID_FORM = "errors.invalidForm";
    public static final String DUPLICATE_ENTRY = "errors.duplicateEntry";
    public static final String HTTP_STATUS_PREFIX = "errors.status.";

    public static final String CATEGORY_NOT_FOUND = "errors.category.notFound";
    public static final String CATEGORY_PARENT_NOT_FOUND = "errors.category.parentNotFound";
    public static final String CATEGORY_CYCLE = "errors.category.cycle";
    public static final String ATTRIBUTE_NOT_FOUND = "errors.attribute.notFound";
    public static final String PRODUCT_NOT_FOUND = "errors.product.notFound";
    public static final String PRODUCT_CATEGORY_NOT_FOUND = "errors.product.categoryNotFound";
    public static final String PRODUCT_OPTIONS_INCOMPLETE = "errors.product.optionsIncomplete";
    public static final String PRODUCT_OPTION_VALUE_INVALID = "errors.product.optionValueInvalid";
    public static final String PRODUCT_OPTION_DUPLICATED = "errors.product.optionDuplicated";
    public static final String OPTION_VALUE_DUPLICATED = "errors.optionValueDuplicated";
    public static final String PRODUCT_VARIANT_DUPLICATED = "errors.product.variantDuplicated";
    public static final String PRODUCT_VARIANTS_MISMATCH = "errors.product.variantsMismatch";
    public static final String PRODUCT_TOO_MANY_VARIANTS = "errors.product.tooManyVariants";
    public static final String PRODUCT_INSUFFICIENT_STOCK = "errors.product.insufficientStock";
    public static final String ARTIST_NOT_FOUND = "errors.artist.notFound";
    public static final String ARTIST_STAGE_NOT_FOUND = "errors.artist.stageNotFound";
    public static final String STAGE_NOT_FOUND = "errors.stage.notFound";
    public static final String NEWS_NOT_FOUND = "errors.news.notFound";
    public static final String SPONSOR_NOT_FOUND = "errors.sponsor.notFound";
    public static final String ADMINISTRATOR_NOT_FOUND = "errors.administrator.notFound";
    public static final String ADMINISTRATOR_EXISTS = "errors.administrator.exists";
    public static final String ADMINISTRATOR_LAST = "errors.administrator.last";
    public static final String ADMINISTRATOR_SELF_UPDATE = "errors.administrator.selfUpdate";
    public static final String ADMINISTRATOR_SELF_DELETE = "errors.administrator.selfDelete";
    public static final String CHECKOUT_DISABLED = "errors.checkout.disabled";
    public static final String CART_QUANTITY_TOO_LARGE = "errors.cart.quantityTooLarge";
    public static final String CART_PRODUCT_UNAVAILABLE = "errors.cart.productUnavailable";
    public static final String MEDIA_NO_FILES = "errors.media.noFiles";
    public static final String MEDIA_TOO_MANY_FILES = "errors.media.tooManyFiles";
    public static final String MEDIA_FILE_TOO_LARGE = "errors.media.fileTooLarge";
    public static final String MEDIA_UNSUPPORTED_TYPE = "errors.media.unsupportedType";
    public static final String LANGUAGE_NOT_FOUND = "errors.language.notFound";
    public static final String TRANSLATION_NOT_FOUND = "errors.translation.notFound";
    public static final String TRANSLATION_UNKNOWN_PLACEHOLDERS = "errors.translation.unknownPlaceholders";
    public static final String TRANSLATION_IMPORT_INVALID_FILE = "errors.translation.import.invalidFile";
    public static final String TRANSLATION_IMPORT_INVALID_HEADER = "errors.translation.import.invalidHeader";
    public static final String TRANSLATION_IMPORT_EMPTY_MESSAGE = "errors.translation.import.emptyMessage";
    public static final String TRANSLATION_IMPORT_UNKNOWN_KEY = "errors.translation.import.unknownKey";
    public static final String TRANSLATION_IMPORT_DUPLICATE_KEY = "errors.translation.import.duplicateKey";
    public static final String TRANSLATION_IMPORT_INVALID_MESSAGE = "errors.translation.import.invalidMessage";
    public static final String TRANSLATION_IMPORT_TOO_LONG = "errors.translation.import.tooLong";
    public static final String TRANSLATION_IMPORT_UNKNOWN_PLACEHOLDERS =
            "errors.translation.import.unknownPlaceholders";

    private MessageKeys() {
    }
}
