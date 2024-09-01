// Enum values for DiscogsTypes
const discogsTypes = [
    { value: 'RELEASE', text: 'Release' },
    { value: 'MASTER', text: 'Master' },
    { value: '', text: 'Select a type' }
];

// Enum values for DiscogFormats
const discogFormats = [
    { value: '', text: 'Any Format' },
    { value: 'vinyl', text: 'Vinyl' },
    { value: 'album', text: 'Album' },
    { value: 'lp', text: 'LP' },
    { value: 'compilation', text: 'Compilation' },
    { value: 'album vinyl', text: 'Album Vinyl' },
    { value: 'compilation vinyl', text: 'Compilation Vinyl' }
];

// Enum values for DiscogCountries
const discogCountries = [
    { value: '', text: 'Any Country' },
    { value: 'EUROPE', text: 'Europe' },
    { value: 'UK', text: 'UK' },
    { value: 'US', text: 'US' },
    { value: 'COSTA_RICA', text: 'Costa Rica' },
    { value: 'CUBA', text: 'Cuba' },
    { value: 'ISRAEL', text: 'Israel' },
    { value: 'BRAZIL', text: 'Brazil' },
    { value: 'GREECE', text: 'Greece' },
    { value: 'URUGUAY', text: 'Uruguay' },
    { value: 'LATVIA', text: 'Latvia' },
    { value: 'AUSTRIA', text: 'Austria' },
    { value: 'ALBANIA', text: 'Albania' },
    { value: 'AUSTRALIA', text: 'Australia' },
    { value: 'NEW_ZEALAND', text: 'New Zealand' },
    { value: 'PARAGUAY', text: 'Paraguay' },
    { value: 'PERU', text: 'Peru' },
    { value: 'HUNGARY', text: 'Hungary' },
    { value: 'NETHERLANDS', text: 'Netherlands' },
    { value: 'CANADA', text: 'Canada' },
    { value: 'HONG_KONG', text: 'Hong Kong' },
    { value: 'TRINIDAD', text: 'Trinidad' },
    { value: 'TOBAGO', text: 'Tobago' },
    { value: 'ESTONIA', text: 'Estonia' },
    { value: 'FIJI', text: 'Fiji' },
    { value: 'PHILIPPINES', text: 'Philippines' },
    { value: 'GERMANY', text: 'Germany' },
    { value: 'GEORGIA', text: 'Georgia' },
    { value: 'NORWAY', text: 'Norway' },
    { value: 'CROATIA', text: 'Croatia' },
    { value: 'IRELAND', text: 'Ireland' },
    { value: 'INDIA', text: 'India' },
    { value: 'JAPAN', text: 'Japan' },
    { value: 'INDONESIA', text: 'Indonesia' },
    { value: 'DENMARK', text: 'Denmark' },
    { value: 'SOUTH_KOREA', text: 'South Korea' },
    { value: 'LITHUANIA', text: 'Lithuania' },
    { value: 'MALAYSIA', text: 'Malaysia' },
    { value: 'CHINA', text: 'China' },
    { value: 'KENYA', text: 'Kenya' },
    { value: 'TURKEY', text: 'Turkey' },
    { value: 'BULGARIA', text: 'Bulgaria' },
    { value: 'TAIWAN', text: 'Taiwan' },
    { value: 'SWEDEN', text: 'Sweden' },
    { value: 'FINLAND', text: 'Finland' },
    { value: 'ICELAND', text: 'Iceland' },
    { value: 'CZECH_REPUBLIC', text: 'Czech Republic' },
    { value: 'RUSSIAN_FEDERATION', text: 'Russian Federation' },
    { value: 'BELGIUM', text: 'Belgium' },
    { value: 'FRANCE', text: 'France' },
    { value: 'LUXEMBOURG', text: 'Luxembourg' },
    { value: 'MEXICO', text: 'Mexico' },
    { value: 'VENEZUELA', text: 'Venezuela' },
    { value: 'ARGENTINA', text: 'Argentina' },
    { value: 'SOUTH_AFRICA', text: 'South Africa' },
    { value: 'COLOMBIA', text: 'Colombia' },
    { value: 'SLOVENIA', text: 'Slovenia' },
    { value: 'CHILE', text: 'Chile' },
    { value: 'ITALY', text: 'Italy' },
    { value: 'THE_VATICAN', text: 'The Vatican' },
    { value: 'SAN_MARINO', text: 'San Marino' },
    { value: 'SERBIA', text: 'Serbia' },
    { value: 'MONTENEGRO', text: 'Montenegro' },
    { value: 'SLOVAKIA', text: 'Slovakia' },
    { value: 'PORTUGAL', text: 'Portugal' },
    { value: 'SWITZERLAND', text: 'Switzerland' },
    { value: 'LIECHTENSTEIN', text: 'Liechtenstein' },
    { value: 'UKRAINE', text: 'Ukraine' },
    { value: 'ROMANIA', text: 'Romania' },
    { value: 'POLAND', text: 'Poland' }
];

const queriesContainer = document.getElementById('queriesContainer');
const addQueryButton = document.getElementById('addQueryButton');

let queryCounter = 1; // Counter to number the queries

function createQueryFields(isFirstQuery = false) {
    const queryDiv = document.createElement('div');
    queryDiv.className = 'query';

    const queryHeader = document.createElement('div');
    queryHeader.className = 'query-header';
    queryHeader.textContent = `Query ${queryCounter}`; // Add the query number
    queryHeader.addEventListener('click', () => {
        const content = queryDiv.querySelector('.query-content');
        content.classList.toggle('hidden');
    });

    const queryContent = document.createElement('div');
    queryContent.className = 'query-content';

    queryContent.innerHTML = `
        <label for="artist">Artist:</label>
        <input type="text" class="artist" name="artist" required>

        <label for="album">Album (optional):</label>
        <input type="text" class="album" name="album">

        <label for="track">Track (optional):</label>
        <input type="text" class="track" name="track">

        <label for="format">Format (optional):</label>
        <select class="format" name="format">
            ${discogFormats.map(format => `<option value="${format.value}">${format.text}</option>`).join('')}
        </select>

        <label for="country">Country (optional):</label>
        <select class="country" name="country">
            ${discogCountries.map(country => `<option value="${country.value}">${country.text}</option>`).join('')}
        </select>

        <label for="types">Types (optional):</label>
        <select class="types" name="types">
            ${discogsTypes.map(type => `<option value="${type.value}">${type.text}</option>`).join('')}
        </select>

        <label for="checkMarketplace">Check Marketplace:</label>
        <input type="checkbox" class="checkMarketplace" name="checkMarketplace">
    `;

    // Append elements to the queryDiv
    queryDiv.appendChild(queryHeader);
    queryDiv.appendChild(queryContent);

    // Ensure all other query contents are hidden except for the new one
    const allQueryContents = document.querySelectorAll('.query-content');
    allQueryContents.forEach(content => {
        content.classList.add('hidden');
    });

    // Append the queryDiv to the container and ensure the new query is open
    queriesContainer.appendChild(queryDiv);
    queryContent.classList.remove('hidden'); // Keep the new query open

    queryCounter++; // Increment the query counter
}

// Add the initial query fields and keep it visible
createQueryFields(true);

addQueryButton.addEventListener('click', () => {
    createQueryFields(); // Create a new query field set that remains open
});

document.getElementById('searchForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const queries = Array.from(document.querySelectorAll('.query')).map(queryDiv => {
        const artist = queryDiv.querySelector('.artist').value;
        const album = queryDiv.querySelector('.album').value || null;
        const track = queryDiv.querySelector('.track').value || null;
        const format = queryDiv.querySelector('.format').value || null;
        const country = queryDiv.querySelector('.country').value || null;
        const types = queryDiv.querySelector('.types').value || null;
        const checkMarketplace = queryDiv.querySelector('.checkMarketplace').checked;

        return {
            artist: artist,
            album: album,
            track: track,
            format: format,
            country: country,
            types: types,
            checkMarketplace: checkMarketplace
        };
    });

    console.log('Submitting Queries:', queries); // Debugging line

    const loadingSpinner = document.getElementById('loading');
    const searchButton = document.getElementById('searchButton');

    // Disable the search button and show the spinner
    searchButton.disabled = true;
    loadingSpinner.style.display = 'block';

    fetch('http://localhost:9090/discogs-query/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic dXNlcm5hbWU6cGFzc3dvcmQ='
        },
        body: JSON.stringify(queries) // Sending a list of queries
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorMessage => {
                throw new Error(`Server responded with status ${response.status}: ${errorMessage}`);
            });
        }
        return response.json();
    })
    .then(data => {
        console.log('API Response:', data);
        displayResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
        displayError('There was an issue with your request. Please try again later.');
    })
    .finally(() => {
        // Re-enable the search button and hide the spinner
        searchButton.disabled = false;
        loadingSpinner.style.display = 'none';
    });
});

function displayResults(response) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = ''; // Clear previous results

    // Check if the response is an array and has at least one element
    if (!Array.isArray(response) || response.length === 0) {
        resultsContainer.innerHTML = '<p>No results found.</p>';
        return;
    }

    // Iterate through each query's results in the response
    response.forEach((queryResult, index) => {
        const results = queryResult.results;

        // If no results for this query, skip it
        if (!Array.isArray(results) || results.length === 0) {
            return;
        }

        // Create a header for each query's results
        const queryHeader = document.createElement('h2');
        queryHeader.textContent = `Results for Query ${index + 1}`;
        resultsContainer.appendChild(queryHeader);

        // Create a collapsible section for each query's results
        const resultsSection = document.createElement('div');
        resultsSection.className = 'results-section';

        const resultsContent = document.createElement('div');
        resultsContent.className = 'results-content hidden';

        results.forEach(entry => {
            // Ensure each entry has the expected properties
            const title = entry.title || 'No Title';
            const id = entry.id || 'N/A';
            const format = entry.format ? entry.format.join(', ') : 'N/A';
            const country = entry.country || 'N/A';
            const year = entry.year || 'N/A';
            const uri = entry.uri || '#';
            const lowestPrice = entry.lowestPrice ? '£' + entry.lowestPrice.toFixed(2) : 'N/A';
            const onMarketplace = entry.isOnMarketplace ? 'Yes' : 'No';

            // Only display "On Marketplace" and "Lowest Price" if `isOnMarketplace` is true
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

        // Add a clickable header to toggle the visibility of the results
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

function displayError(message) {
    const resultsContainer = document.getElementById('results');
    resultsContainer.innerHTML = `<p class="error-message">${message}</p>`;
}
