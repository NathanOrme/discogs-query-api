// src/App.js
import React, { useState } from 'react';
import './css/base.css';
import './css/components.css';
import './css/themes.css';
import QueryFields from './modules/QueryFields';
import SearchForm from './modules/SearchForm';
import DarkModeToggle from './modules/DarkModeToggle';
import CheapestItem from './modules/CheapestItem';
import Results from './modules/Results';

function App() {
  const [queries, setQueries] = useState([{ id: 1 }]);
  const [cheapestItems, setCheapestItems] = useState([]);
  const [response, setResponse] = useState([]);

  const handleQueriesChange = (newQueries) => {
    setQueries(newQueries);
  };

  return (
    <div className="App">
      <header>
        <h1>Discogs Query App</h1>
        <DarkModeToggle />
        {/* Instructions here */}
      </header>
      <main>
        <div className="query-section">
          <QueryFields onQueriesChange={handleQueriesChange} />
        </div>
        <div className="results-section">
          <SearchForm 
            queries={queries} 
            setResponse={setResponse} 
            setCheapestItems={setCheapestItems} // Pass the handler
          />
        </div>
        <div className="results-section">
          <Results response={response} />
        </div>
        <div className="cheapest-item-section">
          <CheapestItem items={cheapestItems} />
        </div>
        <div id="loading" style={{ display: 'none' }}>Loading...</div>
      </main>
    </div>
  );
}

export default App;
