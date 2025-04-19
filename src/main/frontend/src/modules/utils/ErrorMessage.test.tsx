import React from 'react';
import { render, screen } from '@testing-library/react';
import ErrorMessage from './ErrorMessage';

test('renders error message', () => {
  const message = 'Test error message';
  render(<ErrorMessage message={message} />);
  expect(screen.getByText(message)).toBeInTheDocument();
});
