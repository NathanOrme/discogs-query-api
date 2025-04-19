import React from 'react';
import { render, screen } from '@testing-library/react';
import CheapestItem from './CheapestItem';

test('renders header and no items message when items array is empty', () => {
  render(<CheapestItem items={[]} />);
  expect(screen.getByText(/Cheapest Item for Each Query/i)).toBeInTheDocument();
  expect(screen.getByText(/No items found\./i)).toBeInTheDocument();
});

test('renders item details when items array has data', () => {
  const items = [
    {
      title: 'Test Item',
      lowestPrice: 5,
      numberForSale: 10,
      country: 'UK',
      uri: 'http://example.com',
    },
  ];
  render(<CheapestItem items={items} />);
  expect(screen.getByText(/Test Item/i)).toBeInTheDocument();
  expect(screen.getByText(/Price:/i)).toBeInTheDocument();
  expect(screen.getByText(/£5\.00/i)).toBeInTheDocument();
  expect(screen.getByText(/Number For Sale:/i)).toBeInTheDocument();
});
