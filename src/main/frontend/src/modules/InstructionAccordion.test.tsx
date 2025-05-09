import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import InstructionAccordion from './InstructionAccordion';

describe('InstructionAccordion', () => {
  it('renders the main accordion title', () => {
    render(<InstructionAccordion />);
    const titleElement = screen.getByText('How to Use This Search Tool');
    expect(titleElement).toBeInTheDocument();
  });

  it('contains all the expected steps', () => {
    render(<InstructionAccordion />);
    
    // Check that all step labels are present
    expect(screen.getByText('Barcode')).toBeInTheDocument();
    expect(screen.getByText('Artist')).toBeInTheDocument();
    expect(screen.getByText('Track (optional)')).toBeInTheDocument();
    expect(screen.getByText('Album (optional)')).toBeInTheDocument();
    expect(screen.getByText('Format (optional)')).toBeInTheDocument();
    expect(screen.getByText('Add Query')).toBeInTheDocument();
    expect(screen.getByText('Username (optional)')).toBeInTheDocument();
    expect(screen.getByText('Search')).toBeInTheDocument();
  });

  it('expands the main accordion when clicked', () => {
    render(<InstructionAccordion />);
    
    // Initially, step descriptions should not be visible
    expect(screen.queryByText('Enter the barcode for the product you are searching for.')).not.toBeVisible();
    
    // Click the main accordion
    const mainAccordion = screen.getByText('How to Use This Search Tool');
    fireEvent.click(mainAccordion);
    
    // Now the step labels should be visible
    expect(screen.getByText('Barcode')).toBeVisible();
  });

  it('expands a step accordion when clicked', () => {
    render(<InstructionAccordion />);
    
    // First expand the main accordion
    const mainAccordion = screen.getByText('How to Use This Search Tool');
    fireEvent.click(mainAccordion);
    
    // Click on a specific step
    const barcodeStep = screen.getByText('Barcode');
    fireEvent.click(barcodeStep);
    
    // The description should now be visible
    expect(screen.getByText('Enter the barcode for the product you are searching for.')).toBeVisible();
  });
});
