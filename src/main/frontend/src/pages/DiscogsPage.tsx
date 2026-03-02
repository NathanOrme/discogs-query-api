// src/pages/DiscogsPage.tsx

import QueryFields from '@/components/QueryFields';
import { EmptyState } from '@/components/results/EmptyState';
import { ResultCard } from '@/components/results/ResultCard';
import type { ViewMode } from '@/components/results/ResultsHeader';
import { ResultsHeader } from '@/components/results/ResultsHeader';
import { ResultsSkeleton } from '@/components/results/ResultsSkeleton';
import type { EntryType } from '@/utils/DiscogUtils';
import exportToJson from '@/utils/DiscogUtils';
import { Input, Label } from '@/components/ui';
import { cn } from '@/lib/utils';
import type { Query, QueryResult } from '@/types/DiscogsTypes';
import { logger } from '@/utils/logger';
import { Disc, RotateCcw, Search, User } from 'lucide-react';
import {
  memo,
  useCallback,
  useMemo,
  useState,
  type FC,
  type JSX,
} from 'react';

const API_URL = 'https://discogs-query-api.onrender.com/discogs-query/search';

const QUERY_PRESETS = [
  { label: 'Miles Davis', artist: 'Miles Davis', album: '', format: 'vinyl' },
  { label: 'Daft Punk', artist: 'Daft Punk', album: '', format: 'vinyl' },
  { label: 'Pink Floyd', artist: 'Pink Floyd', album: '', format: '' },
  { label: 'Blue Note', artist: '', album: '', format: 'vinyl' },
  { label: 'All LP', artist: '', album: '', format: 'lp' },
] as const;

const EMPTY_QUERY: Query = {
  artist: '',
  barcode: '',
  album: '',
  track: '',
  format: '',
  types: '',
};

const DiscogsPage: FC = (): JSX.Element => {
  const [queries, setQueries] = useState<Query[]>([{ ...EMPTY_QUERY }]);
  const [response, setResponse] = useState<QueryResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [username, setUsername] = useState('');
  const [hasSearched, setHasSearched] = useState(false);
  const [viewMode, setViewMode] = useState<ViewMode>('grid');

  const allEntries = useMemo<EntryType[]>(
    () => response.flatMap((qr) => Object.values(qr.results).flat() as EntryType[]),
    [response],
  );

  const uniqueTitleCount = useMemo(
    () => new Set(response.flatMap((qr) => Object.keys(qr.results))).size,
    [response],
  );

  const handleQueriesChange = useCallback((newQueries: Query[]) => {
    setQueries(newQueries);
  }, []);

  const handleExportJSON = useCallback(() => {
    if (response.length > 0) {
      exportToJson(response, 'discogs-results');
    }
  }, [response]);

  const handleReset = useCallback(() => {
    setQueries([{ ...EMPTY_QUERY }]);
    setResponse([]);
    setError(null);
    setHasSearched(false);
  }, []);

  const performSearch = useCallback(
    async (searchQueries: Query[], searchUsername?: string) => {
      setLoading(true);
      setError(null);
      setHasSearched(true);
      setResponse([]);
      try {
        const payloadQueries = searchQueries.map(({ id: _id, ...rest }) => rest);
        const res = await fetch(API_URL, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: searchUsername || undefined,
            queries: payloadQueries,
          }),
        });
        if (!res.ok) {
          const msg = await res.text();
          throw new Error(`Server error ${res.status}: ${msg}`);
        }
        const data = await res.json();
        setResponse(data);
      } catch (err) {
        logger.error(err);
        setError(
          err instanceof Error ? err.message : 'An unexpected error occurred.',
        );
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const handleSearch = useCallback(() => {
    performSearch(queries, username).catch(logger.error);
  }, [queries, username, performSearch]);

  const handlePreset = useCallback(
    (preset: { artist: string; album: string; format: string }) => {
      const presetQuery: Query = {
        artist: preset.artist,
        barcode: '',
        album: preset.album,
        track: '',
        format: preset.format,
        types: '',
      };
      performSearch([presetQuery]).catch(logger.error);
    },
    [performSearch],
  );

  const handleRetry = useCallback(() => {
    performSearch(queries, username).catch(logger.error);
  }, [queries, username, performSearch]);

  const resultsPanel = (): JSX.Element => {
    if (loading) return <ResultsSkeleton />;
    if (error)
      return <EmptyState variant="error" errorMessage={error} onRetry={handleRetry} />;
    if (!hasSearched) return <EmptyState variant="initial" />;
    if (allEntries.length === 0) return <EmptyState variant="no-results" />;

    return (
      <div className="space-y-4">
        <ResultsHeader
          totalResults={allEntries.length}
          uniqueTitles={uniqueTitleCount}
          viewMode={viewMode}
          onViewModeChange={setViewMode}
          onExportJSON={handleExportJSON}
        />
        <div
          className={cn(
            'grid gap-4',
            viewMode === 'grid'
              ? 'grid-cols-1 sm:grid-cols-2 xl:grid-cols-3'
              : 'grid-cols-1',
          )}
        >
          {allEntries.map((entry, i) => (
            <ResultCard key={entry.id ?? i} entry={entry} />
          ))}
        </div>
      </div>
    );
  };

  return (
    <div className="min-h-screen" style={{ background: '#141210' }}>
      {/* Compact page header */}
      <div
        className="flex items-center gap-3 px-5 py-3.5 border-b"
        style={{ borderColor: 'rgba(255,255,255,0.05)' }}
      >
        <Disc className="w-5 h-5 flex-shrink-0" style={{ color: '#e65100' }} />
        <div>
          <h1 className="text-sm font-bold text-white/85 tracking-wide">Discogs Search</h1>
          <p className="text-xs" style={{ color: 'rgba(255,255,255,0.3)' }}>
            Search millions of music releases
          </p>
        </div>
      </div>

      {/* Two-panel layout */}
      <div className="flex flex-col md:flex-row" style={{ minHeight: 'calc(100vh - 57px)' }}>
        {/* LEFT: Search form panel */}
        <div
          className="md:w-80 lg:w-96 flex-shrink-0 p-5 space-y-5 overflow-y-auto"
          style={{
            borderRight: '1px solid rgba(255,255,255,0.05)',
            borderBottom: '1px solid rgba(255,255,255,0.05)',
          }}
        >
          {/* Quick presets */}
          <div>
            <p
              className="text-[10px] font-semibold uppercase tracking-widest mb-2"
              style={{ color: 'rgba(255,255,255,0.25)' }}
            >
              Quick Search
            </p>
            <div className="flex flex-wrap gap-1.5">
              {QUERY_PRESETS.map((p) => (
                <button
                  key={p.label}
                  onClick={() => handlePreset(p)}
                  className="px-2.5 py-1 rounded text-xs border transition-all duration-150"
                  style={{ borderColor: 'rgba(255,255,255,0.1)', color: 'rgba(255,255,255,0.45)' }}
                  onMouseEnter={(e) => {
                    (e.currentTarget as HTMLElement).style.color = '#e65100';
                    (e.currentTarget as HTMLElement).style.borderColor = 'rgba(230,81,0,0.4)';
                  }}
                  onMouseLeave={(e) => {
                    (e.currentTarget as HTMLElement).style.color = 'rgba(255,255,255,0.45)';
                    (e.currentTarget as HTMLElement).style.borderColor = 'rgba(255,255,255,0.1)';
                  }}
                  aria-label={`Quick search: ${p.label}`}
                >
                  {p.label}
                </button>
              ))}
            </div>
          </div>

          {/* Divider */}
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.05)' }} />

          {/* Query fields */}
          <QueryFields onQueriesChange={handleQueriesChange} />

          {/* Divider */}
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.05)' }} />

          {/* Username */}
          <div className="space-y-1.5">
            <Label
              htmlFor="discogs-username"
              className="flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-widest"
              style={{ color: 'rgba(255,255,255,0.25)' }}
            >
              <User className="w-3 h-3" />
              Collection Filter
              <span
                className="normal-case text-[9px] tracking-normal"
                style={{ color: 'rgba(230,81,0,0.55)' }}
              >
                optional
              </span>
            </Label>
            <Input
              id="discogs-username"
              placeholder="Discogs username..."
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') handleSearch();
              }}
              className="h-9 text-sm text-white placeholder:text-white/25"
              style={{
                background: 'rgba(255,255,255,0.03)',
                borderColor: 'rgba(255,255,255,0.1)',
              }}
              autoComplete="username"
              spellCheck={false}
            />
          </div>

          {/* Actions */}
          <div className="flex gap-2 pt-1">
            <button
              onClick={handleSearch}
              disabled={loading}
              className="flex-1 flex items-center justify-center gap-2 h-9 rounded-lg text-sm font-semibold text-white transition-all duration-200 disabled:opacity-60"
              style={{ background: loading ? 'rgba(230,81,0,0.5)' : '#e65100' }}
              aria-label="Execute search"
            >
              {loading ? (
                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                <Search className="w-4 h-4" />
              )}
              {loading ? 'Searching...' : 'Search'}
            </button>
            <button
              onClick={handleReset}
              className="px-3 h-9 rounded-lg text-sm border transition-all duration-200"
              style={{ borderColor: 'rgba(255,255,255,0.1)', color: 'rgba(255,255,255,0.3)' }}
              onMouseEnter={(e) => {
                (e.currentTarget as HTMLElement).style.color = 'rgba(255,255,255,0.6)';
                (e.currentTarget as HTMLElement).style.borderColor = 'rgba(255,255,255,0.2)';
              }}
              onMouseLeave={(e) => {
                (e.currentTarget as HTMLElement).style.color = 'rgba(255,255,255,0.3)';
                (e.currentTarget as HTMLElement).style.borderColor = 'rgba(255,255,255,0.1)';
              }}
              aria-label="Reset form"
            >
              <RotateCcw className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* RIGHT: Results panel */}
        <div className="flex-1 p-5 overflow-y-auto">{resultsPanel()}</div>
      </div>
    </div>
  );
};

export default memo(DiscogsPage);
