import { createQueryFields } from './queryFields';
import { discogsTypes, discogFormats, discogCountries } from './discogsData';

describe('createQueryFields', () => {
  let queriesContainer;

  beforeEach(() => {
    document.body.innerHTML = '<div id="queriesContainer"></div>';
    queriesContainer = document.getElementById('queriesContainer');
  });

  test('should create a query field with correct elements', () => {
    const queryCounter = createQueryFields(queriesContainer, 1);

    expect(queriesContainer.children.length).toBe(1);
    const queryDiv = queriesContainer.children[0];
    expect(queryDiv).toHaveClass('query');

    const queryHeader = queryDiv.querySelector('.query-header');
    expect(queryHeader).toBeInTheDocument();

    const deleteButton = queryHeader.querySelector('.delete-button');
    expect(deleteButton).toBeInTheDocument();

    const queryContent = queryDiv.querySelector('.query-content');
    expect(queryContent).toBeInTheDocument();

    const formatSelect = queryContent.querySelector('select.format');
    const countrySelect = queryContent.querySelector('select.country');
    const typesSelect = queryContent.querySelector('select.types');

    expect(formatSelect.children.length).toBe(discogFormats.length + 1); // Including default option
    expect(countrySelect.children.length).toBe(discogCountries.length + 1); // Including default option
    expect(typesSelect.children.length).toBe(discogsTypes.length + 1); // Including default option
  });

  test('should prevent form submission if artist and barcode are empty', () => {
    const queryCounter = createQueryFields(queriesContainer, 1);
    const submitButton = document.createElement('button');
    submitButton.setAttribute('type', 'submit');
    document.body.appendChild(submitButton);

    const artistInput = queriesContainer.querySelector('.artist');
    const barcodeInput = queriesContainer.querySelector('.barcode');
    artistInput.value = '';
    barcodeInput.value = '';

    const event = new Event('click');
    const preventDefault = jest.fn();
    submitButton.addEventListener('click', (e) => {
      e.preventDefault = preventDefault;
    });

    submitButton.click();
    expect(preventDefault).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Please provide either an artist name or a barcode.');
  });
});
