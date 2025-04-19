import React from 'react';
import { render, screen } from '@testing-library/react';
import InstructionAccordion from './InstructionAccordion';

test('renders accordion with steps', () => {
  render(<InstructionAccordion />);
  expect(screen.getByText(/How to Use This Search Tool/i)).toBeInTheDocument();
  expect(screen.getByText(/Barcode/i)).toBeInTheDocument();
});
