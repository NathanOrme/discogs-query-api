import React from 'react';
import exportToJson from './utils/exportToJson';
import QueryResults from './results/QueryResults';
import ErrorMessage from './utils/ErrorMessage';
import { Entry } from './results/Entry';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';

interface QueryResult {
  results: Record<string, Entry[]>;
}

interface ResultsProps {
  response: QueryResult[];
}

const Results: React.FC<ResultsProps> = ({ response }) => {
  if (!Array.isArray(response) || response.length === 0) {
    return <ErrorMessage message="No results found." />;
  }

  return (
    <Box id="results" sx={{ p: 2 }}>
      <Button variant="contained" onClick={() => exportToJson(response, 'results')} sx={{ mb: 2 }}>
        Export Results to JSON
      </Button>
      {response.map((queryResult, index) => (
        <QueryResults queryResult={queryResult} index={index} key={index} />
      ))}
    </Box>
  );
};

export default Results;
