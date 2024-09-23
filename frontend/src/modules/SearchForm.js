// src/modules/SearchForm.js

import React, { useState } from 'react';
import Results from './Results.js';
import CheapestItem from './CheapestItem.js'; // Import the default export

const SearchForm = ({ queries }) => {
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

  const handleSearchFormSubmit = (event) => {
    event.preventDefault();
    setLoading(true);

    const apiUrl = getApiUrl();
    console.log("API URL:", apiUrl);
    console.log("Queries to submit:", queries);

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
        console.log("Response data received:", data);

        if (Array.isArray(data) && data.length > 0) {
          const cheapestItemsList = data.map(result => result.cheapestItem).filter(item => item !== null);
          setCheapestItems(cheapestItemsList);
          console.log("Cheapest items found:", cheapestItemsList);
        } else {
          setCheapestItems([]);
          console.log("No cheapest items found.");
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
      <button type="submit" disabled={loading}>{loading ? 'Loading...' : 'Search'}</button>
      {resultsData && <Results response={resultsData} />}
      {cheapestItems.length > 0 && <CheapestItem items={cheapestItems} />} {/* Use CheapestItem component */}
    </form>
  );
};

export default SearchForm;
