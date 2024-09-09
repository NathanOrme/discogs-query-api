import { createQueryFields } from './queryFields';

describe('createQueryFields', () => {
  let queriesContainer;

  beforeEach(() => {
    // Set up a container for the query fields
    queriesContainer = document.createElement('div');
    queriesContainer.id = 'queriesContainer';
    document.body.appendChild(queriesContainer);
  });

  afterEach(() => {
    // Clean up after each test
    queriesContainer.remove();
  });

  test('creates and appends a query field correctly', () => {
    const initialQueryCount = document.querySelectorAll('.query').length;
    const newQueryCounter = createQueryFields(queriesContainer, initialQueryCount);

    // Check if the new query field was created
    const queryDivs = document.querySelectorAll('.query');
    expect(queryDivs.length).toBe(initialQueryCount + 1);

    const newQueryDiv = queryDivs[queryDivs.length - 1];

    // Verify the query header
    const queryHeader = newQueryDiv.querySelector('.query-header');
    expect(queryHeader).not.toBeNull();
    expect(queryHeader.textContent).toContain(`Query ${newQueryCounter}`);

    // Check if the query content is present
    const queryContent = newQueryDiv.querySelector('.query-content');
    expect(queryContent).not.toBeNull();
    expect(queryContent.querySelectorAll('input').length).toBe(4); // Artist, Barcode, Album, Track
    expect(queryContent.querySelectorAll('select').length).toBe(3); // Format, Country, Types
  });

  test('validates artist or barcode before submission', () => {
    // Add a query field
    createQueryFields(queriesContainer, 0);

    const submitButton = document.querySelector('form button[type="submit"]');
    submitButton.click();

    // Expect alert to be called (you can mock alert if needed)
    expect(window.alert).toHaveBeenCalledWith("Please provide either an artist name or a barcode.");
  });
});
