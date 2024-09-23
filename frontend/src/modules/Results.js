// src/modules/Results.js

import React from 'react';

export const displayError = (message) => {
  return <p className="error-message">{message}</p>;
};

const Results = ({ response }) => {
  const renderResults = () => {
    if (!Array.isArray(response) || response.length === 0) {
      return displayError("No results found.");
    }

    let hasResults = false;

    return response.map((queryResult, index) => {
      const results = queryResult.results;

      if (!results || Object.keys(results).length === 0) {
        return null; // Skip empty results
      }

      hasResults = true; // Mark that we have valid results

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
                <div className="results-content hidden">
                  {entries.map((entry) => {
                    const id = entry.id || "N/A";
                    const format = entry.format ? entry.format.join(", ") : "N/A";
                    const country = entry.country || "N/A";
                    const year = entry.year || "N/A";
                    const uri = entry.uri || "#";

                    const numberForSale = entry.numberForSale ?? 0;
                    const lowestPrice = entry.lowestPrice !== null && entry.lowestPrice !== undefined
                      ? `£${parseFloat(entry.lowestPrice).toFixed(2)}`
                      : "N/A";

                    return (
                      <div className="result-item" key={id}>
                        <h3>{entry.title || title}</h3>
                        <div className="details">
                          <p><strong>ID:</strong> {id}</p>
                          <p><strong>Formats:</strong> {format}</p>
                          <p><strong>Country:</strong> {country}</p>
                          <p><strong>Year:</strong> {year}</p>
                          <p><strong>URL:</strong> <a href={uri} target="_blank" rel="noopener noreferrer">{uri}</a></p>
                          <p><strong>Number For Sale:</strong> {numberForSale}</p>
                          <p><strong>Lowest Price:</strong> {lowestPrice}</p>
                        </div>
                      </div>
                    );
                  })}
                </div>
                <div
                  className="results-toggle-header"
                  onClick={(e) => {
                    const content = e.currentTarget.nextElementSibling;
                    content.classList.toggle("hidden");
                    e.currentTarget.textContent = content.classList.contains("hidden")
                      ? `Show Results for ${title}`
                      : `Hide Results for ${title}`;
                  }}
                >
                  Show Results for {title}
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
      {renderResults()}
    </div>
  );
};

export default Results;
