// src/modules/Results.tsx

import React from 'react';
import exportToJson from './utils/exportToJson';
import QueryResults from './results/QueryResults';
import ErrorMessage from './utils/ErrorMessage';

interface ResultsProps {
  response: QueryResults[];
}

const Results: React.FC<ResultsProps> = ({ response }) => {
  if (!Array.isArray(response) || response.length === 0) {
    return <ErrorMessage message="No results found." />;
  }

  return (
    <div id="results">
      <button
        onClick={() => exportToJson(response, 'results')}
        className="export-button"
      >
        Export Results to JSON
      </button>
      {response.map((queryResult, index) => (
        <QueryResults queryResult={queryResult} index={index} key={index} />
      ))}
    </div>
  );
};

export default Results;

