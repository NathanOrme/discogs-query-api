import React from 'react';
import { render, screen } from '@testing-library/react';
import QueryResults from './QueryResults';
import { QueryResult } from '../types';

test('renders nothing when results object is empty', () => {
  const queryResult: QueryResult = { results: {} };
  const { container } = render(
    <QueryResults queryResult={queryResult} index={0} />
  );
  expect(container.firstChild).toBeNull();
});

test('renders results section when data is provided', () => {
  const mockEntry = {
    id: '1',
    format: [],
    country: '',
    year: '',
    uri: '',
    numberForSale: 1,
    lowestPrice: 10,
  };
  const queryResult: QueryResult = { results: { Category: [mockEntry] } };
  render(<QueryResults queryResult={queryResult} index={1} />);
  expect(screen.getByText(/Results for Query 2/i)).toBeInTheDocument();
  expect(screen.getByText(/Show Results for Category/i)).toBeInTheDocument();
});
