package com.workeando.plataform.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class MaxCategoriasValidator implements ConstraintValidator<MaxCategorias, List<Categoria>> {

    private int max;

    @Override
    public void initialize(MaxCategorias constraintAnnotation) {
        max = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(List<Categoria> value, ConstraintValidatorContext context) {
        if (value == null) return true; // permitido si está vacío (otra validación lo controla)
        return value.size() <= max;
    }
}
