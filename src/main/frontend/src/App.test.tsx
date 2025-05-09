import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';

// Mock the child components to simplify testing
jest.mock('./modules/HeroBanner', () => () => (
  <div data-testid="hero-banner">Hero Banner</div>
));
jest.mock('./modules/InstructionAccordion', () => () => (
  <div data-testid="instruction-accordion">Instructions</div>
));
jest.mock('./modules/QueryFields', () => ({ onQueriesChange }: any) => (
  <div data-testid="query-fields">Query Fields</div>
));
jest.mock(
  './modules/SearchForm',
  () =>
    ({ queries, setResponse, onCheapestItemsChange, onStepComplete }: any) => (
      <div data-testid="search-form">Search Form</div>
    )
);
jest.mock('./modules/Results', () => ({ response }: any) => (
  <div data-testid="results">Results</div>
));
jest.mock('./modules/CheapestItem', () => ({ items }: any) => (
  <div data-testid="cheapest-item">Cheapest Item</div>
));

describe('App', () => {
  it('renders the HeroBanner component', () => {
    render(<App />);
    expect(screen.getByTestId('hero-banner')).toBeInTheDocument();
  });

  it('renders the InstructionAccordion component', () => {
    render(<App />);
    expect(screen.getByTestId('instruction-accordion')).toBeInTheDocument();
  });

  it('renders the stepper with all steps', () => {
    render(<App />);
    expect(screen.getByText('Queries')).toBeInTheDocument();
    expect(screen.getByText('Search')).toBeInTheDocument();
    expect(screen.getByText('Results')).toBeInTheDocument();
    expect(screen.getByText('Cheapest Items')).toBeInTheDocument();
  });

  it('initially renders the QueryFields component', () => {
    render(<App />);
    expect(screen.getByTestId('query-fields')).toBeInTheDocument();
    expect(screen.queryByTestId('search-form')).not.toBeInTheDocument();
  });

  it('navigates to the next step when Next button is clicked', () => {
    render(<App />);

    // Initially on QueryFields
    expect(screen.getByTestId('query-fields')).toBeInTheDocument();

    // Click Next
    fireEvent.click(screen.getByText('Next'));

    // Should now show SearchForm
    expect(screen.queryByTestId('query-fields')).not.toBeInTheDocument();
    expect(screen.getByTestId('search-form')).toBeInTheDocument();
  });

  it('navigates back to the previous step when Back button is clicked', () => {
    render(<App />);

    // Go to step 1 (SearchForm)
    fireEvent.click(screen.getByText('Next'));
    expect(screen.getByTestId('search-form')).toBeInTheDocument();

    // Click Back
    fireEvent.click(screen.getByText('Back'));

    // Should now show QueryFields again
    expect(screen.getByTestId('query-fields')).toBeInTheDocument();
    expect(screen.queryByTestId('search-form')).not.toBeInTheDocument();
  });

  it('disables the Back button on the first step', () => {
    render(<App />);
    const backButton = screen.getByText('Back');
    expect(backButton).toBeDisabled();
  });

  it('disables the Next button on the last step', async () => {
    render(<App />);

    // Navigate to the last step
    fireEvent.click(screen.getByText('Next')); // Step 1
    fireEvent.click(screen.getByText('Next')); // Step 2
    fireEvent.click(screen.getByText('Next')); // Step 3

    // Check that Next button is disabled
    const nextButton = screen.getByText('Next');
    expect(nextButton).toBeDisabled();
  });
});
