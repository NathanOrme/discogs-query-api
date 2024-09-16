export function displayCheapestItems(results) {
    const container = document.getElementById('cheapest-items-container');
    container.innerHTML = '';

    results.forEach(result => {
        if (result.cheapestItem) {
            const itemElement = document.createElement('div');
            itemElement.className = 'cheapest-item';
            itemElement.innerHTML = `
                <h3>${result.cheapestItem.title}</h3>
                <p>Price: $${result.cheapestItem.lowestPrice}</p>
                <p><a href="${result.cheapestItem.url}" target="_blank">View Item</a></p>
            `;
            container.appendChild(itemElement);
        }
    });
}
