package com.workeando.plataform.model;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxCategoriasValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxCategorias {
    int value(); // número máximo permitido
    String message() default "Se han seleccionado demasiadas categorías";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
