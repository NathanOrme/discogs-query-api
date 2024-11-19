// src/modules/QueryFieldsRenderer.tsx

import React from 'react';
import { Query } from './QueryFieldsTypes';
import { discogFormats } from './../DiscogsData';

export const renderQueryFields = (
  query: Query,
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void,
  removeQuery: (index: number) => void,
  queryCount: number
): JSX.Element => (
  <div className="query" key={index}>
    <div className="query-header">
      <span>Query {index + 1}</span>
      {queryCount > 1 && (
        <button
          type="button"
          className="delete-button"
          onClick={() => removeQuery(index)}
        >
          Remove
        </button>
      )}
    </div>
    <div className="query-content">
      {renderInputField(
        'Artist:',
        'artist',
        query.artist,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Barcode:',
        'barcode',
        query.barcode,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Album (optional):',
        'album',
        query.album,
        index,
        handleInputChange
      )}
      {renderInputField(
        'Track (optional):',
        'track',
        query.track,
        index,
        handleInputChange
      )}
      {renderSelectField(
        'Format (optional):',
        'format',
        query.format,
        discogFormats,
        index,
        handleInputChange
      )}
    </div>
  </div>
);

const renderInputField = (
  label: string,
  field: keyof Query,
  value: string,
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void
): JSX.Element => (
  <>
    <label htmlFor={`${field}-${index}`}>{label}</label>
    <input
      type="text"
      className={field}
      name={`${field}-${index}`}
      value={value}
      onChange={(e) => handleInputChange(index, field, e.target.value)}
    />
  </>
);

const renderSelectField = (
  label: string,
  field: keyof Query,
  value: string,
  options: { value: string; text: string }[],
  index: number,
  handleInputChange: (index: number, field: keyof Query, value: string) => void
): JSX.Element => (
  <>
    <label htmlFor={`${field}-${index}`}>{label}</label>
    <select
      className={field}
      name={`${field}-${index}`}
      value={value}
      onChange={(e) => handleInputChange(index, field, e.target.value)}
    >
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.text}
        </option>
      ))}
    </select>
  </>
);
