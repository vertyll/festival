package com.vertyll.festival.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.vertyll.festival.common.MessageKeys;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = IcuMessageValidator.class)
@Target(
    {
        FIELD,
        RECORD_COMPONENT
    }
)
@Retention(RUNTIME)
@interface IcuMessage {

    String message() default MessageKeys.ICU_INVALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
