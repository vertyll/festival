package com.vertyll.festival;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.common.LocalizedText;
import com.vertyll.festival.keycloak.KeycloakAccounts;
import com.vertyll.festival.keycloak.KeycloakHttp;
import com.vertyll.festival.keycloak.KeycloakProperties;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.vertyll.festival", importOptions = ImportOption.DoNotIncludeTests.class)
final class ArchitectureTest {

    private static final String ADMIN_PREFIX = "/api/admin/";
    private static final String BASE_PACKAGE = "com.vertyll.festival";

    @ArchTest
    static final ArchRule KEYCLOAK_IS_REACHED_THROUGH_ITS_ENTRY = noClasses().that()
        .resideOutsideOfPackage("com.vertyll.festival.keycloak..")
        .should()
        .dependOnClassesThat(
            resideInAPackage("com.vertyll.festival.keycloak..").and(not(equivalentTo(KeycloakAccounts.class)))
                .and(not(equivalentTo(KeycloakProperties.class)))
                .and(not(equivalentTo(KeycloakHttp.class)))
        );

    @ArchTest
    static final ArchRule ADMIN_CONTROLLERS_SERVE_ONLY_ADMIN_PATHS = classes().that()
        .areAnnotatedWith(RestController.class)
        .and()
        .haveSimpleNameEndingWith("AdminController")
        .should(mapOnly(path -> path.startsWith(ADMIN_PREFIX), "paths under " + ADMIN_PREFIX));

    @ArchTest
    static final ArchRule OTHER_CONTROLLERS_STAY_OUTSIDE_ADMIN_PATHS = classes().that()
        .areAnnotatedWith(RestController.class)
        .and()
        .haveSimpleNameNotEndingWith("AdminController")
        .should(mapOnly(path -> !path.startsWith("/api/admin"), "paths outside /api/admin"));

    @ArchTest
    static final ArchRule CONTROLLERS_ARE_PACKAGE_PRIVATE =
            classes().that().areAnnotatedWith(RestController.class).should().notHaveModifier(JavaModifier.PUBLIC);

    @ArchTest
    static final ArchRule REPOSITORIES_ARE_PACKAGE_PRIVATE =
            classes().that().areAssignableTo(Repository.class).should().notHaveModifier(JavaModifier.PUBLIC);

    @ArchTest
    static final ArchRule DOCUMENTS_ARE_PACKAGE_PRIVATE =
            classes().that().areAnnotatedWith(Document.class).should().notHaveModifier(JavaModifier.PUBLIC);

    @ArchTest
    static final ArchRule MAPPERS_ARE_PACKAGE_PRIVATE =
            classes().that().areAnnotatedWith(Mapper.class).should().notHaveModifier(JavaModifier.PUBLIC);

    @ArchTest
    static final ArchRule CONTROLLERS_DO_NOT_TALK_TO_REPOSITORIES = noClasses().that()
        .areAnnotatedWith(RestController.class)
        .should()
        .dependOnClassesThat()
        .areAssignableTo(Repository.class);

    @ArchTest
    static final ArchRule STORED_RECORDS_STAY_BEHIND_THE_API = classes().should(notExposeStoredRecords());

    @ArchTest
    static final ArchRule MODULES_ARE_FREE_OF_CYCLES =
            slices().matching("com.vertyll.festival.(*)..").should().beFreeOfCycles();

    private ArchitectureTest() {
    }

    private static ArchCondition<JavaClass> mapOnly(Predicate<String> accepted, String description) {
        return new ArchCondition<>("map only " + description) {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                List<String> paths = handlerPaths(javaClass.reflect());
                boolean satisfied = !paths.isEmpty() && paths.stream().allMatch(accepted);
                events.add(new SimpleConditionEvent(javaClass, satisfied, javaClass.getName() + " maps " + paths));
            }
        };
    }

    private static ArchCondition<JavaClass> notExposeStoredRecords() {
        return new ArchCondition<>("not expose a record stored in a document through the API") {

            private final Set<Class<?>> storedRecords = new HashSet<>();

            @Override
            public void init(Collection<JavaClass> allClasses) {
                allClasses.stream()
                    .filter(javaClass -> javaClass.isAnnotatedWith(Document.class))
                    .forEach(document -> collectStoredRecords(document.reflect(), storedRecords));
                storedRecords.remove(LocalizedText.class);
            }

            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                if (!isApiType(javaClass)) {
                    return;
                }
                exposedTypes(javaClass.reflect()).stream()
                    .filter(storedRecords::contains)
                    .distinct()
                    .forEach(
                        stored -> events.add(
                            SimpleConditionEvent.violated(
                                javaClass,
                                javaClass.getName() + " exposes the stored record " + stored.getName()
                            )
                        )
                    );
            }
        };
    }

    private static List<Class<?>> exposedTypes(Class<?> apiType) {
        if (apiType.isRecord()) {
            return Arrays.stream(apiType.getRecordComponents())
                .flatMap(component -> rawTypes(component.getGenericType()).stream())
                .toList();
        }
        return Arrays.stream(apiType.getDeclaredMethods())
            .flatMap(
                method -> Stream
                    .concat(Stream.of(method.getGenericReturnType()), Arrays.stream(method.getGenericParameterTypes()))
            )
            .flatMap(type -> rawTypes(type).stream())
            .toList();
    }

    private static void collectStoredRecords(Class<?> record, Set<Class<?>> storedRecords) {
        for (RecordComponent component : record.getRecordComponents()) {
            for (Class<?> type : rawTypes(component.getGenericType())) {
                if (type.isRecord() && type.getPackageName().startsWith(BASE_PACKAGE) && storedRecords.add(type)) {
                    collectStoredRecords(type, storedRecords);
                }
            }
        }
    }

    private static List<Class<?>> rawTypes(Type type) {
        return switch (type) {
            case Class<?> raw -> List.of(raw);
            case ParameterizedType parameterized -> Stream
                .concat(Stream.of(parameterized.getRawType()), Arrays.stream(parameterized.getActualTypeArguments()))
                .flatMap(argument -> rawTypes(argument).stream())
                .toList();
            default -> List.of();
        };
    }

    private static boolean isApiType(JavaClass javaClass) {
        String name = javaClass.getSimpleName();
        return javaClass.isAnnotatedWith(RestController.class) || name.endsWith("Request") || name.endsWith("Response")
                || javaClass.getEnclosingClass().map(ArchitectureTest::isApiType).orElse(false);
    }

    private static List<String> handlerPaths(Class<?> controller) {
        RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class);
        List<String> prefixes = classMapping == null ? List.of("") : Arrays.asList(classMapping.path());
        List<String> paths = new ArrayList<>();
        for (Method method : controller.getDeclaredMethods()) {
            RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            if (mapping == null) {
                continue;
            }
            List<String> suffixes = mapping.path().length == 0 ? List.of("") : Arrays.asList(mapping.path());
            for (String prefix : prefixes) {
                for (String suffix : suffixes) {
                    paths.add(prefix + suffix);
                }
            }
        }
        return paths;
    }
}
