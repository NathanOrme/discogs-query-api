import { cn } from '@/lib/utils';
import { type JSX } from 'react';

function Skeleton({ className, ...props }: React.ComponentProps<'div'>): JSX.Element {
  return (
    <div
      data-slot="skeleton"
      className={cn('bg-accent animate-pulse rounded-md', className)}
      {...props}
    />
  );
}

export { Skeleton };
