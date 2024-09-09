                                                import { createQueryFields } from './../modules/queryFields.js'; // Adjust the path if needed
                                                import { discogsTypes, discogFormats, discogCountries } from './../modules/discogsData.js'; // Adjust the path if needed

                                                describe('createQueryFields', () => {
                                                  let queriesContainer;
                                                  let queryCounter;

                                                  beforeEach(() => {
                                                    // Set up the DOM structure
                                                    document.body.innerHTML = `
                                                      <div id="queriesContainer"></div>
                                                      <form>
                                                        <button type="submit">Submit</button>
                                                      </form>
                                                    `;
                                                    queriesContainer = document.getElementById('queriesContainer');
                                                    queryCounter = 1; // Initialize the query counter
                                                  });

                                                  test('should create a new query field with correct elements and content', () => {
                                                    queryCounter = createQueryFields(queriesContainer, queryCounter);

                                                    const queryDiv = queriesContainer.querySelector('.query');
                                                    expect(queryDiv).toBeInTheDocument();

                                                    const queryHeader = queryDiv.querySelector('.query-header');
                                                    expect(queryHeader).toBeInTheDocument();

                                                    const queryTitle = queryHeader.querySelector('span');
                                                    expect(queryTitle.textContent).toBe('Query 1');

                                                    const deleteButton = queryHeader.querySelector('.delete-button');
                                                    expect(deleteButton).toBeInTheDocument();

                                                    const queryContent = queryDiv.querySelector('.query-content');
                                                    expect(queryContent).toBeInTheDocument();

                                                    // Check if the select options are correctly populated
                                                    const formatSelect = queryContent.querySelector('.format');
                                                    const countrySelect = queryContent.querySelector('.country');
                                                    const typesSelect = queryContent.querySelector('.types');

                                                    expect(formatSelect.innerHTML).toContain(discogFormats.map(f => `<option value="${f.value}">${f.text}</option>`).join(""));
                                                    expect(countrySelect.innerHTML).toContain(discogCountries.map(c => `<option value="${c.value}">${c.text}</option>`).join(""));
                                                    expect(typesSelect.innerHTML).toContain(discogsTypes.map(t => `<option value="${t.value}">${t.text}</option>`).join(""));
                                                  });

                                                  test('should toggle query content visibility when header is clicked', () => {
                                                    createQueryFields(queriesContainer, queryCounter);
                                                    const queryDiv = queriesContainer.querySelector('.query');
                                                    const queryHeader = queryDiv.querySelector('.query-header');
                                                    const queryContent = queryDiv.querySelector('.query-content');

                                                    expect(queryContent.classList.contains('hidden')).toBe(false);

                                                    queryHeader.click();
                                                    expect(queryContent.classList.contains('hidden')).toBe(true);

                                                    queryHeader.click();
                                                    expect(queryContent.classList.contains('hidden')).toBe(false);
                                                  });

                                                  test('should remove query field when delete button is clicked', () => {
                                                    createQueryFields(queriesContainer, queryCounter);
                                                    const queryDiv = queriesContainer.querySelector('.query');
                                                    const deleteButton = queryDiv.querySelector('.delete-button');

                                                    deleteButton.click();
                                                    expect(queryDiv).not.toBeInTheDocument();
                                                  });

                                                  test('should show alert if both artist and barcode are empty and submit is clicked', () => {
                                                    createQueryFields(queriesContainer, queryCounter);
                                                    const queryDiv = queriesContainer.querySelector('.query');
                                                    const artistInput = queryDiv.querySelector('.artist');
                                                    const barcodeInput = queryDiv.querySelector('.barcode');
                                                    const submitButton = document.querySelector('form button[type="submit"]');
                                                    const alertMock = jest.spyOn(window, 'alert').mockImplementation(() => {});

                                                    // Set both artist and barcode to empty
                                                    artistInput.value = '';
                                                    barcodeInput.value = '';

                                                    submitButton.click();
                                                    expect(alertMock).toHaveBeenCalledWith('Please provide either an artist name or a barcode.');

                                                    alertMock.mockRestore(); // Clean up the mock
                                                  });
                                                });
