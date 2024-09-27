// src/modules/SearchForm.js

import React, { useState } from 'react';
import { getApiUrl } from './Api'; // Import the getApiUrl function

const SearchForm = ({ queries, setResponse, onCheapestItemsChange }) => {
  const [loading, setLoading] = useState(false);

  const handleSearchFormSubmit = (event) => {
    event.preventDefault();
    setLoading(true);

    // Use the search API URL
    const apiUrl = getApiUrl('search');
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
