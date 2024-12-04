import React, { useState, FormEvent } from 'react';

interface SearchFormProps {
  queries: Array<{
    artist?: string;
    barcode?: string;
    album?: string;
    track?: string;
    format?: string;
    types?: string;
  }>;
  setResponse: (response: any) => void;
  onCheapestItemsChange: (cheapestItems: Array<any>) => void;
}

const SearchForm: React.FC<SearchFormProps> = ({
  queries,
  setResponse,
  onCheapestItemsChange,
}) => {
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState(''); // New username state

  const getApiUrl = (): string => {
    if (typeof window === 'undefined') {
      throw new Error('window is not defined');
    }
    const hostname = window.location.hostname;
    const urlMapping: Record<string, string> = {
      netlify: 'https://theboot-1001-albums.onrender.com/discogs-query/search',
      'rgbnathan-discogs-api':
        'https://theboot-1001-albums.onrender.com/discogs-query/search',
    };

    for (const [key, url] of Object.entries(urlMapping)) {
      if (hostname.includes(key)) {
        return url;
      }
    }

    return 'http://localhost:9090/discogs-query/search';
  };

  const handleSearchFormSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);

    const apiUrl = getApiUrl();
    const payload = {
      username: username || undefined, // Only include username if provided
      queries,
    };

    fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    })
      .then((response) => {
        if (!response.ok) {
          return response.text().then((errorMessage) => {
            throw new Error(
              `Server responded with status ${response.status}: ${errorMessage}`
            );
          });
        }
        return response.json();
      })
      .then((data) => {
        setResponse(data);
        if (Array.isArray(data) && data.length > 0) {
          const cheapestItemsList = data
            .map((result: any) => result.cheapestItem)
            .filter((item: any) => item !== null);
          onCheapestItemsChange(cheapestItemsList);
        } else {
          onCheapestItemsChange([]);
        }
      })
      .catch((error) => {
        console.error('Error:', error);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <form onSubmit={handleSearchFormSubmit}>
      <input
        type="text"
        placeholder="Enter your username"
        value={username}
        onChange={(e) => setUsername(e.target.value)} // Update username state
      />
      <button type="submit" disabled={loading}>
        {loading ? 'Loading...' : 'Search'}
      </button>
    </form>
  );
};

export default SearchForm;
