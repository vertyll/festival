package com.vertyll.festival.i18n;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.common.Language;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
class TranslationPublicController {

    private final TranslationService service;

    @GetMapping("/{language}")
    Map<String, String> messages(@PathVariable String language) {
        return service.messages(
            Language.fromCode(language).orElseThrow(() -> new NotFoundException(MessageKeys.LANGUAGE_NOT_FOUND))
        );
    }
}
