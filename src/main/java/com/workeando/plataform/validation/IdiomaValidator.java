package com.workeando.plataform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class IdiomaValidator implements ConstraintValidator<Idioma, List<String>> {

    @Override
    public boolean isValid(List<String> idiomas, ConstraintValidatorContext context) {
        return idiomas != null && !idiomas.isEmpty();
    }
}
