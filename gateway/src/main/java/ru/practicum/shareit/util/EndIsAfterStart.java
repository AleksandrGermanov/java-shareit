package ru.practicum.shareit.util;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingEndFieldValidator.class)
public @interface EndIsAfterStart {
    String message() default "Время окончания бронирования должно быть позже времени его начала.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
