import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

jest.mock('./pages/DiscogsPage', () => () => (
  <div data-testid="discogs-page">Discogs Page</div>
));

describe('App', () => {
  it('renders the DiscogsPage', () => {
    render(<App />);
    expect(screen.getByTestId('discogs-page')).toBeInTheDocument();
  });
});
