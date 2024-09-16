// modules/cheapestItem.js

/**
 * Function to create and display the "Cheapest Item" section.
 * @param {Array} items - Array of items where each item contains title, price, seller, and url.
 */
export function displayCheapestItems(items) {
    const container = document.getElementById('cheapest-item-section');
    container.innerHTML = ''; // Clear any existing content

    if (items.length === 0) {
        container.innerHTML = '<p>No items found.</p>';
        return;
    }

    const section = document.createElement('div');
    section.className = 'cheapest-item-section'; // Apply specific class for cheapest item section

    const header = document.createElement('div');
    header.className = 'cheapest-item-header';
    header.innerHTML = '<h2>Cheapest Item for Each Query</h2>';
    section.appendChild(header);

    const content = document.createElement('div');
    content.className = 'cheapest-item-content';

    items.forEach(item => {
        const itemContainer = document.createElement('div');
        itemContainer.className = 'cheapest-item'; // Apply class for each item

        const title = document.createElement('h3');
        title.textContent = item.title;
        itemContainer.appendChild(title);

        const details = document.createElement('div');
        details.className = 'details';
        details.innerHTML = `
            <p><strong>Price:</strong> ${item.lowestPrice !== null ? "£" + item.lowestPrice.toFixed(2) : 'N/A'}</p>
            <p><strong>Number For Sale:</strong> ${item.numberForSale !== null ? item.numberForSale : 'N/A'}</p>
            <p><strong>Country:</strong> ${item.country || 'N/A'}</p>
            <a href="${item.uri || '#'}" target="_blank">View Item</a>
        `;
        itemContainer.appendChild(details);

        content.appendChild(itemContainer);
    });

    section.appendChild(content);
    container.appendChild(section);
}
