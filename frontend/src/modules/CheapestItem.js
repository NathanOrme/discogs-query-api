// src/modules/CheapestItem.js

import React from 'react';
import PropTypes from 'prop-types';

/**
 * Component to display the "Cheapest Item" section.
 * @param {Array} items - Array of items where each item contains title, price, seller, and url.
 */
const CheapestItem = ({ items }) => {
    return (
        <div className="cheapest-item-section" id="cheapest-item-container">
            <h2>Cheapest Item for Each Query</h2>
            {items.length === 0 ? (
                <p>No items found.</p>
            ) : (
                <div className="cheapest-item-content">
                    {items.map((item, index) => (
                        <div className="cheapest-item" key={index}>
                            <h3>{item.title}</h3>
                            <div className="details">
                                <p><strong>Price:</strong> {item.lowestPrice !== null ? `£${item.lowestPrice.toFixed(2)}` : 'N/A'}</p>
                                <p><strong>Number For Sale:</strong> {item.numberForSale !== null ? item.numberForSale : 'N/A'}</p>
                                <p><strong>Country:</strong> {item.country || 'N/A'}</p>
                                <a href={item.uri || '#'} target="_blank" rel="noopener noreferrer">View Item</a>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

CheapestItem.propTypes = {
    items: PropTypes.arrayOf(PropTypes.shape({
        title: PropTypes.string.isRequired,
        lowestPrice: PropTypes.number,
        numberForSale: PropTypes.number,
        country: PropTypes.string,
        uri: PropTypes.string
    })).isRequired
};

export default CheapestItem;
