// src/App.tsx

import React, { useState, useCallback } from 'react';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import QueryFields from './modules/QueryFields';
import SearchForm from './modules/SearchForm';
import CheapestItem from './modules/CheapestItem';
import Results from './modules/Results';
import { Query, QueryResult } from './modules/types';

const App: React.FC = () => {
  const [queries, setQueries] = useState<Query[]>([{ id: 1 }]);
  const [cheapestItems, setCheapestItems] = useState<any[]>([]);
  const [response, setResponse] = useState<QueryResult[]>([]);

  const handleQueriesChange = useCallback((newQueries: Query[]) => {
    setQueries(newQueries);
  }, []);

  return (
    <Container maxWidth="md" sx={{ py: 4 }}>
      <Box component="header" sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Discogs Query App
        </Typography>
        <Box sx={{ mb: 2 }}>
          <Typography variant="h4" component="h2" gutterBottom>
            How to Use This Search Tool
          </Typography>
          <Typography variant="body1" gutterBottom>
            This tool allows you to search for music releases from the Discogs database. It will filter the results to show any that are sold in the UK.
          </Typography>
          <Typography variant="body1" gutterBottom>
            Follow these steps to get started:
          </Typography>
          <Typography variant="body1" gutterBottom>
            <strong>Note that you must supply at least the barcode or the artist</strong>
          </Typography>
          <ol>
            <li>
              <strong>Barcode: </strong>Enter the barcode for the product you are searching for.
            </li>
            <li>
              <strong>Artist:</strong> Enter the name of the artist you are searching for.
            </li>
            <li>
              <strong>Track:</strong> Enter the name of the track. This is optional, so you can leave it blank if not needed.
            </li>
            <li>
              <strong>Album:</strong> Enter the name of the Album. This is optional, so you can leave it blank if not needed.
            </li>
            <li>
              <strong>Format:</strong> Select the format of the release (e.g., Vinyl, Compilation). This is optional.
            </li>
            <li>
              Click the <strong>Add Another Query</strong> button to add more search criteria. Each query will be processed separately.
            </li>
            <li>
              Add your <strong>Username</strong> to the search, if your Discogs collection is public, so that the API can cross reference it.
            </li>
            <li>
              Click the <strong>Search</strong> button to submit your queries and see the results.
            </li>
          </ol>
        </Box>
      </Box>
      <Box component="main" sx={{ mb: 4 }}>
        <Box sx={{ mb: 4 }}>
          <QueryFields onQueriesChange={handleQueriesChange} />
        </Box>
        <Box sx={{ mb: 4 }}>
          <SearchForm
            queries={queries}
            setResponse={setResponse}
            onCheapestItemsChange={setCheapestItems}
          />
          {response.length > 0 && <Results response={response} />}
        </Box>
        <Box sx={{ mb: 4 }}>
          <CheapestItem items={cheapestItems} />
        </Box>
        <div id="loading" style={{ display: 'none' }}>
          Loading...
        </div>
      </Box>
    </Container>
  );
};

export default App;
