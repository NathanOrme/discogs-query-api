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
    { value: 'compilation', text: 'Compilation' },
    { value: 'compilation vinyl', text: 'Compilation Vinyl' }
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

        <label for="track">Track (optional):</label>
        <input type="text" class="track" name="track">

        <label for="format">Format (optional):</label>
        <select class="format" name="format">
            ${discogFormats.map(format => `<option value="${format.value}">${format.text}</option>`).join('')}
        </select>

        <label for="types">Types (optional):</label>
        <select class="types" name="types">
            ${discogsTypes.map(type => `<option value="${type.value}">${type.text}</option>`).join('')}
        </select>
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
        const track = queryDiv.querySelector('.track').value || null; // Make track optional
        const format = queryDiv.querySelector('.format').value || null;
        const types = queryDiv.querySelector('.types').value || null;

        return {
            artist: artist,
            track: track,
            format: format,
            types: types
        };
    });

    console.log('Submitting Queries:', queries); // Debugging line

    const loadingSpinner = document.getElementById('loading');
    const searchButton = document.getElementById('searchButton');

    // Disable the search button and show the spinner
    searchButton.disabled = true;
    loadingSpinner.style.display = 'block';

    fetch('https://discogs-query-api.onrender.com/discogs-query/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic dXNlcm5hbWU6cGFzc3dvcmQ='
        },
        body: JSON.stringify(queries) // Sending a list of queries
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        console.log('API Response:', data); // Check if this is an array
        displayResults(data);
    })
    .catch(error => {
        console.error('Error:', error);
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

    // Extract the results array from the response
    const results = response[0].results;

    if (!Array.isArray(results) || results.length === 0) {
        resultsContainer.innerHTML = '<p>No results found.</p>';
        return;
    }

    results.forEach(entry => {
        // Ensure each entry has the expected properties
        const title = entry.title || 'No Title';
        const id = entry.id || 'N/A';
        const format = entry.format ? entry.format.join(', ') : 'N/A';
        const uri = entry.uri || '#';
        const lowestPrice = entry.lowestPrice ? '£' + entry.lowestPrice.toFixed(2) : 'N/A';
        const onMarketplace = entry.onMarketplace ? 'Yes' : 'No';

        const resultItem = document.createElement('div');
        resultItem.className = 'result-item';

        resultItem.innerHTML = `
            <h3>${title}</h3>
            <div class="details">
                <p><strong>ID:</strong> ${id}</p>
                <p><strong>Formats:</strong> ${format}</p>
                <p><strong>URL:</strong> <a href="${uri}" target="_blank">${uri}</a></p>
                <p><strong>Lowest Price:</strong> ${lowestPrice}</p>
                <p><strong>On Marketplace:</strong> ${onMarketplace}</p>
            </div>
        `;

        resultsContainer.appendChild(resultItem);
    });
}
