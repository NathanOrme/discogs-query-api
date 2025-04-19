import React from 'react';
import { render, screen } from '@testing-library/react';
import Header from './Header';

test('renders app header', () => {
  render(<Header />);
  expect(screen.getByText(/Discogs Query App/i)).toBeInTheDocument();
});
