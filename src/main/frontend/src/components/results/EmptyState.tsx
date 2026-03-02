/**
 * @file EmptyState.tsx
 * @description Empty state variations for search results with example search suggestions
 */

import { Button } from '@/components/ui';
import { cn } from '@/lib/utils';
import { AlertCircle, Disc, Search } from 'lucide-react';
import type { ReactNode } from 'react';

export type EmptyStateVariant = 'initial' | 'no-results' | 'error';

interface ExampleSearch {
  label: string;
  query: string;
}

interface EmptyStateProps {
  variant: EmptyStateVariant;
  errorMessage?: string;
  onExampleSearchClick?: (query: string) => void;
  onRetry?: () => void;
  className?: string;
}

const EXAMPLE_SEARCHES: ExampleSearch[] = [
  {
    label: 'Miles Davis - Kind of Blue',
    query: 'artist:Miles Davis album:Kind of Blue',
  },
  { label: 'Daft Punk on Vinyl', query: 'artist:Daft Punk format:Vinyl' },
  {
    label: '90s Hip-Hop Albums',
    query: 'genre:Hip Hop format:Album year:1990-1999',
  },
  {
    label: 'UK Electronic EPs',
    query: 'genre:Electronic format:EP country:UK',
  },
  { label: 'Blue Note Records', query: 'label:Blue Note' },
];

export function EmptyState({
  variant,
  errorMessage,
  onExampleSearchClick,
  onRetry,
  className,
}: EmptyStateProps) {
  const renderContent = (): ReactNode => {
    switch (variant) {
      case 'initial':
        return <InitialState onExampleSearchClick={onExampleSearchClick} />;
      case 'no-results':
        return <NoResultsState onExampleSearchClick={onExampleSearchClick} />;
      case 'error':
        return <ErrorState message={errorMessage} onRetry={onRetry} />;
    }
  };

  return (
    <div
      className={cn(
        'flex flex-col items-center justify-center',
        'py-16 px-4 text-center',
        className
      )}
      role="status"
      aria-live="polite"
    >
      {renderContent()}
    </div>
  );
}

function InitialState({
  onExampleSearchClick,
}: Pick<EmptyStateProps, 'onExampleSearchClick'>) {
  return (
    <>
      <div className="relative mb-6">
        <div className="w-24 h-24 rounded-full bg-gradient-to-br from-discogs-primary/20 to-discogs-primary/5 flex items-center justify-center">
          <Disc className="w-12 h-12 text-discogs-primary" />
        </div>
        <div className="absolute inset-0 bg-discogs-primary/10 blur-2xl rounded-full" />
      </div>

      <h3 className="text-2xl font-bold text-white mb-2">
        Start Your Vinyl Journey
      </h3>
      <p className="text-white/70 mb-8 max-w-md">
        Search millions of releases from the world's largest music database. Try
        an example search below or create your own.
      </p>

      <div className="space-y-3">
        <p className="text-sm font-semibold text-white/60 uppercase tracking-wide">
          Popular Searches
        </p>
        <div className="flex flex-wrap gap-2 justify-center max-w-2xl">
          {EXAMPLE_SEARCHES.map((example) => (
            <button
              key={example.label}
              onClick={() => onExampleSearchClick?.(example.query)}
              className={cn(
                'discogs-chip',
                'text-white hover:text-discogs-primary',
                'transition-all duration-200'
              )}
              aria-label={`Search for ${example.label}`}
            >
              <Search className="w-4 h-4" />
              <span>{example.label}</span>
            </button>
          ))}
        </div>
      </div>

      <div className="mt-8 p-4 rounded-lg bg-white/5 border border-white/10 max-w-lg">
        <p className="text-sm text-white/60">
          <strong className="text-white/80">Pro tip:</strong> Use filters like
          artist, format, year, and label to narrow down your search.
        </p>
      </div>
    </>
  );
}

function NoResultsState({
  onExampleSearchClick,
}: Pick<EmptyStateProps, 'onExampleSearchClick'>) {
  return (
    <>
      <div className="relative mb-6">
        <div className="w-24 h-24 rounded-full bg-gradient-to-br from-white/10 to-white/5 flex items-center justify-center">
          <Search className="w-12 h-12 text-white/40" />
        </div>
      </div>

      <h3 className="text-2xl font-bold text-white mb-2">No Results Found</h3>
      <p className="text-white/70 mb-6 max-w-md">
        We couldn't find any releases matching your search criteria. Try
        adjusting your filters or broadening your search.
      </p>

      <div className="mb-8 p-6 rounded-lg bg-white/5 border border-white/10 max-w-lg text-left">
        <h4 className="font-semibold text-white mb-3">Search Tips:</h4>
        <ul className="space-y-2 text-sm text-white/70">
          <li className="flex items-start gap-2">
            <span className="text-discogs-primary mt-0.5">•</span>
            <span>Try removing some filters to broaden results</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-discogs-primary mt-0.5">•</span>
            <span>Check spelling of artist or album names</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-discogs-primary mt-0.5">•</span>
            <span>Use partial names (e.g., "Daft" instead of "Daft Punk")</span>
          </li>
          <li className="flex items-start gap-2">
            <span className="text-discogs-primary mt-0.5">•</span>
            <span>Try different format types (Vinyl, CD, Cassette)</span>
          </li>
        </ul>
      </div>

      <div className="space-y-3">
        <p className="text-sm font-semibold text-white/60 uppercase tracking-wide">
          Try These Instead
        </p>
        <div className="flex flex-wrap gap-2 justify-center max-w-2xl">
          {EXAMPLE_SEARCHES.slice(0, 3).map((example) => (
            <button
              key={example.label}
              onClick={() => onExampleSearchClick?.(example.query)}
              className={cn(
                'discogs-chip',
                'text-white hover:text-discogs-primary'
              )}
              aria-label={`Try searching for ${example.label}`}
            >
              <Search className="w-4 h-4" />
              <span>{example.label}</span>
            </button>
          ))}
        </div>
      </div>
    </>
  );
}

function ErrorState({
  message,
  onRetry,
}: Pick<EmptyStateProps, 'errorMessage' | 'onRetry'> & { message?: string }) {
  return (
    <>
      <div className="relative mb-6">
        <div className="w-24 h-24 rounded-full bg-gradient-to-br from-red-500/20 to-red-500/5 flex items-center justify-center">
          <AlertCircle className="w-12 h-12 text-red-400" />
        </div>
        <div className="absolute inset-0 bg-red-500/10 blur-2xl rounded-full" />
      </div>

      <h3 className="text-2xl font-bold text-white mb-2">Search Error</h3>
      <p className="text-white/70 mb-6 max-w-md">
        {message || 'An error occurred while searching. Please try again.'}
      </p>

      <div className="mb-8 p-4 rounded-lg bg-red-500/10 border border-red-500/20 max-w-lg">
        <p className="text-sm text-red-200">
          If this problem persists, check your internet connection or try again
          later.
        </p>
      </div>

      {onRetry && (
        <Button
          onClick={onRetry}
          size="lg"
          className="bg-discogs-primary hover:bg-discogs-primary-dark text-white"
        >
          <Search className="w-4 h-4 mr-2" />
          Try Again
        </Button>
      )}
    </>
  );
}
