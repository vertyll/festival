package com.vertyll.festival.media;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
class MediaAdminController {

    private final ImageUploadService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadResponse upload(@RequestParam("files") List<MultipartFile> files) {
        return new UploadResponse(service.upload(files));
    }

    record UploadResponse(List<String> urls) {
    }
}
