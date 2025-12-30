package com.tribly;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tribly.domain.common.NotNullableDbValue;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "com.tribly", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule packages_with_classes_should_be_null_marked =
            classes()
                    .should(beInPackageAnnotatedWithNullMarked())
                    .because("all packages with classes must be annotated with @NullMarked for null safety");

    @ArchTest
    static final ArchRule entities_should_have_no_args_constructor =
            classes()
                    .that()
                    .areAnnotatedWith(Entity.class)
                    .should(haveNoArgsConstructor())
                    .because("Hibernate requires a no-args constructor for entity instantiation");

    @ArchTest
    static final ArchRule entity_fields_should_have_proper_nullability_annotations =
            classes()
                    .that()
                    .areAnnotatedWith(Entity.class)
                    .or()
                    .areAnnotatedWith(MappedSuperclass.class)
                    .should(haveProperNullabilityAnnotationsOnFields())
                    .because(
                            "nullable database fields must be annotated with @Nullable, "
                                    + "non-null fields must NOT be annotated with @Nullable");

    private static ArchCondition<JavaClass> haveNoArgsConstructor() {
        return new ArchCondition<>("have a no-args constructor") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                boolean hasNoArgsConstructor =
                        javaClass.getConstructors().stream()
                                .anyMatch(c -> c.getRawParameterTypes().isEmpty());

                if (!hasNoArgsConstructor) {
                    events.add(
                            SimpleConditionEvent.violated(
                                    javaClass,
                                    String.format(
                                            "Entity '%s' does not have a no-args constructor required by Hibernate",
                                            javaClass.getSimpleName())));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> beInPackageAnnotatedWithNullMarked() {
        return new ArchCondition<>("be in a package annotated with @NullMarked") {
            private final Set<String> checkedPackages = new HashSet<>();

            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                JavaPackage javaPackage = javaClass.getPackage();
                String packageName = javaPackage.getName();

                if (checkedPackages.contains(packageName)) {
                    return;
                }
                checkedPackages.add(packageName);

                boolean hasNullMarked = javaPackage.isAnnotatedWith(NullMarked.class);

                if (!hasNullMarked) {
                    String message =
                            String.format(
                                    "Package '%s' contains classes but is not annotated with @NullMarked",
                                    packageName);
                    events.add(SimpleConditionEvent.violated(javaClass, message));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> haveProperNullabilityAnnotationsOnFields() {
        return new ArchCondition<>("have proper nullability annotations on fields") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaField field : javaClass.getFields()) {

                    boolean nullable = isNullable(field);
                    boolean hasNullableAnnotation = hasNullableTypeAnnotation(field);
                    if (nullable && !hasNullableAnnotation) {
                        events.add(
                                SimpleConditionEvent.violated(
                                        field,
                                        String.format(
                                                "Field '%s' in '%s' is nullable in database but not annotated with @Nullable",
                                                field.getName(), javaClass.getSimpleName())));
                    }
                    if (!nullable && hasNullableAnnotation) {
                        events.add(
                                SimpleConditionEvent.violated(
                                        field,
                                        String.format(
                                                "Field '%s' in '%s' is NOT nullable in database but is annotated with @Nullable",
                                                field.getName(), javaClass.getSimpleName())));
                    }
                }
            }

        };
    }

    enum Nullability {
        NOT_NULL_USER,
        NOT_NULL_JPA,
        NULLABLE
    }

    private static boolean isNullable(JavaField field) {
        // Skip fields with auto-generated values
        if (field.isAnnotatedWith(CreationTimestamp.class)
                || field.isAnnotatedWith(UpdateTimestamp.class)
                || field.isAnnotatedWith(Version.class)
                || field.isAnnotatedWith(Id.class)
                || field.isAnnotatedWith(OneToMany.class)) {
            return false;
        }

        if (field.isAnnotatedWith(NotNullableDbValue.class)) {
            return false;
        }

        if (field.isAnnotatedWith(ManyToOne.class)) {
            Optional<JoinColumn> joinColumn = field.tryGetAnnotationOfType(JoinColumn.class);
            if (joinColumn.isEmpty()) {
                return true;
            }
            return joinColumn.get().nullable();
        }
        Optional<Column> column = field.tryGetAnnotationOfType(Column.class);
        if (column.isEmpty()) {
            return true;
        }
        return column.get().nullable();
    }

    private static boolean hasNullableTypeAnnotation(JavaField javaField) {
        // Check field-level annotation first
        if (javaField.isAnnotatedWith(Nullable.class)) {
            return true;
        }

        // Check TYPE_USE annotation on the field's type using reflection
        try {
            Field reflectField = javaField.reflect();
            AnnotatedType annotatedType = reflectField.getAnnotatedType();
            return annotatedType.isAnnotationPresent(Nullable.class);
        } catch (Exception e) {
            // Fallback: can't reflect, assume no annotation
            return false;
        }
    }

}
