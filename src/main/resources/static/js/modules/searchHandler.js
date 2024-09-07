// /js/modules/searchHandler.js
import { displayResults, displayError } from './resultsHandler.js';

// Your existing code...

function handleSearchFormSubmit(event) {
    event.preventDefault();

    const queries = Array.from(document.querySelectorAll('.query')).map(queryDiv => {
        const artist = queryDiv.querySelector('.artist').value;
        const barcode = queryDiv.querySelector('.barcode').value || null;
        const album = queryDiv.querySelector('.album').value || null;
        const track = queryDiv.querySelector('.track').value || null;
        const format = queryDiv.querySelector('.format').value || null;
        const country = queryDiv.querySelector('.country').value || null;
        const types = queryDiv.querySelector('.types').value || null;

        return {
            artist: artist,
            barcode: barcode,
            album: album,
            track: track,
            format: format,
            country: country,
            types: types,
        };
    });

    console.log('Submitting Queries:', queries);

    const loadingSpinner = document.getElementById('loading');
    const searchButton = document.getElementById('searchButton');

    searchButton.disabled = true;
    loadingSpinner.style.display = 'block';

    fetch('https://discogs-query-api.onrender.com/discogs-query/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic dXNlcm5hbWU6cGFzc3dvcmQ='
        },
        body: JSON.stringify(queries)
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
        searchButton.disabled = false;
        loadingSpinner.style.display = 'none';
    });
}

export { handleSearchFormSubmit };
