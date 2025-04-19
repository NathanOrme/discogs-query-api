import React from 'react';
import { render, screen } from '@testing-library/react';
import Results from './Results';

test('renders no results message when response is empty', () => {
  render(<Results response={[]} />);
  expect(screen.getByText(/No results found\./i)).toBeInTheDocument();
});

test('renders results and export button when response has data', () => {
  const mockEntry = { id: '1', format: [], country: '', year: '', uri: '', numberForSale: 1, lowestPrice: 10 };
  const response = [{ results: { Category: [mockEntry] } }];
  render(<Results response={response} />);
  expect(screen.getByRole('button', { name: /Export Results to JSON/i })).toBeInTheDocument();
  expect(screen.getByText(/Results for Query 1/i)).toBeInTheDocument();
});
