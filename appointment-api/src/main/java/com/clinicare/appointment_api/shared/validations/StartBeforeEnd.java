package com.clinicare.appointment_api.shared.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
@Documented
public @interface StartBeforeEnd {
    String message() default "A data de término deve ser posterior à data de início.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}