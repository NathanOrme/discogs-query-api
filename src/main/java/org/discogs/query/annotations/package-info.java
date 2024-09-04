/**
 * Contains annotations and validators used for validating
 * {@link org.discogs.query.model.DiscogsQueryDTO} objects
 * based on specific criteria related to "Various Artists" and
 * compilation scenarios.
 *
 * <p>This package includes:
 * <ul>
 *   <li>{@link CompilationValidation} - An annotation used to ensure
 *       that for compilation albums, both track and album fields are supplied
 *       in the DTO.</li>
 *   <li>{@link CompilationValidator} - Validator for the
 *       {@link CompilationValidation} annotation that performs
 *       validation logic to ensure the presence of necessary fields.</li>
 *   <li>{@link VariousArtistsValidation} - An annotation used to validate
 *       DTOs when the artist is classified as "Various Artists", requiring
 *       the presence of either a track or an album.</li>
 *   <li>{@link VariousArtistsValidator} - Validator for the
 *       {@link VariousArtistsValidation} annotation that checks if the
 *       DTO meets the criteria for "Various Artists" scenarios.</li>
 * </ul>
 *
 * <p>
 *     The annotations and validators are designed to enforce
 * the consistency and correctness of DTOs based on different
 * validation rules related to discography queries.
 */
package org.discogs.query.annotations;
