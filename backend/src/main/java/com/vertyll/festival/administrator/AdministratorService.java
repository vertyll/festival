package com.vertyll.festival.administrator;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vertyll.festival.common.ConflictException;
import com.vertyll.festival.common.MessageKeys;
import com.vertyll.festival.common.NotFoundException;
import com.vertyll.festival.security.AdministratorDirectory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class AdministratorService implements AdministratorDirectory {

    private static final long MIN_ADMINISTRATORS = 1;

    private static final String EMAIL = "email";

    private final AdministratorRepository repository;
    private final AdministratorMapper mapper;
    private final Clock clock;

    @Override
    public boolean isAdministrator(String email) {
        return repository.existsByEmail(normalize(email));
    }

    List<AdministratorResponse> findAll() {
        return repository.findAll(Sort.by(EMAIL)).stream().map(mapper::toResponse).toList();
    }

    AdministratorResponse create(AdministratorRequest request) {
        String email = normalize(request.email());
        if (repository.existsByEmail(email)) {
            throw new ConflictException(MessageKeys.ADMINISTRATOR_EXISTS, Map.of(EMAIL, email));
        }
        return mapper.toResponse(repository.insert(AdministratorDocument.create(email, clock.instant())));
    }

    AdministratorResponse update(ObjectId id, AdministratorRequest request, String actorEmail) {
        AdministratorDocument current = get(id);
        requireNotSelf(current, actorEmail, MessageKeys.ADMINISTRATOR_SELF_UPDATE);
        String email = normalize(request.email());
        repository.findByEmail(email).filter(other -> !other.id().equals(id)).ifPresent(_ -> {
            throw new ConflictException(MessageKeys.ADMINISTRATOR_EXISTS, Map.of(EMAIL, email));
        });
        return mapper.toResponse(repository.save(current.withEmail(email, clock.instant())));
    }

    void delete(ObjectId id, String actorEmail) {
        AdministratorDocument current = get(id);
        requireNotSelf(current, actorEmail, MessageKeys.ADMINISTRATOR_SELF_DELETE);
        if (repository.count() <= MIN_ADMINISTRATORS) {
            throw new ConflictException(MessageKeys.ADMINISTRATOR_LAST);
        }
        repository.delete(current);
    }

    void ensureExists(String email) {
        String normalized = normalize(email);
        if (!repository.existsByEmail(normalized)) {
            repository.insert(AdministratorDocument.create(normalized, clock.instant()));
            log.info("[INFO] An administrator has been added from: festival.security.bootstrap-admins");
        }
    }

    private AdministratorDocument get(ObjectId id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MessageKeys.ADMINISTRATOR_NOT_FOUND));
    }

    private static void requireNotSelf(AdministratorDocument administrator, String actorEmail, String messageKey) {
        if (administrator.email().equals(normalize(actorEmail))) {
            throw new ConflictException(messageKey);
        }
    }

    private static String normalize(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
