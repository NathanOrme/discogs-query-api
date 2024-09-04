// /js/modules/queryFields.js
import { discogsTypes, discogFormats, discogCountries } from './discogsData.js';

export function createQueryFields(queriesContainer, queryCounter, isFirstQuery = false) {
    const queryDiv = document.createElement('div');
    queryDiv.className = 'query';

    const queryHeader = document.createElement('div');
    queryHeader.className = 'query-header';

    // Create the Query Title
    const queryTitle = document.createElement('span');
    queryTitle.textContent = `Query ${queryCounter}`; // Add the query number
    queryHeader.appendChild(queryTitle);

    // Create the Delete button
    const deleteButton = document.createElement('button');
    deleteButton.className = 'delete-button';
    deleteButton.setAttribute('aria-label', 'Delete Query');

    // Add SVG for the delete button
    deleteButton.innerHTML = `
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M6 18L18 6M6 6l12 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
    `;

    deleteButton.addEventListener('click', () => {
        queryDiv.remove(); // Remove the entire query div
    });
    queryHeader.appendChild(deleteButton);

    // Add event listener to toggle query content visibility
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

    return queryCounter + 1;
}
