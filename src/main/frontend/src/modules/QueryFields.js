// src/modules/QueryFields.js

import React, { useState, useEffect } from "react";
import { discogsTypes, discogFormats, discogCountries } from "./DiscogsData";

const QueryFields = ({ onQueriesChange }) => {
  const [queries, setQueries] = useState([
    {
      artist: "",
      barcode: "",
      album: "",
      track: "",
      format: "",
      country: "",
      types: "",
    },
  ]);

  const [queryCounter, setQueryCounter] = useState(1);

  useEffect(() => {
    onQueriesChange(queries);
  }, [queries, onQueriesChange]);

  const handleInputChange = (index, field, value) => {
    const updatedQueries = queries.map((query, i) => {
      if (i === index) {
        return { ...query, [field]: value }; // Update the specific field
      }
      return query;
    });
    setQueries(updatedQueries);
  };

  const addQuery = () => {
    const newQuery = {
      artist: "",
      barcode: "",
      album: "",
      track: "",
      format: "",
      country: "",
      types: "",
    };
    setQueries([...queries, newQuery]);
    setQueryCounter(queryCounter + 1);
    console.log("Added new query:", newQuery);
  };

  const removeQuery = (index) => {
    const updatedQueries = queries.filter((_, i) => i !== index);
    setQueries(updatedQueries);
    console.log(
      "Removed query at index:",
      index,
      "Remaining queries:",
      updatedQueries,
    );
  };

  return (
    <form>
      {queries.map((query, index) => (
        <div className="query" key={index}>
          <div className="query-header">
            <span>Query {index + 1}</span>
            {queries.length > 1 && (
              <button
                type="button"
                className="delete-button"
                onClick={() => removeQuery(index)}
              >
                Remove
              </button>
            )}
          </div>
          <div className="query-content">
            <label htmlFor={`artist-${index}`}>Artist:</label>
            <input
              type="text"
              className="artist"
              name={`artist-${index}`}
              value={query.artist}
              onChange={(e) =>
                handleInputChange(index, "artist", e.target.value)
              } // Update state on change
            />

            <label htmlFor={`barcode-${index}`}>Barcode:</label>
            <input
              type="text"
              className="barcode"
              name={`barcode-${index}`}
              value={query.barcode}
              onChange={(e) =>
                handleInputChange(index, "barcode", e.target.value)
              } // Update state on change
            />

            <label htmlFor={`album-${index}`}>Album (optional):</label>
            <input
              type="text"
              className="album"
              name={`album-${index}`}
              value={query.album}
              onChange={(e) =>
                handleInputChange(index, "album", e.target.value)
              } // Update state on change
            />

            <label htmlFor={`track-${index}`}>Track (optional):</label>
            <input
              type="text"
              className="track"
              name={`track-${index}`}
              value={query.track}
              onChange={(e) =>
                handleInputChange(index, "track", e.target.value)
              } // Update state on change
            />

            <label htmlFor={`format-${index}`}>Format (optional):</label>
            <select
              className="format"
              name={`format-${index}`}
              value={query.format}
              onChange={(e) =>
                handleInputChange(index, "format", e.target.value)
              } // Update state on change
            >
              {discogFormats.map((format) => (
                <option key={format.value} value={format.value}>
                  {format.text}
                </option>
              ))}
            </select>

            <label htmlFor={`country-${index}`}>Country (optional):</label>
            <select
              className="country"
              name={`country-${index}`}
              value={query.country}
              onChange={(e) =>
                handleInputChange(index, "country", e.target.value)
              } // Update state on change
            >
              {discogCountries.map((country) => (
                <option key={country.value} value={country.value}>
                  {country.text}
                </option>
              ))}
            </select>

            <label htmlFor={`types-${index}`}>Types (optional):</label>
            <select
              className="types"
              name={`types-${index}`}
              value={query.types}
              onChange={(e) =>
                handleInputChange(index, "types", e.target.value)
              } // Update state on change
            >
              {discogsTypes.map((type) => (
                <option key={type.value} value={type.value}>
                  {type.text}
                </option>
              ))}
            </select>
          </div>
        </div>
      ))}
      <button type="button" onClick={addQuery}>
        Add Query
      </button>
    </form>
  );
};

export default QueryFields;
