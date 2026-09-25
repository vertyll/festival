package com.vertyll.festival.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import jakarta.validation.Constraint;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintMessagesTest {

    @Test
    void everyConstraintMessageIsTranslationKey() throws IOException, ReflectiveOperationException {
        Set<String> messages = new TreeSet<>();
        for (JavaClass javaClass : new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("com.vertyll.festival")) {
            for (Field field : javaClass.reflect().getDeclaredFields()) {
                for (Annotation annotation : annotations(field).toList()) {
                    if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                        messages.add((String) annotation.annotationType().getMethod("message").invoke(annotation));
                    }
                }
            }
        }

        assertThat(messages).contains(MessageKeys.REQUIRED).isSubsetOf(translationKeys());
    }

    private static Stream<Annotation> annotations(Field field) {
        return Stream.concat(Arrays.stream(field.getAnnotations()), annotations(field.getAnnotatedType()));
    }

    private static Stream<Annotation> annotations(AnnotatedType type) {
        Stream<AnnotatedType> nested = switch (type) {
            case AnnotatedParameterizedType parameterized ->
                Arrays.stream(parameterized.getAnnotatedActualTypeArguments());
            case AnnotatedArrayType array -> Stream.of(array.getAnnotatedGenericComponentType());
            default -> Stream.empty();
        };
        return Stream.concat(Arrays.stream(type.getAnnotations()), nested.flatMap(ConstraintMessagesTest::annotations));
    }

    private static Set<String> translationKeys() throws IOException {
        try (InputStream input = new ClassPathResource("i18n/pl.json").getInputStream()) {
            return JsonMapper.builder().build().readValue(input, new TypeReference<Map<String, String>>() {
            }).keySet();
        }
    }
}
