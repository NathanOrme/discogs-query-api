/**
 * @file ResultCard.tsx
 * @description Enhanced vinyl-themed result card for displaying Discogs releases
 */

import type { EntryType } from '@/utils/DiscogUtils';
import { MagicCard } from '@/components/magicui';
import { Badge, Button } from '@/components/ui';
import { cn } from '@/lib/utils';
import { Calendar, Disc, ExternalLink, MapPin, Tag } from 'lucide-react';

interface ResultCardProps {
  entry: EntryType;
  onViewDetails?: (entry: EntryType) => void;
  onAddToCollection?: (entry: EntryType) => void;
  className?: string;
}

export function ResultCard({
  entry,
  onViewDetails,
  onAddToCollection,
  className,
}: ResultCardProps) {
  const {
    title = 'Untitled',
    format,
    country = 'Unknown',
    year = 'N/A',
    uri,
    numberForSale,
    lowestPrice,
  } = entry;

  const formatStr = format && format.length > 0 ? format.join(', ') : 'N/A';
  const primaryFormat = format?.[0] || 'Release';

  const handleViewDetails = () => {
    if (uri) {
      window.open(uri, '_blank', 'noopener,noreferrer');
    } else if (onViewDetails) {
      onViewDetails(entry);
    }
  };

  return (
    <MagicCard
      className={cn(
        'group relative overflow-hidden',
        'bg-white/5 backdrop-blur-sm border border-white/10',
        'transition-all duration-300',
        'hover:bg-white/10 hover:border-discogs-primary/50',
        'hover:shadow-lg hover:shadow-discogs-primary/20',
        className
      )}
      gradientColor="rgba(230, 81, 0, 0.15)"
    >
      <div className="p-4 space-y-4">
        {/* Header: Vinyl Disc + Title */}
        <div className="flex items-start gap-4">
          <div className="relative flex-shrink-0 group-hover:rotate-12 transition-transform duration-500">
            <div className="vinyl-disc w-16 h-16 flex items-center justify-center">
              <div className="relative z-10">
                <Disc className="w-8 h-8 text-discogs-primary" />
              </div>
            </div>
          </div>

          <div className="flex-1 min-w-0 space-y-2">
            <h3
              className="text-base font-semibold text-white line-clamp-2 leading-tight"
              title={title}
            >
              {title}
            </h3>

            <div className="flex flex-wrap gap-2">
              {year !== 'N/A' && (
                <Badge
                  variant="secondary"
                  className="bg-discogs-primary/20 text-discogs-primary border-discogs-primary/30 text-xs"
                >
                  <Calendar className="w-3 h-3 mr-1" />
                  {year}
                </Badge>
              )}
              {primaryFormat && (
                <Badge
                  variant="secondary"
                  className="bg-white/10 text-white border-white/20 text-xs"
                >
                  <Tag className="w-3 h-3 mr-1" />
                  {primaryFormat}
                </Badge>
              )}
              {country !== 'Unknown' && (
                <Badge
                  variant="secondary"
                  className="bg-white/10 text-white/80 border-white/20 text-xs"
                >
                  <MapPin className="w-3 h-3 mr-1" />
                  {country}
                </Badge>
              )}
            </div>
          </div>
        </div>

        {/* Release Details */}
        <div className="space-y-2 text-sm">
          <div className="flex items-start gap-2">
            <span className="text-white/60 min-w-[80px]">Format:</span>
            <span className="text-white/90 flex-1">{formatStr}</span>
          </div>

          {numberForSale !== undefined && (
            <div className="flex items-start gap-2">
              <span className="text-white/60 min-w-[80px]">For Sale:</span>
              <span className="text-white/90">
                {numberForSale === 0 ? (
                  <span className="text-white/50">None available</span>
                ) : (
                  <>
                    {numberForSale.toLocaleString()}{' '}
                    {numberForSale === 1 ? 'copy' : 'copies'}
                  </>
                )}
              </span>
            </div>
          )}

          {lowestPrice !== undefined && lowestPrice !== null && (
            <div className="flex items-start gap-2">
              <span className="text-white/60 min-w-[80px]">Price from:</span>
              <span className="text-green-400 font-semibold">
                £{lowestPrice.toFixed(2)}
              </span>
            </div>
          )}
        </div>

        {/* Action Buttons */}
        <div className="flex gap-2 pt-2 border-t border-white/10">
          <Button
            variant="outline"
            size="sm"
            onClick={handleViewDetails}
            className="flex-1 bg-white/5 border-white/10 text-white hover:bg-discogs-primary hover:border-discogs-primary hover:text-white transition-all"
          >
            <ExternalLink className="w-4 h-4 mr-2" />
            View Details
          </Button>

          {onAddToCollection && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => onAddToCollection(entry)}
              className="flex-1 bg-white/5 border-white/10 text-white hover:bg-white/10 hover:border-white/20 hover:text-white transition-all"
            >
              <Disc className="w-4 h-4 mr-2" />
              Add
            </Button>
          )}
        </div>
      </div>

      {/* Hover gradient overlay */}
      <div
        className={cn(
          'absolute inset-0 opacity-0 group-hover:opacity-100',
          'bg-gradient-to-br from-discogs-primary/5 to-transparent',
          'transition-opacity duration-300',
          'pointer-events-none'
        )}
      />
    </MagicCard>
  );
}
