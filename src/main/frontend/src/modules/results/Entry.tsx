import React from 'react';
import { Entry } from '../types';

interface EntryProps {
  entry: Entry;
}

const EntryComponent: React.FC<EntryProps> = ({ entry }) => {
  const id = entry.id || 'N/A';
  const format = entry.format ? entry.format.join(', ') : 'N/A';
  const country = entry.country || 'N/A';
  const year = entry.year || 'N/A';
  const uri = entry.uri || '#';

  return (
    <div className="result-item" key={id}>
      <h3>{entry.title || 'Untitled'}</h3>
      <div className="details">
        <p>
          <strong>ID:</strong> {id}
        </p>
        <p>
          <strong>Formats:</strong> {format}
        </p>
        <p>
          <strong>Country:</strong> {country}
        </p>
        <p>
          <strong>Year:</strong> {year}
        </p>
        <p>
          <strong>URL:</strong>{' '}
          <a href={uri} target="_blank" rel="noopener noreferrer">
            {uri}
          </a>
        </p>
        <p>
          <strong>Number For Sale:</strong> {entry.numberForSale || 'N/A'}
        </p>
        <p>
          <strong>Lowest Price:</strong>{' '}
          {entry.lowestPrice != null
            ? `£${entry.lowestPrice.toFixed(2)}`
            : 'N/A'}
        </p>
      </div>
    </div>
  );
};

export default EntryComponent;
export type { Entry };
