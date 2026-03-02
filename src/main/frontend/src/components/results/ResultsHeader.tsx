/**
 * @file ResultsHeader.tsx
 * @description Results header with stats, view mode toggle, export, and share functionality
 */

import { Button } from '@/components/ui';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';
import { logger } from '@/utils/logger';
import { Download, Grid3x3, Link2, List, Share2 } from 'lucide-react';
import { useState } from 'react';

export type ViewMode = 'grid' | 'list';

interface ResultsHeaderProps {
  totalResults: number;
  uniqueTitles?: number;
  viewMode: ViewMode;
  onViewModeChange: (mode: ViewMode) => void;
  onExportJSON?: () => void;
  onExportCSV?: () => void;
  searchUrl?: string;
  className?: string;
}

export function ResultsHeader({
  totalResults,
  uniqueTitles,
  viewMode,
  onViewModeChange,
  onExportJSON,
  onExportCSV,
  searchUrl,
  className,
}: ResultsHeaderProps) {
  const [isCopied, setIsCopied] = useState(false);

  const handleCopyUrl = async () => {
    if (!searchUrl) return;
    try {
      await navigator.clipboard.writeText(searchUrl);
      setIsCopied(true);
      setTimeout(() => setIsCopied(false), 2000);
    } catch (error) {
      logger.error('Failed to copy URL:', error);
    }
  };

  const hasExportOptions = onExportJSON || onExportCSV;

  return (
    <div
      className={cn(
        'flex flex-col sm:flex-row items-start sm:items-center justify-between',
        'gap-4 p-4 rounded-lg',
        'bg-white/5 backdrop-blur-sm border border-white/10',
        className,
      )}
    >
      {/* Stats */}
      <div className="flex flex-col gap-1">
        <h3 className="text-xl font-bold text-white">
          {totalResults.toLocaleString()}{' '}
          <span className="text-white/70">{totalResults === 1 ? 'Result' : 'Results'}</span>
        </h3>
        {uniqueTitles !== undefined && uniqueTitles > 0 && (
          <p className="text-sm text-white/60">
            {uniqueTitles.toLocaleString()} unique {uniqueTitles === 1 ? 'title' : 'titles'}
          </p>
        )}
      </div>

      {/* Actions */}
      <div className="flex items-center gap-2 w-full sm:w-auto">
        {/* View Mode Toggle */}
        <div
          className="flex items-center rounded-lg bg-white/5 border border-white/10 p-1"
          role="group"
          aria-label="View mode"
        >
          <Button
            variant={viewMode === 'grid' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => onViewModeChange('grid')}
            aria-label="Grid view"
            aria-pressed={viewMode === 'grid'}
            className={cn(
              'h-8 w-8 p-0',
              viewMode === 'grid'
                ? 'bg-discogs-primary hover:bg-discogs-primary-dark text-white'
                : 'text-white/70 hover:text-white hover:bg-white/10',
            )}
          >
            <Grid3x3 className="h-4 w-4" />
          </Button>
          <Button
            variant={viewMode === 'list' ? 'default' : 'ghost'}
            size="sm"
            onClick={() => onViewModeChange('list')}
            aria-label="List view"
            aria-pressed={viewMode === 'list'}
            className={cn(
              'h-8 w-8 p-0',
              viewMode === 'list'
                ? 'bg-discogs-primary hover:bg-discogs-primary-dark text-white'
                : 'text-white/70 hover:text-white hover:bg-white/10',
            )}
          >
            <List className="h-4 w-4" />
          </Button>
        </div>

        {/* Export Dropdown */}
        {hasExportOptions && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                variant="outline"
                size="sm"
                className="bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                aria-label="Export results"
              >
                <Download className="h-4 w-4 mr-2" />
                Export
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="bg-black/95 border-white/10 text-white">
              {onExportJSON && (
                <DropdownMenuItem
                  onClick={onExportJSON}
                  className="cursor-pointer hover:bg-white/10 focus:bg-white/10"
                >
                  <Download className="h-4 w-4 mr-2" />
                  Export as JSON
                </DropdownMenuItem>
              )}
              {onExportCSV && (
                <DropdownMenuItem
                  onClick={onExportCSV}
                  className="cursor-pointer hover:bg-white/10 focus:bg-white/10"
                >
                  <Download className="h-4 w-4 mr-2" />
                  Export as CSV
                </DropdownMenuItem>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        )}

        {/* Share Button */}
        {searchUrl && (
          <Button
            variant="outline"
            size="sm"
            onClick={handleCopyUrl}
            className={cn(
              'bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white transition-all',
              isCopied && 'bg-green-500/20 border-green-500/50 text-green-400',
            )}
            aria-label={isCopied ? 'URL copied' : 'Share search'}
          >
            {isCopied ? (
              <>
                <Link2 className="h-4 w-4 mr-2" />
                Copied!
              </>
            ) : (
              <>
                <Share2 className="h-4 w-4 mr-2" />
                Share
              </>
            )}
          </Button>
        )}
      </div>
    </div>
  );
}
