package com.vertyll.festival.common;

import java.io.Serial;
import java.util.Map;

import org.springframework.http.HttpStatus;

public final class FeatureUnavailableException extends ApiException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FeatureUnavailableException(String messageKey) {
        this(messageKey, Map.of());
    }

    public FeatureUnavailableException(String messageKey, Map<String, Object> messageArgs) {
        super(messageKey, messageArgs);
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }
}
