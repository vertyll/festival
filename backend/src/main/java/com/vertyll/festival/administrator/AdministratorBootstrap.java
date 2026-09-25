package com.vertyll.festival.administrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.vertyll.festival.security.FestivalSecurityProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class AdministratorBootstrap implements ApplicationRunner {

    private final FestivalSecurityProperties properties;
    private final AdministratorService administrators;

    @Override
    public void run(ApplicationArguments args) {
        properties.bootstrapAdmins().forEach(administrators::ensureExists);
    }
}
