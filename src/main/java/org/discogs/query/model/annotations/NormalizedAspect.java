package org.discogs.query.model.annotations;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.discogs.query.exceptions.NormalizedException;
import org.discogs.query.service.NormalizationService;
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
     * Intercepts the constructor execution of target objects to normalize fields annotated
     * with @Normalized.
     *
     * @param obj the object being constructed
     */
    @Before("execution(* *.new(..)) && target(obj)")
    public void normalizeFields(final Object obj) {
        for (final Field field : obj.getClass().getDeclaredFields()) {
            if (shouldNormalizeField(field)) {
                normalizeField(field, obj);
            }
        }
    }

    /**
     * Checks if a field is annotated with @Normalized and is of type String.
     *
     * @param field the field to check
     * @return true if the field should be normalized, false otherwise
     */
    private boolean shouldNormalizeField(final Field field) {
        return field.isAnnotationPresent(Normalized.class) && field.getType() == String.class;
    }

    /**
     * Normalizes the value of a given field for the specified object.
     *
     * @param field the field to normalize
     * @param obj   the object containing the field
     */
    private void normalizeField(final Field field, final Object obj) {
        try {
            field.setAccessible(true);
            String value = (String) field.get(obj);
            if (value != null) {
                field.set(obj, normalizationService.normalizeString(value));
            }
        } catch (final IllegalAccessException e) {
            throw new NormalizedException("Failed to normalize field: " + field.getName(), e);
        } finally {
            field.setAccessible(false);
        }
    }
}
