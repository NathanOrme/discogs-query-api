package org.discogs.query.model.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogsFormats;
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

    private static boolean isFormatBlankOrNotCompilation(final DiscogsQueryDTO discogsQueryDTO) {
        if (discogsQueryDTO.getFormat() == null) {
            return true;
        }
        return !discogsQueryDTO.getFormat().equalsIgnoreCase(DiscogsFormats.COMP.getFormat())
                && !discogsQueryDTO.getFormat()
                .equalsIgnoreCase(DiscogsFormats.VINYL_COMPILATION.getFormat());
    }

    private boolean isNotBlankBlank(final String string) {
        return string != null && !string.isBlank();
    }

    /**
     * Validates the provided DiscogsQueryDTO based on the context of
     * a compilation album. If a track hasn't been supplied or an album,
     * then reject it entirely.
     *
     * @param discogsQueryDTO            the DiscogsQueryDTO object to be
     *                                   validated
     * @param constraintValidatorContext the context in which the constraint
     *                                   is evaluated
     * @return whether it matches the criteria or not
     */
    @Override
    public boolean isValid(final DiscogsQueryDTO discogsQueryDTO,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (isFormatBlankOrNotCompilation(discogsQueryDTO)) {
            return true;
        }
        if (isNotBlankBlank(discogsQueryDTO.getTrack())) {
            return true;
        }
        return isNotBlankBlank(discogsQueryDTO.getAlbum());

    }
}
