import { discogFormats } from '@/utils/DiscogsConstants';
import type { QueryItemProps } from '@/utils/DiscogsTypes';
import {
  Input,
  Label,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui';
import { Trash2 } from 'lucide-react';
import type { ChangeEvent, FC } from 'react';
import { useCallback } from 'react';
import type { Query } from '@/types/DiscogsTypes';

const QueryItem: FC<QueryItemProps> = ({
  query,
  index,
  onInputChange,
  removeQuery,
}) => {
  const handleTextChange = useCallback(
    (field: keyof typeof query) => (e: ChangeEvent<HTMLInputElement>) => {
      onInputChange(index, field, e.target.value);
    },
    [index, onInputChange]
  );

  const handleSelectChange = useCallback(
    (value: string) => {
      const actualValue = value === 'none' ? '' : value;
      onInputChange(index, 'format', actualValue);
    },
    [index, onInputChange]
  );

  const handleRemove = useCallback(() => {
    removeQuery(index);
  }, [index, removeQuery]);

  return (
    <div
      className="rounded-xl p-4 space-y-3"
      style={{
        background: 'rgba(255,255,255,0.02)',
        border: '1px solid rgba(255,255,255,0.06)',
      }}
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <span
          className="text-[10px] font-semibold uppercase tracking-widest"
          style={{ color: 'rgba(255,255,255,0.25)' }}
        >
          Query {index + 1}
        </span>
        {index > 0 && (
          <button
            onClick={handleRemove}
            className="flex items-center gap-1 text-xs transition-colors duration-150"
            style={{ color: 'rgba(255,80,80,0.5)' }}
            onMouseEnter={(e) =>
              ((e.currentTarget as HTMLElement).style.color =
                'rgba(255,80,80,0.9)')
            }
            onMouseLeave={(e) =>
              ((e.currentTarget as HTMLElement).style.color =
                'rgba(255,80,80,0.5)')
            }
            aria-label={`Remove query ${index + 1}`}
          >
            <Trash2 className="w-3.5 h-3.5" />
            Remove
          </button>
        )}
      </div>

      {/* Fields - 2-column grid on larger screens */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        {/* Artist */}
        <div className="space-y-1">
          <Label
            htmlFor={`artist-${index}`}
            className="text-[11px] font-medium"
            style={{ color: 'rgba(255,255,255,0.4)' }}
          >
            Artist
          </Label>
          <Input
            id={`artist-${index}`}
            value={query.artist ?? ''}
            onChange={handleTextChange('artist')}
            placeholder="e.g. Miles Davis"
            className="h-8 text-sm text-white placeholder:text-white/20"
            style={{
              background: 'rgba(255,255,255,0.04)',
              borderColor: 'rgba(255,255,255,0.08)',
            }}
          />
        </div>

        {/* Album */}
        <div className="space-y-1">
          <Label
            htmlFor={`album-${index}`}
            className="text-[11px] font-medium"
            style={{ color: 'rgba(255,255,255,0.4)' }}
          >
            Album
          </Label>
          <Input
            id={`album-${index}`}
            value={query.album ?? ''}
            onChange={handleTextChange('album')}
            placeholder="optional"
            className="h-8 text-sm text-white placeholder:text-white/20"
            style={{
              background: 'rgba(255,255,255,0.04)',
              borderColor: 'rgba(255,255,255,0.08)',
            }}
          />
        </div>

        {/* Barcode */}
        <div className="space-y-1">
          <Label
            htmlFor={`barcode-${index}`}
            className="text-[11px] font-medium"
            style={{ color: 'rgba(255,255,255,0.4)' }}
          >
            Barcode
          </Label>
          <Input
            id={`barcode-${index}`}
            value={query.barcode ?? ''}
            onChange={handleTextChange('barcode')}
            placeholder="optional"
            className="h-8 text-sm text-white placeholder:text-white/20"
            style={{
              background: 'rgba(255,255,255,0.04)',
              borderColor: 'rgba(255,255,255,0.08)',
            }}
          />
        </div>

        {/* Track */}
        <div className="space-y-1">
          <Label
            htmlFor={`track-${index}`}
            className="text-[11px] font-medium"
            style={{ color: 'rgba(255,255,255,0.4)' }}
          >
            Track
          </Label>
          <Input
            id={`track-${index}`}
            value={query.track ?? ''}
            onChange={handleTextChange('track')}
            placeholder="optional"
            className="h-8 text-sm text-white placeholder:text-white/20"
            style={{
              background: 'rgba(255,255,255,0.04)',
              borderColor: 'rgba(255,255,255,0.08)',
            }}
          />
        </div>

        {/* Format - full width */}
        <div className="space-y-1 sm:col-span-2">
          <Label
            htmlFor={`format-${index}`}
            className="text-[11px] font-medium"
            style={{ color: 'rgba(255,255,255,0.4)' }}
          >
            Format
          </Label>
          <Select
            value={query.format || undefined}
            onValueChange={handleSelectChange}
          >
            <SelectTrigger
              className="h-8 text-sm text-white"
              style={{
                background: 'rgba(255,255,255,0.04)',
                borderColor: 'rgba(255,255,255,0.08)',
              }}
            >
              <SelectValue placeholder="Any format" />
            </SelectTrigger>
            <SelectContent className="bg-[#1a1715] border-white/10">
              {discogFormats.map((option) => (
                <SelectItem
                  key={option.value || 'none'}
                  value={option.value || 'none'}
                  className="text-white/80 hover:bg-white/5 focus:bg-white/5"
                >
                  {option.text}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>
    </div>
  );
};

export default QueryItem;
