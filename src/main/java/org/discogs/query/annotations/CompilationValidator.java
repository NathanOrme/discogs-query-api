package org.discogs.query.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogsFormats;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Component;

/**
 * Validator for ensuring that DiscogsQueryDTO is
 * properly validated when the artist is one of the "Various Artists".
 * This validator checks if the provided DTO meets
 * the necessary criteria based on the presence of certain fields.
 */
@Slf4j
@Component
public class CompilationValidator
        implements ConstraintValidator<CompilationValidation, DiscogsQueryDTO> {

    private boolean isNotBlankBlank(final String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Validates the provided DiscogsQueryDTO based on the context of
     * a compilation album. If a track hasn't been supplied or an album,
     * then reject it entirely.
     *
     * @param discogsQueryDTO            the DiscogsQueryDTO object to be validated
     * @param constraintValidatorContext the context in which the constraint is evaluated
     * @return whether it matches the criteria or not
     */
    @Override
    public boolean isValid(final DiscogsQueryDTO discogsQueryDTO,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (!discogsQueryDTO.getFormat().equalsIgnoreCase(DiscogsFormats.COMP.getFormat())
                && !discogsQueryDTO.getFormat()
                .equalsIgnoreCase(DiscogsFormats.VINYL_COMPILATION.getFormat())) {
            return true;
        }
        if (isNotBlankBlank(discogsQueryDTO.getTrack())) {
            return true;
        }
        return isNotBlankBlank(discogsQueryDTO.getAlbum());

    }
}
