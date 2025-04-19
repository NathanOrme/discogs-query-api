import React, { JSX } from 'react';
import { Item } from './types';

/**
 * Renders a single item.
 *
 * @param {Item} item - The item object containing details.
 * @param {number} index - The index of the item in the list.
 * @returns {JSX.Element} The rendered item details.
 */
const renderItem = (item: Item, index: number): JSX.Element => (
  <div className="cheapest-item" key={index}>
    <h3>{item.title}</h3>
    <div className="details">
      <p>
        <strong>Price:</strong>{' '}
        {item.lowestPrice !== null ? `£${item.lowestPrice?.toFixed(2)}` : 'N/A'}
      </p>
      <p>
        <strong>Number For Sale:</strong>{' '}
        {item.numberForSale !== null ? item.numberForSale : 'N/A'}
      </p>
      <p>
        <strong>Country:</strong> {item.country ?? 'N/A'}
      </p>
      <a href={item.uri ?? '#'} target="_blank" rel="noopener noreferrer">
        View Item
      </a>
    </div>
  </div>
);

export default renderItem;
