// src/Search.js
import React, { useState } from 'react';
import axios from 'axios';

function Search() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);

    const handleSearch = async () => {
        const queries = query.split('\n').map(q => ({ query: q }));

        try {
            const response = await axios.get('http://localhost:8080/discogs-query/search', {
                headers: {
                    'Content-Type': 'application/json',
                },
                data: queries
            });

            setResults(response.data);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    };

    return (
        <div>
            <h1>Search Discogs</h1>
            <textarea
                rows="4"
                cols="50"
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Enter your query here..."
            />
            <br />
            <button onClick={handleSearch}>Search</button>
            <h2>Results:</h2>
            <div>
                {results.map((result, index) => (
                    <pre key={index}>{JSON.stringify(result, null, 2)}</pre>
                ))}
            </div>
        </div>
    );
}

export default Search;