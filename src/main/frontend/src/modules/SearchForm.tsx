import React, { useState, FormEvent } from 'react';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

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
  const [username, setUsername] = useState('');

  const getApiUrl = (): string => {
    if (typeof window === 'undefined') {
      throw new Error('window is not defined');
    }
    const hostname = window.location.hostname;
    const urlMapping: Record<string, string> = {
      netlify: 'https://discogs-query-api.onrender.com/discogs-query/search',
      'rgbnathan-discogs-api':
        'https://discogs-query-api.onrender.com/discogs-query/search',
    };

    for (const [key, url] of Object.entries(urlMapping)) {
      if (hostname.includes(key)) {
        return url;
      }
    }

    return 'http://localhost:9090/discogs-query/search';
  };

  const handleSearchFormSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setLoading(true);

    const apiUrl = getApiUrl();
    const payload = {
      username: username || undefined,
      queries,
    };

    try {
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(
          `Server responded with status ${response.status}: ${errorMessage}`
        );
      }

      const data = await response.json();
      setResponse(data);

      if (Array.isArray(data) && data.length > 0) {
        const cheapestItemsList = data
          .map((result: any) => result.cheapestItem)
          .filter((item: any) => item !== null);
        onCheapestItemsChange(cheapestItemsList);
      } else {
        onCheapestItemsChange([]);
      }
    } catch (error: any) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box component="form" onSubmit={handleSearchFormSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
      <TextField
        label="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        variant="outlined"
        size="small"
      />
      <Button variant="contained" type="submit" disabled={loading}>
        {loading ? 'Loading...' : 'Search'}
      </Button>
    </Box>
  );
};

export default SearchForm;
