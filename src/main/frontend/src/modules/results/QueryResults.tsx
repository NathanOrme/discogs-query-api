//src/modules/results/QueryResults.tsx

import React from 'react';
import Entry from './Entry';
import { QueryResult } from './../types';

interface QueryResultsProps {
  queryResult: QueryResult;
  index: number;
}

const QueryResults: React.FC<QueryResultsProps> = ({ queryResult, index }) => {
  const { results } = queryResult;

  if (!results || Object.keys(results).length === 0) return null;

  return (
    <div>
      <h2>Results for Query {index + 1}</h2>
      {Object.keys(results).map((title) => {
        const entries = results[title];

        if (!Array.isArray(entries) || entries.length === 0) {
          return null;
        }

        return (
          <div className="results-section" key={title}>
            <div
              className="results-toggle-header"
              onClick={(e) => {
                const content = e.currentTarget.nextElementSibling;
                if (content) {
                  content.classList.toggle('hidden');
                  e.currentTarget.textContent = content.classList.contains(
                    'hidden'
                  )
                    ? "Show Results for "+title
                    : "Hide Results for "+title;
                }
              }}
            >
              Show Results for {title}
            </div>
            <div className="results-content hidden">
              {entries.map((entry) => (
                <Entry entry={entry} key={entry.id || Math.random()} />
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default QueryResults;
