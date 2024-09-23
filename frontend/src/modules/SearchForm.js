// src/modules/SearchForm.js

import React, { useState } from 'react';

const SearchForm = ({ queries, setResponse, onCheapestItemsChange }) => {
  const [loading, setLoading] = useState(false);

  const getApiUrl = () => {
    const hostname = window.location.hostname;
    const urlMapping = {
      render: "https://discogs-query-api.onrender.com/discogs-query/search",
      koyeb: "https://discogs-query-api-rgbnathan.koyeb.app/discogs-query/search"
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
        setResponse(data); // Pass response back to App.js
        console.log("Response data received:", data);

        if (Array.isArray(data) && data.length > 0) {
          const cheapestItemsList = data.map(result => result.cheapestItem).filter(item => item !== null);
          onCheapestItemsChange(cheapestItemsList); // Update cheapest items in App.js
          console.log("Cheapest items found:", cheapestItemsList);
        } else {
          onCheapestItemsChange([]); // Clear cheapest items if none found
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
    </form>
  );
};

export default SearchForm;
