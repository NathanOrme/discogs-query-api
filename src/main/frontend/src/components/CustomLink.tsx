// src/components/CustomLink.tsx
import { cn } from '@/lib/utils';

import type { AnchorHTMLAttributes, FC, ReactNode } from 'react';

interface CustomLinkProps extends AnchorHTMLAttributes<HTMLAnchorElement> {
  children: ReactNode;
  className?: string;
}

export const CustomLink: FC<CustomLinkProps> = ({
  children,
  className,
  ...props
}) => {
  return (
    <a
      className={cn(
        'text-cyan-400 hover:text-cyan-300 underline font-bold transition-colors duration-200 break-all',
        className
      )}
      {...props}
    >
      {children}
    </a>
  );
};
