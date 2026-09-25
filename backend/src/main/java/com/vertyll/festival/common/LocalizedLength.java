package com.vertyll.festival.common;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = LocalizedLengthValidator.class)
@Target(
    {
        FIELD,
        PARAMETER,
        RECORD_COMPONENT,
        TYPE_USE
    }
)
@Retention(RUNTIME)
public @interface LocalizedLength {

    int max();

    String message() default MessageKeys.TOO_LONG;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
