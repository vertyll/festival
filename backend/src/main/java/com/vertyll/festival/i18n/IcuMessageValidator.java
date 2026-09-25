package com.vertyll.festival.i18n;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.jspecify.annotations.Nullable;

import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.common.MessageKeys;

class IcuMessageValidator implements ConstraintValidator<IcuMessage, LocalizedText> {

    @Override
    public boolean isValid(@Nullable LocalizedText value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        boolean valid = true;
        for (Language language : Language.values()) {
            if (!IcuMessages.isValid(value.in(language))) {
                valid = false;
                context.buildConstraintViolationWithTemplate(MessageKeys.ICU_INVALID)
                    .addPropertyNode(language.code())
                    .addConstraintViolation();
            }
        }
        return valid;
    }
}
