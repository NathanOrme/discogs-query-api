import React from 'react';
import { render, screen } from '@testing-library/react';
import QueryFields from './QueryFields';

test('renders initial query field and add query button', () => {
  const mockOnQueriesChange = jest.fn();
  render(<QueryFields onQueriesChange={mockOnQueriesChange} />);
  expect(screen.getByText(/Query 1/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Add Another Query/i })).toBeInTheDocument();
});
