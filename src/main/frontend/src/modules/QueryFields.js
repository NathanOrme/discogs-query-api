// src/modules/QueryFields.js

import React, { useState, useEffect } from "react";
import { discogsTypes, discogFormats, discogCountries } from "./DiscogsData";

/**
 * QueryFields component allows users to input multiple queries
 * for searching Discogs.
 *
 * @param {Function} onQueriesChange - Callback function to handle queries change.
 * @returns {JSX.Element} The rendered QueryFields component.
 */
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

  /**
   * Handles input changes in the query fields.
   *
   * @param {number} index - The index of the query being updated.
   * @param {string} field - The field that is being updated.
   * @param {string} value - The new value for the field.
   */
  const handleInputChange = (index, field, value) => {
    const updatedQueries = queries.map((query, i) => {
      if (i === index) {
        return { ...query, [field]: value }; // Update the specific field
      }
      return query;
    });
    setQueries(updatedQueries);
  };

  /**
   * Adds a new query to the list of queries.
   */
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

  /**
   * Removes a query from the list based on the provided index.
   *
   * @param {number} index - The index of the query to be removed.
   */
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

  /**
   * Renders input fields for a single query.
   *
   * @param {Object} query - The query object to render.
   * @param {number} index - The index of the query being rendered.
   * @returns {JSX.Element} The rendered query input fields.
   */
  const renderQueryFields = (query, index) => (
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
        {renderInputField("Artist:", "artist", query.artist, index)}
        {renderInputField("Barcode:", "barcode", query.barcode, index)}
        {renderInputField("Album (optional):", "album", query.album, index)}
        {renderInputField("Track (optional):", "track", query.track, index)}
        {renderSelectField("Format (optional):", "format", query.format, discogFormats, index)}
        {renderSelectField("Country (optional):", "country", query.country, discogCountries, index)}
        {renderSelectField("Types (optional):", "types", query.types, discogsTypes, index)}
      </div>
    </div>
  );

  /**
   * Renders a text input field.
   *
   * @param {string} label - The label for the input field.
   * @param {string} field - The field name.
   * @param {string} value - The current value of the field.
   * @param {number} index - The index of the query.
   * @returns {JSX.Element} The rendered input field.
   */
  const renderInputField = (label, field, value, index) => (
    <>
      <label htmlFor={`${field}-${index}`}>{label}</label>
      <input
        type="text"
        className={field}
        name={`${field}-${index}`}
        value={value}
        onChange={(e) => handleInputChange(index, field, e.target.value)} // Update state on change
      />
    </>
  );

  /**
   * Renders a select dropdown field.
   *
   * @param {string} label - The label for the select field.
   * @param {string} field - The field name.
   * @param {string} value - The current value of the field.
   * @param {Array} options - The options for the select dropdown.
   * @param {number} index - The index of the query.
   * @returns {JSX.Element} The rendered select dropdown.
   */
  const renderSelectField = (label, field, value, options, index) => (
    <>
      <label htmlFor={`${field}-${index}`}>{label}</label>
      <select
        className={field}
        name={`${field}-${index}`}
        value={value}
        onChange={(e) => handleInputChange(index, field, e.target.value)} // Update state on change
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.text}
          </option>
        ))}
      </select>
    </>
  );

  return (
    <form>
      {queries.map((query, index) => renderQueryFields(query, index))}
      <button type="button" onClick={addQuery}>
        Add Query
      </button>
    </form>
  );
};

export default QueryFields;
