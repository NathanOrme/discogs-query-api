import { Tag } from 'lucide-react';

import { MagicCard } from '@/components/magicui';
import { CardContent } from '@/components/ui';

import type { CheapestItemProps } from '@/utils/DiscogsTypes';
import type { FC } from 'react';
import { CustomLink } from './CustomLink';

const CheapestItemComponent: FC<CheapestItemProps> = ({ item }) => {
  return (
    <MagicCard gradientColor="#1abc9c">
      <CardContent className="p-4">
        <div className="flex items-center gap-2 mb-3">
          <Tag size={20} className="text-green-400" />
          <h3 className="text-lg font-bold text-green-400">Cheapest Item</h3>
        </div>
        <div className="space-y-2 text-sm">
          <p className="text-white/90">
            <span className="font-medium text-green-400">Title:</span>{' '}
            <span className="text-white font-medium">{item.title ?? 'Untitled'}</span>
          </p>
          <p className="text-white/90 break-all">
            <span className="font-medium text-green-400">URL:</span>{' '}
            {item.uri ? (
              <CustomLink href={item.uri} target="_blank" rel="noopener noreferrer">
                {item.uri}
              </CustomLink>
            ) : (
              <span className="text-white/60">N/A</span>
            )}
          </p>
          <p className="text-white/90">
            <span className="font-medium text-green-400">Price:</span>{' '}
            <span className="text-green-300 font-bold text-lg">
              {item.lowestPrice !== null ? `£${item.lowestPrice?.toFixed(2)}` : 'N/A'}
            </span>
          </p>
        </div>
      </CardContent>
    </MagicCard>
  );
};

export default CheapestItemComponent;
