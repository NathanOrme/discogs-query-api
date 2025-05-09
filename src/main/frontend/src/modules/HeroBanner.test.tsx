import React from 'react';
import { render, screen } from '@testing-library/react';
import HeroBanner from './HeroBanner';

describe('HeroBanner', () => {
  it('renders the main heading correctly', () => {
    render(<HeroBanner />);
    const headingElement = screen.getByText('Discogs Query');
    expect(headingElement).toBeInTheDocument();
  });

  it('renders the subheading correctly', () => {
    render(<HeroBanner />);
    const subheadingElement = screen.getByText(
      'Search and explore vinyl releases effortlessly'
    );
    expect(subheadingElement).toBeInTheDocument();
  });

  it('renders with the correct structure', () => {
    const { container } = render(<HeroBanner />);
    // Check that we have the Box component with Typography children
    expect(container.firstChild).toBeInTheDocument();
    expect(screen.getAllByRole('heading').length).toBe(2);
  });
});
