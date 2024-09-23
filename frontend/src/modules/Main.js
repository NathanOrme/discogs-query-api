// src/components/Main.js
import React from 'react';
import { createQueryFields } from "./modules/queryFields"; // Adjust the import path accordingly
import { handleSearchFormSubmit } from "./modules/searchHandler"; // Adjust the import path accordingly

const Main = () => {
  const queriesContainerRef = React.useRef(null);
  const [queryCounter, setQueryCounter] = React.useState(1);

  React.useEffect(() => {
    const newCounter = createQueryFields(queriesContainerRef.current, queryCounter, true);
    setQueryCounter(newCounter);
  }, []);

  const addQuery = () => {
    const newCounter = createQueryFields(queriesContainerRef.current, queryCounter);
    setQueryCounter(newCounter);
  };

  return (
    <div className="container">
      <header>
        <h1>Discogs Search</h1>
        <button id="toggleDarkMode">Toggle Dark Mode</button>
      </header>
      <div className="instructions">
        {/* Instructions content goes here */}
      </div>
      <div className="form-container">
        <form id="searchForm" onSubmit={handleSearchFormSubmit}>
          <div ref={queriesContainerRef} id="queriesContainer"></div>
          <button type="button" onClick={addQuery}>Add Another Query</button>
          <button type="submit" id="searchButton">Search</button>
        </form>
        <div className="spinner" id="loading"></div>
      </div>
      <div id="cheapest-item-container"></div>
      <div className="results-container" id="results"></div>
    </div>
  );
};

export default Main;
