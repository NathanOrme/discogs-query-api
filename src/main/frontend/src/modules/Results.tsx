import React from 'react';
import exportToJson from './utils/exportToJson';
import QueryResults from './results/QueryResults';
import ErrorMessage from './utils/ErrorMessage';
import { Entry } from './results/Entry';

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
    <div id="results">
      <button
        onClick={function() { return exportToJson(response, 'results') }}
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
