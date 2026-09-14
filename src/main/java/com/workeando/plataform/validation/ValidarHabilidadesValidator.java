package com.workeando.plataform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ValidarHabilidadesValidator implements ConstraintValidator<ValidarHabilidades, List<String>> {

    @Override
    public boolean isValid(List<String> habilidades, ConstraintValidatorContext context) {
        return habilidades != null && habilidades.size() >= 1 && habilidades.size() <= 4;
    }
}