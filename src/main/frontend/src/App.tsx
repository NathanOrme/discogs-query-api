// src/App.tsx

import React, { useState } from 'react';
import './css/base.css';
import QueryFields from './modules/QueryFields';
import SearchForm from './modules/SearchForm';
import DarkModeToggle from './modules/DarkModeToggle';
import CheapestItem from './modules/CheapestItem';
import Results from './modules/Results';
import { Query, QueryResult } from './modules/types'; // Import the necessary types

const App: React.FC = () => {
  const [queries, setQueries] = useState<Query[]>([{ id: 1 }]);
  const [cheapestItems, setCheapestItems] = useState<any[]>([]);
  const [response, setResponse] = useState<QueryResult[]>([]); // Change type to QueryResult[]

  /**
   * Handles changes to the queries.
   *
   * @param {Array} newQueries - The updated array of queries.
   */
  const handleQueriesChange = (newQueries: Query[]) => {
    setQueries(newQueries);
  };

  return (
    <div className="App">
      <header>
        <h1>Discogs Query App</h1>
        <DarkModeToggle />
        <div className="instructions">
          <h2>How to Use This Search Tool</h2>
          <p>
            This tool allows you to search for music releases from the Discogs
            database. It will filter the results to show any that are sold in
            the UK.
          </p>
          <p>Follow these steps to get started:</p>
          <p>
            <strong>
              Note that you must supply at least the barcode or the artist
            </strong>
          </p>
          <ol>
            <li>
              <strong>Barcode: </strong>Enter the barcode for the product you
              are searching for.
            </li>
            <li>
              <strong>Artist:</strong> Enter the name of the artist you are
              searching for.
            </li>
            <li>
              <strong>Track:</strong> Enter the name of the track. This is
              optional, so you can leave it blank if not needed.
            </li>
            <li>
              <strong>Album:</strong> Enter the name of the Album. This is
              optional, so you can leave it blank if not needed.
            </li>
            <li>
              <strong>Format:</strong> Select the format of the release (e.g.,
              Vinyl, Compilation). This is optional.
            </li>
            <li>
              Click the <strong>Add Another Query</strong> button to add more
              search criteria. Each query will be processed separately.
            </li>
            <li>
              Add your <strong>Username</strong> to the search, if your discogs
              collection is public, so that the API can cross reference it.
            </li>
            <li>
              Click the <strong>Search</strong> button to submit your queries
              and see the results.
            </li>
          </ol>
        </div>
      </header>
      <main>
        <div className="query-section">
          <QueryFields onQueriesChange={handleQueriesChange} />
        </div>
        <div className="results-section">
          <SearchForm
            queries={queries}
            setResponse={setResponse}
            onCheapestItemsChange={setCheapestItems} // Correctly pass the handler
          />
          {response.length > 0 && <Results response={response} />}
        </div>
        <div className="cheapest-item-section">
          <CheapestItem items={cheapestItems} />
        </div>
        <div id="loading" style={{ display: 'none' }}>
          Loading...
        </div>
      </main>
    </div>
  );
};

export default App;
