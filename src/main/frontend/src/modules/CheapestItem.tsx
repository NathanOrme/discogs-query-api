//src/modules/CheapestItem.tsx

import React from "react";

/**
 * Interface for item details.
 */
interface Item {
  title: string;
  lowestPrice?: number | null;
  numberForSale?: number | null;
  country?: string;
  uri?: string;
}

/**
 * Component to display the "Cheapest Item" section.
 *
 * @param {Object} props - The component props.
 * @param {Array} props.items - Array of items where each item contains title, price, seller, and url.
 * @returns {JSX.Element} The rendered component.
 */
const CheapestItem: React.FC<{ items: Item[] }> = ({ items }) => {
  return (
    <div className="cheapest-item-section" id="cheapest-item-container">
      <h2>Cheapest Item for Each Query</h2>
      {renderContent(items)}
    </div>
  );
};

/**
 * Renders the content based on the items array.
 *
 * @param {Array} items - Array of items.
 * @returns {JSX.Element} The rendered content.
 */
const renderContent = (items: Item[]) => {
  if (items.length === 0) {
    return <p>No items found.</p>;
  }

  return (
    <div className="cheapest-item-content">
      {items.map((item, index) => renderItem(item, index))}
    </div>
  );
};

/**
 * Renders a single item.
 *
 * @param {Object} item - The item object containing details.
 * @param {number} index - The index of the item in the list.
 * @returns {JSX.Element} The rendered item details.
 */
const renderItem = (item: Item, index: number) => (
  <div className="cheapest-item" key={index}>
    <h3>{item.title}</h3>
    <div className="details">
      <p>
        <strong>Price:</strong>{" "}
        {item.lowestPrice !== null ? `£${item.lowestPrice?.toFixed(2)}` : "N/A"}
      </p>
      <p>
        <strong>Number For Sale:</strong>{" "}
        {item.numberForSale !== null ? item.numberForSale : "N/A"}
      </p>
      <p>
        <strong>Country:</strong> {item.country || "N/A"}
      </p>
      <a href={item.uri || "#"} target="_blank" rel="noopener noreferrer">
        View Item
      </a>
    </div>
  </div>
);

export default CheapestItem;
