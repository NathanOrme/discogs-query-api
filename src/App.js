import React, { useState } from 'react';
import './App.css';

function App() {
  const [queries, setQueries] = useState([{ artist: '', track: '', format: '', types: '' }]);
  const [results, setResults] = useState([]);

  const handleChange = (index, event) => {
    const values = [...queries];
    values[index][event.target.name] = event.target.value;
    setQueries(values);
  };

  const handleAddQuery = () => {
    setQueries([...queries, { artist: '', track: '', format: '', types: '' }]);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    fetch('http://localhost:9090/discogs-query/search', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic dXNlcm5hbWU6cGFzc3dvcmQ=',
      },
      body: JSON.stringify(queries),
    })
      .then((response) => response.json())
      .then((data) => {
        setResults(data);
      })
      .catch((error) => {
        console.error('Error:', error);
      });
  };

  return (
    <div className="App">
      <h1>Discogs Search</h1>
      <form onSubmit={handleSubmit}>
        {queries.map((query, index) => (
          <div key={index} className="query-block">
            <label>
              Artist:
              <input
                type="text"
                name="artist"
                value={query.artist}
                onChange={(event) => handleChange(index, event)}
                required
              />
            </label>
            <label>
              Track:
              <input
                type="text"
                name="track"
                value={query.track}
                onChange={(event) => handleChange(index, event)}
                required
              />
            </label>
            <label>
              Format (optional):
              <input
                type="text"
                name="format"
                value={query.format}
                onChange={(event) => handleChange(index, event)}
              />
            </label>
            <label>
              Types (optional):
              <select
                name="types"
                value={query.types}
                onChange={(event) => handleChange(index, event)}
              >
                <option value="">Select a type</option>
                <option value="ALBUM">Album</option>
                <option value="SINGLE">Single</option>
                <option value="EP">EP</option>
                {/* Add more options based on DiscogsTypes */}
              </select>
            </label>
          </div>
        ))}
        <button type="button" onClick={handleAddQuery}>
          Add Another Query
        </button>
        <button type="submit">Search</button>
      </form>

      <div className="results">
        <h2>Results</h2>
        <ul>
          {results.map((result, index) => (
            <li key={index}>
              {/* Adjust this based on DiscogsResultDTO */}
              {JSON.stringify(result)}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default App;