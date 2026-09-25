package com.vertyll.festival;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.mapstruct.Mapper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.vertyll.festival", importOptions = ImportOption.DoNotIncludeTests.class)
final class ArchitectureTest {

    private static final String ADMIN_PREFIX = "/api/admin/";

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
