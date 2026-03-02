import type { EntryProps } from '@/utils/DiscogsTypes';
import type { FC } from 'react';
import { CustomLink } from './CustomLink';

import { MagicCard } from '@/components/magicui';
import { CardContent } from '@/components/ui';

const EntryCard: FC<EntryProps> = ({ entry }) => {
  const {
    id = 'N/A',
    title = 'Untitled',
    format,
    country = 'N/A',
    year = 'N/A',
    uri,
    numberForSale,
    lowestPrice,
  } = entry;

  const formatStr = format ? format.join(', ') : 'N/A';

  return (
    <MagicCard gradientColor="#9b59b6">
      <CardContent className="p-4">
        <h4 className="text-lg font-semibold text-white mb-3">{title}</h4>
        <div className="space-y-2 text-sm">
          <p className="text-white/90">
            <span className="font-medium text-purple-400">ID:</span>{' '}
            <span className="text-white/80">{id}</span>
          </p>
          <p className="text-white/90">
            <span className="font-medium text-purple-400">Formats:</span>{' '}
            <span className="text-white/80">{formatStr}</span>
          </p>
          <p className="text-white/90">
            <span className="font-medium text-purple-400">Country:</span>{' '}
            <span className="text-white/80">{country}</span>
          </p>
          <p className="text-white/90">
            <span className="font-medium text-purple-400">Year:</span>{' '}
            <span className="text-white/80">{year}</span>
          </p>
          <p className="text-white/90">
            <span className="font-medium text-purple-400">URL:</span>{' '}
            {uri ? (
              <CustomLink href={uri} target="_blank" rel="noopener noreferrer">
                {uri}
              </CustomLink>
            ) : (
              <span className="text-white/60">N/A</span>
            )}
          </p>
          <p className="text-white/90">
            <span className="font-medium text-purple-400">
              Number For Sale:
            </span>{' '}
            <span className="text-white/80">{numberForSale ?? 'N/A'}</span>
          </p>
          <p className="text-white/90">
            <span className="font-medium text-teal-400">Lowest Price:</span>{' '}
            <span className="text-green-400 font-medium">
              {lowestPrice !== null ? `£${lowestPrice?.toFixed(2)}` : 'N/A'}
            </span>
          </p>
        </div>
      </CardContent>
    </MagicCard>
  );
};

export default EntryCard;
