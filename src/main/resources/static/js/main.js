import { createQueryFields } from './modules/queryFields.js';
import { handleSearchFormSubmit } from './modules/searchHandler.js';
import { displayResults, displayError } from './modules/resultsHandler.js';

const queriesContainer = document.getElementById('queriesContainer');
const addQueryButton = document.getElementById('addQueryButton');

let queryCounter = 1;

createQueryFields(queriesContainer, queryCounter, true);

addQueryButton.addEventListener('click', () => {
    queryCounter = createQueryFields(queriesContainer, queryCounter);
});

document.getElementById('searchForm').addEventListener('submit', handleSearchFormSubmit);