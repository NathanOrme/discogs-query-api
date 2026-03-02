import {
  motion,
  useMotionTemplate,
  useMotionValue,
} from 'motion/react';
import React, {
  useCallback,
  useEffect,
  useRef,
  useState,
} from 'react';

import { cn } from '@/lib/utils';

interface MagicCardProps extends React.HTMLAttributes<HTMLDivElement> {
  children?: React.ReactNode;
  className?: string;
  gradientSize?: number;
  gradientColor?: string;
  gradientOpacity?: number;
  gradientFrom?: string;
  gradientTo?: string;
  isRust?: boolean;
}

export function MagicCard({
  children,
  className,
  gradientSize = 200,
  gradientColor = 'rgba(255, 255, 255, 0.1)',
  gradientOpacity = 0.4,
  gradientFrom: gradientFromProp = '#e65100',
  gradientTo: gradientToProp = '#bf360c',
  isRust,
  ...props
}: Readonly<MagicCardProps>) {
  const cardRef = useRef<HTMLDivElement>(null);
  const mouseX = useMotionValue(-gradientSize);
  const mouseY = useMotionValue(-gradientSize);

  const [gradientFrom, setGradientFrom] = useState(gradientFromProp);
  const [gradientTo, setGradientTo] = useState(gradientToProp);

  useEffect(() => {
    if (isRust) {
      setGradientFrom('#b6a559');
      setGradientTo('#f39c12');
    } else {
      setGradientFrom(gradientFromProp);
      setGradientTo(gradientToProp);
    }
  }, [isRust, gradientFromProp, gradientToProp]);

  const handleMouseMove = useCallback(
    (e: MouseEvent) => {
      if (cardRef.current) {
        const { left, top } = cardRef.current.getBoundingClientRect();
        mouseX.set(e.clientX - left);
        mouseY.set(e.clientY - top);
      }
    },
    [mouseX, mouseY],
  );

  const handleMouseOut = useCallback(
    (e: MouseEvent) => {
      if (!e.relatedTarget) {
        document.removeEventListener('mousemove', handleMouseMove);
        mouseX.set(-gradientSize);
        mouseY.set(-gradientSize);
      }
    },
    [handleMouseMove, mouseX, gradientSize, mouseY],
  );

  const handleMouseEnter = useCallback(() => {
    document.addEventListener('mousemove', handleMouseMove);
    mouseX.set(-gradientSize);
    mouseY.set(-gradientSize);
  }, [handleMouseMove, mouseX, gradientSize, mouseY]);

  useEffect(() => {
    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseout', handleMouseOut);
    document.addEventListener('mouseenter', handleMouseEnter);
    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseout', handleMouseOut);
      document.removeEventListener('mouseenter', handleMouseEnter);
    };
  }, [handleMouseEnter, handleMouseMove, handleMouseOut]);

  useEffect(() => {
    mouseX.set(-gradientSize);
    mouseY.set(-gradientSize);
  }, [gradientSize, mouseX, mouseY]);

  return (
    <div
      ref={cardRef}
      className={cn(
        'group relative rounded-[inherit] transition-all duration-300',
        'bg-gradient-to-br from-black/40 via-black/60 to-black/80',
        'backdrop-blur-xl border border-white/10 shadow-2xl shadow-black/50',
        className,
      )}
      {...props}
    >
      {/* Animated border */}
      <motion.div
        className="pointer-events-none absolute inset-0 rounded-[inherit] opacity-0 duration-300 group-hover:opacity-100"
        style={{
          background: useMotionTemplate`
          radial-gradient(${gradientSize}px circle at ${mouseX}px ${mouseY}px,
          ${gradientFrom},
          ${gradientTo},
          transparent 100%
          )
          `,
        }}
      />

      {/* Inner content background */}
      <div className="absolute inset-px rounded-[inherit] bg-gradient-to-br from-black/60 to-black/80 backdrop-blur-xl" />

      {/* Glow effect */}
      <motion.div
        className="pointer-events-none absolute inset-px rounded-[inherit] opacity-0 transition-opacity duration-300 group-hover:opacity-100"
        style={{
          background: useMotionTemplate`
            radial-gradient(${gradientSize}px circle at ${mouseX}px ${mouseY}px, ${gradientColor}, transparent 100%)
          `,
          opacity: gradientOpacity,
        }}
      />

      {/* Content */}
      <div className="relative z-10">{children}</div>
    </div>
  );
}
