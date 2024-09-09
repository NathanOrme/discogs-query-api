import { displayResults, displayError } from './resultsHandler';

// Mock DOM elements
document.body.innerHTML = `
  <div id="results"></div>
`;

test('displays results correctly', () => {
  const response = [
    {
      results: [
        {
          title: 'Test Title',
          id: '123',
          format: ['Vinyl'],
          country: 'US',
          year: '2020',
          uri: 'http://example.com',
          numberForSale: 5,
          lowestPrice: 10.5
        }
      ]
    }
  ];

  displayResults(response);

  // Check if results are displayed
  const resultsContainer = document.getElementById('results');
  expect(resultsContainer).not.toBeNull();
  expect(resultsContainer.querySelector('h2').textContent).toBe('Results for Query 1');
  expect(resultsContainer.querySelector('.result-item')).not.toBeNull();
  expect(resultsContainer.querySelector('.result-item').querySelector('h3').textContent).toBe('Test Title');
});

test('displays an error message if no results are found', () => {
  displayResults([]);

  const resultsContainer = document.getElementById('results');
  expect(resultsContainer).not.toBeNull();
  expect(resultsContainer.querySelector('.error-message').textContent).toBe('No results found.');
});
