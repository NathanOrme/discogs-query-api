package org.discogs.query.model.annotations;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.discogs.query.service.utils.NormalizationService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Aspect to apply normalization to fields annotated with @Normalized in records or other classes.
 * This aspect is triggered before the constructor execution to ensure that fields are normalized
 * before the object is fully instantiated.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class NormalizedAspect {

    private final NormalizationService normalizationService;

    /**
     * Applies normalization to fields annotated with @Normalized and of type String.
     * This method is executed before the constructor of the target object.
     *
     * @param obj the object being constructed
     * @throws IllegalAccessException if the field cannot be accessed
     */
    @Before("execution(* *.new(..)) && target(obj)")
    public void normalizeFields(final Object obj) throws IllegalAccessException {
        for (final Field field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Normalized.class) && field.getType() == String.class) {
                field.setAccessible(true);
                String value = (String) field.get(obj);
                if (value != null) {
                    field.set(obj, normalizationService.normalizeString(value));
                }
            }
        }
    }
}
