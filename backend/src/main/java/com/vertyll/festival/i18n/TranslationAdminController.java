package com.vertyll.festival.i18n;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/translations")
@RequiredArgsConstructor
class TranslationAdminController {

    private final TranslationService service;

    @GetMapping
    List<TranslationResponse> list() {
        return service.findAll();
    }

    @GetMapping("/export")
    ResponseEntity<byte[]> export() {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(TranslationSpreadsheet.CONTENT_TYPE))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(TranslationSpreadsheet.FILE_NAME).build().toString()
            )
            .body(service.exportSpreadsheet());
    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    TranslationImportResponse importSpreadsheet(@RequestParam("file") MultipartFile file) {
        return service.importSpreadsheet(file);
    }

    @PutMapping("/{key}")
    TranslationResponse update(@PathVariable String key, @Valid @RequestBody TranslationRequest request) {
        return service.update(key, request);
    }

    @DeleteMapping("/{key}/customization")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void reset(@PathVariable String key) {
        service.reset(key);
    }
}
