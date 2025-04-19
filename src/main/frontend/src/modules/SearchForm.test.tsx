import React from 'react';
import { render, screen } from '@testing-library/react';
import SearchForm from './SearchForm';

const mockQueries = [{ artist: 'Test Artist', barcode: '12345' }];
const mockSetResponse = jest.fn();
const mockOnCheapestItemsChange = jest.fn();

// Since SearchForm relies on window.location.hostname, mock it
Object.defineProperty(window, 'location', {
  value: { hostname: 'localhost' },
  writable: true,
});

test('renders search form and search button', () => {
  render(
    <SearchForm
      queries={mockQueries}
      setResponse={mockSetResponse}
      onCheapestItemsChange={mockOnCheapestItemsChange}
    />
  );
  expect(
    screen.getByRole('textbox', { name: /Username/i })
  ).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Search/i })).toBeInTheDocument();
});
