import React from 'react';
import { Item } from './types';
import renderItem from './renderItem';

/**
 * Renders the content based on the items array.
 *
 * @param {Item[]} items - Array of items.
 * @returns {JSX.Element} The rendered content.
 */
const renderContent = (items: Item[]): JSX.Element => {
  if (items.length === 0) {
    return <p>No items found.</p>;
  }

  return (
    <div className="cheapest-item-content">
      {items.map((item, index) => renderItem(item, index))}
    </div>
  );
};

export default renderContent;
