import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders header and next button', () => {
  render(<App />);
  expect(screen.getByText(/Discogs Query App/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Next/i })).toBeInTheDocument();
});
