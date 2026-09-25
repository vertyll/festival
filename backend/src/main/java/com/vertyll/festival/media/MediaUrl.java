package com.vertyll.festival.media;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.vertyll.festival.common.MessageKeys;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MediaUrlValidator.class)
@Target(
    {
        FIELD,
        PARAMETER,
        RECORD_COMPONENT,
        TYPE_USE
    }
)
@Retention(RUNTIME)
public @interface MediaUrl {

    String message() default MessageKeys.MEDIA_URL_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
