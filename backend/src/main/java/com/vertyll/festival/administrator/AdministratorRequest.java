package com.vertyll.festival.administrator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.ValidationLimits;

record AdministratorRequest(
    @NotBlank(message = MessageKeys.REQUIRED) @Email(
        message = MessageKeys.EMAIL_INVALID
    ) @Size(max = ValidationLimits.EMAIL_MAX_LENGTH, message = MessageKeys.EMAIL_INVALID) String email
) {
}
