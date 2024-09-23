// src/components/SearchForm.js

import React, { useState } from 'react';
import Results from './Results.js';
import CheapestItem from './CheapestItem.js'; // Import the default export

const SearchForm = () => {
  const [queries, setQueries] = useState([{ artist: '', barcode: '', album: '', track: '', format: '', country: '', types: '' }]);
  const [loading, setLoading] = useState(false);
  const [resultsData, setResultsData] = useState(null);
  const [cheapestItems, setCheapestItems] = useState([]);

  const getApiUrl = () => {
    const hostname = window.location.hostname;

    const urlMapping = {
      render: "https://discogs-query-api.onrender.com/discogs-query/search",
      koyeb: "https://discogs-query-api-rgbnathan.koyeb.app/discogs-query/search",
      b4a: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/search"
    };

    for (const [key, url] of Object.entries(urlMapping)) {
      if (hostname.includes(key)) {
        return url;
      }
    }

    return "http://localhost:9090/discogs-query/search";
  };

  const handleQueryChange = (index, event) => {
    const { name, value } = event.target;
    const newQueries = [...queries];
    newQueries[index][name] = value;
    setQueries(newQueries);
  };

  const handleAddQuery = () => {
    setQueries([...queries, { artist: '', barcode: '', album: '', track: '', format: '', country: '', types: '' }]);
  };

  const handleRemoveQuery = (index) => {
    const newQueries = queries.filter((_, i) => i !== index);
    setQueries(newQueries);
  };

  const handleSearchFormSubmit = (event) => {
    event.preventDefault();
    setLoading(true);

    const apiUrl = getApiUrl();

    fetch(apiUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Basic dXNlcm5hbWU6cGFzc3dvcmQ=",
      },
      body: JSON.stringify(queries),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((errorMessage) => {
            throw new Error(`Server responded with status ${response.status}: ${errorMessage}`);
          });
        }
        return response.json();
      })
      .then((data) => {
        setResultsData(data);

        if (Array.isArray(data) && data.length > 0) {
          const cheapestItemsList = data.map(result => result.cheapestItem).filter(item => item !== null);
          setCheapestItems(cheapestItemsList);
        } else {
          setCheapestItems([]);
        }
      })
      .catch((error) => {
        console.error("Error:", error);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <form onSubmit={handleSearchFormSubmit}>
      {queries.map((query, index) => (
        <div className="query" key={index}>
          <input
            type="text"
            name="artist"
            placeholder="Artist"
            value={query.artist}
            onChange={(e) => handleQueryChange(index, e)}
          />
          <input
            type="text"
            name="barcode"
            placeholder="Barcode"
            value={query.barcode}
            onChange={(e) => handleQueryChange(index, e)}
          />
          <input
            type="text"
            name="album"
            placeholder="Album (optional)"
            value={query.album}
            onChange={(e) => handleQueryChange(index, e)}
          />
          <input
            type="text"
            name="track"
            placeholder="Track (optional)"
            value={query.track}
            onChange={(e) => handleQueryChange(index, e)}
          />
          <select
            name="format"
            value={query.format}
            onChange={(e) => handleQueryChange(index, e)}
          >
            <option value="">Format (optional)</option>
            {/* Map formats from your data */}
          </select>
          <select
            name="country"
            value={query.country}
            onChange={(e) => handleQueryChange(index, e)}
          >
            <option value="">Country (optional)</option>
            {/* Map countries from your data */}
          </select>
          <select
            name="types"
            value={query.types}
            onChange={(e) => handleQueryChange(index, e)}
          >
            <option value="">Types (optional)</option>
            {/* Map types from your data */}
          </select>
          <button type="button" onClick={() => handleRemoveQuery(index)}>Remove Query</button>
        </div>
      ))}
      <button type="button" onClick={handleAddQuery}>Add Query</button>
      <button type="submit" disabled={loading}>{loading ? 'Loading...' : 'Search'}</button>
      {resultsData && <Results response={resultsData} />}
      {cheapestItems.length > 0 && <CheapestItem items={cheapestItems} />} {/* Use CheapestItem component */}
    </form>
  );
};

export default SearchForm;
