package org.discogs.query.model.annotations;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.discogs.query.service.NormalizationService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Aspect to apply normalization to fields annotated with @Normalize in records or other classes.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class NormalizedAspect {

    private final NormalizationService normalizationService;

    @Before("execution(* *.new(..))")
    public void normalizeFields(final Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (final Field field : fields) {
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
