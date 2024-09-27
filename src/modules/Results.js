// src/modules/Results.js

import React, { useState } from 'react';
import { getApiUrl } from './Api'; // Import the getApiUrl function

// Function to download JSON data
const exportToJson = (data, filename) => {
  const jsonString = `data:text/json;charset=utf-8,${encodeURIComponent(
    JSON.stringify(data, null, 2)
  )}`;
  const link = document.createElement('a');
  link.href = jsonString;
  link.download = `${filename}.json`;

  link.click();
};

export const displayError = (message) => {
  return <p className="error-message">{message}</p>;
};

const Results = ({ response }) => {
  const [filteredResponse, setFilteredResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const callFilterUkMarketplace = async () => {
    setLoading(true);
    setError(null);

    const apiUrl = getApiUrl('filterUk'); // Get the filter UK endpoint
    console.log("Filtering UK marketplace with API URL:", apiUrl);

    try {
      const apiResponse = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(response), // Send original results to be filtered
      });

      if (!apiResponse.ok) {
        throw new Error('Failed to filter results');
      }

      const filteredData = await apiResponse.json();
      setFilteredResponse(filteredData); // Update state with filtered results
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const renderResults = (data) => {
    if (!Array.isArray(data) || data.length === 0) {
      return displayError('No results found.');
    }

    return data.map((queryResult, index) => {
      const results = queryResult.results;

      if (!results || Object.keys(results).length === 0) {
        return null; // Skip empty results
      }

      return (
        <div key={index}>
          <h2>Results for Query {index + 1}</h2>
          {Object.keys(results).map((title) => {
            const entries = results[title];

            if (!Array.isArray(entries) || entries.length === 0) {
              return null; // Skip empty entries
            }

            return (
              <div className="results-section" key={title}>
                <div
                  className="results-toggle-header"
                  onClick={(e) => {
                    const content = e.currentTarget.nextElementSibling;

                    // Safety check
                    if (content) {
                      content.classList.toggle('hidden');
                      e.currentTarget.textContent = content.classList.contains('hidden')
                        ? `Show Results for ${title}`
                        : `Hide Results for ${title}`;
                    } else {
                      console.error('Content not found for:', title);
                    }
                  }}
                >
                  Show Results for {title}
                </div>
                <div className="results-content hidden">
                  {entries.map((entry) => {
                    const id = entry.id || 'N/A';
                    const format = entry.format ? entry.format.join(', ') : 'N/A';
                    const country = entry.country || 'N/A';
                    const year = entry.year || 'N/A';
                    const uri = entry.uri || '#';

                    return (
                      <div className="result-item" key={id}>
                        <h3>{entry.title || title}</h3>
                        <div className="details">
                          <p><strong>ID:</strong> {id}</p>
                          <p><strong>Formats:</strong> {format}</p>
                          <p><strong>Country:</strong> {country}</p>
                          <p><strong>Year:</strong> {year}</p>
                          <p><strong>URL:</strong> <a href={uri} target="_blank" rel="noopener noreferrer">{uri}</a></p>
                          <p><strong>Number For Sale:</strong> {entry.numberForSale || 'N/A'}</p>
                          <p><strong>Lowest Price:</strong> {entry.lowestPrice !== null ? `£${parseFloat(entry.lowestPrice).toFixed(2)}` : 'N/A'}</p>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      );
    });
  };

  return (
    <div id="results">
      <button
        onClick={() => exportToJson(response, 'results')}
        className="export-button"
      >
        Export Results to JSON
      </button>

      {/* Button to call the UK marketplace filter */}
      <button
        onClick={callFilterUkMarketplace}
        className="filter-button"
        disabled={loading}
      >
        {loading ? 'Filtering...' : 'Filter UK Marketplace'}
      </button>

      {/* Render original or filtered results */}
      {error && displayError(error)}
      {filteredResponse ? renderResults(filteredResponse) : renderResults(response)}
    </div>
  );
};

export default Results;
