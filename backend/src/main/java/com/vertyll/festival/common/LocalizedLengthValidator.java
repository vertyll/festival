package com.vertyll.festival.common;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jspecify.annotations.Nullable;

public final class LocalizedLengthValidator implements ConstraintValidator<LocalizedLength, LocalizedText> {

    private int max;

    @Override
    public void initialize(LocalizedLength constraint) {
        max = constraint.max();
    }

    @Override
    public boolean isValid(@Nullable LocalizedText value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        boolean valid = true;
        for (Language language : Language.values()) {
            String text = value.in(language);

            String problem;
            if (text.isEmpty()) {
                problem = MessageKeys.REQUIRED;
            } else if (text.length() > max) {
                problem = MessageKeys.TOO_LONG;
            } else {
                problem = null;
            }

            if (problem != null) {
                valid = false;
                context.buildConstraintViolationWithTemplate(problem)
                    .addPropertyNode(language.code())
                    .addConstraintViolation();
            }
        }
        return valid;
    }
}
