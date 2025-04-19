import React from 'react';
import { render, screen } from '@testing-library/react';
import EntryComponent, { Entry } from './Entry';

test('renders entry details', () => {
  const entry: Entry = {
    id: '1',
    title: 'Title',
    format: ['LP'],
    country: 'USA',
    year: '2020',
    uri: 'http://example.com',
    numberForSale: 5,
    lowestPrice: 20,
  };
  render(<EntryComponent entry={entry} />);
  expect(screen.getByText(/Title/i)).toBeInTheDocument();
  expect(screen.getByText(/USA/i)).toBeInTheDocument();
  expect(screen.getByText(/2020/i)).toBeInTheDocument();
  expect(screen.getByText(/LP/i)).toBeInTheDocument();
});
