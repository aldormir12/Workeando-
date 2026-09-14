package com.workeando.plataform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdiomaValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Idioma {
    String message() default "Debes seleccionar al menos un idioma";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
