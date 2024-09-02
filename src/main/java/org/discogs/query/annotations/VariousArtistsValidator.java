package org.discogs.query.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.enums.DiscogsVarious;
import org.discogs.query.model.DiscogsQueryDTO;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class VariousArtistsValidator implements ConstraintValidator<VariousArtistsValidation, DiscogsQueryDTO> {

    @Override
    public boolean isValid(final DiscogsQueryDTO discogsQueryDTO, final ConstraintValidatorContext constraintValidatorContext) {
        log.info("Validating DiscogsQueryDTO for Various Artists scenario");
        if (discogsQueryDTO == null) {
            log.info("DTO Supplied to validator is null");
            return true; // or false, depending on your requirements
        }
        if (Arrays.stream(DiscogsVarious.values())
                .map(DiscogsVarious::getVariousName)
                .anyMatch(value -> value.equalsIgnoreCase(discogsQueryDTO.getArtist()))) {
            log.error("Invalid discogsQueryDTO supplied - Various Artists MUST be supplied with a track or album");
            return isNotBlankBlank(discogsQueryDTO.getTrack()) && isNotBlankBlank(discogsQueryDTO.getAlbum());
        }
        return true;
    }


    private boolean isNotBlankBlank(final String string) {
        return string != null && !string.isBlank();
    }
}
