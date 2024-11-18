//src/modules/CheapestItem.tsx

import React from 'react';
import { Item } from './cheapestItem/types';
import renderContent from './cheapestItem/renderContent';

/**
 * Component to display the "Cheapest Item" section.
 */
const CheapestItem: React.FC<{ items: Item[] }> = ({ items }) => {
  return (
    <div className="cheapest-item-section" id="cheapest-item-container">
      <h2>Cheapest Item for Each Query</h2>
      {renderContent(items)}
    </div>
  );
};

export default CheapestItem;
