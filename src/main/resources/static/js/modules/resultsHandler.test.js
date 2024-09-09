import { displayResults, displayError } from './resultsHandler';
import '@testing-library/jest-dom/extend-expect';  // for additional matchers like `toBeInTheDocument`

// Mock DOM elements
document.body.innerHTML = `
  <div id="results"></div>
`;

describe('displayResults', () => {
  beforeEach(() => {
    document.getElementById('results').innerHTML = '';
  });

  it('should display error when no results', () => {
    displayResults([]);
    expect(document.getElementById('results').textContent).toContain('No results found.');
  });

  it('should display results correctly', () => {
    const response = [
      {
        results: [
          {
            title: 'Test Album',
            id: '12345',
            format: ['Vinyl'],
            country: 'US',
            year: '2020',
            uri: 'http://example.com',
            numberForSale: 5,
            lowestPrice: 9.99
          }
        ]
      }
    ];

    displayResults(response);

    expect(document.querySelector('.result-item')).toBeInTheDocument();
    expect(document.querySelector('h3').textContent).toBe('Test Album');
  });
});

describe('displayError', () => {
  it('should display an error message', () => {
    displayError('Test error message');
    expect(document.getElementById('results').textContent).toContain('Test error message');
  });
});
