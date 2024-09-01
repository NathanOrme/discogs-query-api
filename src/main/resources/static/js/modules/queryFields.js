// /js/modules/queryFields.js
import { discogsTypes, discogFormats, discogCountries } from './discogsData.js';

export function createQueryFields(queriesContainer, queryCounter, isFirstQuery = false) {
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

    queryDiv.appendChild(queryHeader);
    queryDiv.appendChild(queryContent);

    const allQueryContents = document.querySelectorAll('.query-content');
    allQueryContents.forEach(content => {
        content.classList.add('hidden');
    });

    queriesContainer.appendChild(queryDiv);
    queryContent.classList.remove('hidden');

    return queryCounter + 1;
}
