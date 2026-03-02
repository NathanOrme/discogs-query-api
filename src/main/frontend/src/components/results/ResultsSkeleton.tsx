/**
 * @file ResultsSkeleton.tsx
 * @description Loading skeleton for search results with vinyl spinner and rotating messages
 */

import { MagicCard } from '@/components/magicui';
import { Skeleton } from '@/components/ui/skeleton';
import { cn } from '@/lib/utils';
import { Disc } from 'lucide-react';
import { useEffect, useState } from 'react';

const LOADING_MESSAGES = [
  'jazz classics',
  'rare vinyl pressings',
  'electronic gems',
  'rock anthems',
  'hip-hop treasures',
  'indie discoveries',
  'world music',
  'experimental sounds',
] as const;

interface ResultsSkeletonProps {
  count?: number;
  className?: string;
}

export function ResultsSkeleton({ count = 6, className }: ResultsSkeletonProps) {
  const [messageIndex, setMessageIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setMessageIndex((prev) => (prev + 1) % LOADING_MESSAGES.length);
    }, 3000);
    return () => clearInterval(interval);
  }, []);

  const currentMessage = LOADING_MESSAGES[messageIndex];

  return (
    <div className={cn('space-y-8', className)}>
      {/* Loading Header */}
      <div
        className="flex flex-col items-center justify-center py-12 space-y-4"
        role="status"
        aria-live="polite"
        aria-label="Loading search results"
      >
        <div className="relative">
          <Disc className="w-16 h-16 text-discogs-primary animate-spin" aria-hidden="true" />
          <div className="absolute inset-0 bg-discogs-primary/20 blur-xl rounded-full animate-pulse" />
        </div>

        <div className="text-center space-y-2">
          <h3 className="text-lg font-semibold text-white">Searching Discogs...</h3>
          <p className="text-sm text-white/70 max-w-md">
            Exploring{' '}
            <span className="text-discogs-primary font-semibold transition-all duration-500" key={messageIndex}>
              {currentMessage}
            </span>{' '}
            through millions of releases
          </p>
        </div>
      </div>

      {/* Skeleton Cards Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {Array.from({ length: count }).map((_, index) => (
          <SkeletonResultCard key={index} delay={index * 100} />
        ))}
      </div>
    </div>
  );
}

function SkeletonResultCard({ delay = 0 }: { delay?: number }) {
  return (
    <MagicCard
      className={cn('p-4 space-y-4', 'bg-white/5 backdrop-blur-sm border border-white/10', 'animate-pulse')}
      style={{ animationDelay: `${delay}ms` }}
    >
      <div className="flex items-center gap-4">
        <div className="relative flex-shrink-0">
          <Skeleton className="w-16 h-16 rounded-full bg-white/10" />
          <div className="absolute inset-0 m-auto w-4 h-4 rounded-full bg-white/5" />
        </div>
        <div className="flex-1 space-y-2">
          <Skeleton className="h-4 w-3/4 bg-white/10" />
          <Skeleton className="h-3 w-1/2 bg-white/10" />
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex gap-2">
          <Skeleton className="h-6 w-16 rounded-full bg-white/10" />
          <Skeleton className="h-6 w-20 rounded-full bg-white/10" />
        </div>
        <Skeleton className="h-3 w-full bg-white/10" />
        <Skeleton className="h-3 w-4/5 bg-white/10" />
      </div>

      <div className="flex gap-2 pt-2">
        <Skeleton className="h-9 w-24 rounded-lg bg-white/10" />
        <Skeleton className="h-9 w-24 rounded-lg bg-white/10" />
      </div>
    </MagicCard>
  );
}
