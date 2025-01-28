package org.discogs.query.model.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.discogs.query.model.DiscogsQueryDTO;
import org.discogs.query.model.enums.DiscogsFormats;
import org.springframework.stereotype.Component;

/**
 * Validator for ensuring that DiscogsQueryDTO is properly validated when the artist is one of the
 * "Various Artists". This validator checks if the provided DTO meets the necessary criteria based
 * on the presence of certain fields.
 */
@Slf4j
@Component
public class CompilationValidator
    implements ConstraintValidator<CompilationValidation, DiscogsQueryDTO> {

  private boolean isFormatCompilation(final DiscogsQueryDTO dto) {
    return DiscogsFormats.COMP.getFormat().equalsIgnoreCase(dto.format())
        || DiscogsFormats.VINYL_COMPILATION.getFormat().equalsIgnoreCase(dto.format());
  }

  private boolean isFieldNotBlank(final String field) {
    return field != null && !field.isBlank();
  }

  /**
   * Validates the provided DiscogsQueryDTO based on the context of a compilation album. If the
   * format is not a compilation, it passes validation. Otherwise, it ensures either a track or
   * album field is provided.
   *
   * @param discogsQueryDTO the DiscogsQueryDTO object to be validated
   * @param context the context in which the constraint is evaluated
   * @return whether it matches the criteria or not
   */
  @Override
  public boolean isValid(
      final DiscogsQueryDTO discogsQueryDTO, final ConstraintValidatorContext context) {
    if (!isFormatCompilation(discogsQueryDTO)) {
      return true;
    }
    return isFieldNotBlank(discogsQueryDTO.track()) || isFieldNotBlank(discogsQueryDTO.album());
  }
}
