package com.workeando.plataform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidarHabilidadesValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidarHabilidades {
    String message() default "Debes seleccionar entre 1 y 4 habilidades técnicas";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
