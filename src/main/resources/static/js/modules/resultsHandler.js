// /js/modules/resultsHandler.js
export function displayResults(response) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = '';

    if (!Array.isArray(response) || response.length === 0) {
        resultsContainer.innerHTML = '<p>No results found.</p>';
        return;
    }

    response.forEach((queryResult, index) => {
        const results = queryResult.results;

        if (!Array.isArray(results) || results.length === 0) {
            return;
        }

        const queryHeader = document.createElement('h2');
        queryHeader.textContent = `Results for Query ${index + 1}`;
        resultsContainer.appendChild(queryHeader);

        const resultsSection = document.createElement('div');
        resultsSection.className = 'results-section';

        const resultsContent = document.createElement('div');
        resultsContent.className = 'results-content hidden';

        results.forEach(entry => {
            const title = entry.title || 'No Title';
            const id = entry.id || 'N/A';
            const format = entry.format ? entry.format.join(', ') : 'N/A';
            const country = entry.country || 'N/A';
            const year = entry.year || 'N/A';
            const uri = entry.uri || '#';
            const lowestPrice = entry.lowestPrice ? '£' + entry.lowestPrice.toFixed(2) : 'N/A';
            const onMarketplace = entry.isOnMarketplace ? 'Yes' : 'No';

            const marketplaceDetails = entry.isOnMarketplace ? `
                <p><strong>On Marketplace:</strong> ${onMarketplace}</p>
                <p><strong>Lowest Price:</strong> ${lowestPrice}</p>
            ` : '';

            const resultItem = document.createElement('div');
            resultItem.className = 'result-item';

            resultItem.innerHTML = `
                <h3>${title}</h3>
                <div class="details">
                    <p><strong>ID:</strong> ${id}</p>
                    <p><strong>Formats:</strong> ${format}</p>
                    <p><strong>Country:</strong> ${country}</p>
                    <p><strong>Year:</strong> ${year}</p>
                    <p><strong>URL:</strong> <a href="${uri}" target="_blank">${uri}</a></p>
                    ${marketplaceDetails}
                </div>
            `;

            resultsContent.appendChild(resultItem);
        });

        resultsSection.appendChild(resultsContent);

        const toggleHeader = document.createElement('div');
        toggleHeader.className = 'results-toggle-header';
        toggleHeader.textContent = 'Show Results';
        toggleHeader.addEventListener('click', () => {
            resultsContent.classList.toggle('hidden');
            toggleHeader.textContent = resultsContent.classList.contains('hidden') ? 'Show Results' : 'Hide Results';
        });

        resultsSection.insertBefore(toggleHeader, resultsContent);

        resultsContainer.appendChild(resultsSection);
    });
}

export function displayError(message) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = `<p class="error-message">${message}</p>`;
}
