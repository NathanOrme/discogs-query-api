// src/modules/Results.tsx

import React from 'react'

interface Entry {
  id?: string
  format?: string[]
  country?: string
  year?: string
  uri?: string
  numberForSale?: number
  lowestPrice?: number | null
  title?: string
}

interface QueryResult {
  results: Record<string, Entry[]>
}

interface ResultsProps {
  response: QueryResult[]
}

/**
 * Function to download JSON data.
 *
 * @param {Object} data - The data to export as JSON.
 * @param {string} filename - The name of the file to be downloaded.
 */
const exportToJson = (data: any, filename: string) => {
  const jsonString = `data:text/json;charset=utf-8,${encodeURIComponent(
    JSON.stringify(data, null, 2)
  )}`
  const link = document.createElement('a')
  link.href = jsonString
  link.download = `${filename}.json`

  link.click()
}

/**
 * Displays an error message.
 *
 * @param {string} message - The error message to display.
 * @returns {JSX.Element} The error message element.
 */
export const displayError = (message: string): JSX.Element => {
  return <p className='error-message'>{message}</p>
}

/**
 * Renders the details of a single result entry.
 *
 * @param {Entry} entry - The result entry to display.
 * @returns {JSX.Element} The JSX element representing the result entry.
 */
const renderEntry = (entry: Entry): JSX.Element => {
  const id = entry.id || 'N/A'
  const format = entry.format ? entry.format.join(', ') : 'N/A'
  const country = entry.country || 'N/A'
  const year = entry.year || 'N/A'
  const uri = entry.uri || '#'

  return (
    <div className='result-item' key={id}>
      <h3>{entry.title || 'Untitled'}</h3>
      <div className='details'>
        <p>
          <strong>ID:</strong> {id}
        </p>
        <p>
          <strong>Formats:</strong> {format}
        </p>
        <p>
          <strong>Country:</strong> {country}
        </p>
        <p>
          <strong>Year:</strong> {year}
        </p>
        <p>
          <strong>URL:</strong>{' '}
          <a href={uri} target='_blank' rel='noopener noreferrer'>
            {uri}
          </a>
        </p>
        <p>
          <strong>Number For Sale:</strong> {entry.numberForSale || 'N/A'}
        </p>
        <p>
          <strong>Lowest Price:</strong>{' '}
          {entry.lowestPrice != null
            ? `£${entry.lowestPrice.toFixed(2)}`
            : 'N/A'}
        </p>
      </div>
    </div>
  )
}

/**
 * Renders the results for a specific query.
 *
 * @param {QueryResult} queryResult - The query result containing the results.
 * @param {number} index - The index of the query result.
 * @returns {JSX.Element|null} The JSX element for the query results or null if no results.
 */
const renderQueryResults = (
  queryResult: QueryResult,
  index: number
): JSX.Element | null => {
  const { results } = queryResult

  if (!results || Object.keys(results).length === 0) {
    return null // Skip empty results
  }

  return (
    <div key={index}>
      <h2>Results for Query {index + 1}</h2>
      {Object.keys(results).map(title => {
        const entries = results[title]

        if (!Array.isArray(entries) || entries.length === 0) {
          return null // Skip empty entries
        }

        return (
          <div className='results-section' key={title}>
            <div
              className='results-toggle-header'
              onClick={e => {
                const content = e.currentTarget.nextElementSibling

                // Safety check
                if (content) {
                  content.classList.toggle('hidden')
                  e.currentTarget.textContent = content.classList.contains(
                    'hidden'
                  )
                    ? `Show Results for ${title}`
                    : `Hide Results for ${title}`
                } else {
                  console.error('Content not found for:', title)
                }
              }}
            >
              Show Results for {title}
            </div>
            <div className='results-content hidden'>
              {entries.map(entry => renderEntry(entry))}
            </div>
          </div>
        )
      })}
    </div>
  )
}

/**
 * The Results component displays query results and allows exporting them as JSON.
 *
 * @param {ResultsProps} props - The component props.
 * @returns {JSX.Element} The Results component.
 */
const Results: React.FC<ResultsProps> = ({ response }) => {
  const renderResults = (): JSX.Element | null => {
    if (!Array.isArray(response) || response.length === 0) {
      return displayError('No results found.')
    }

    return (
      <>
        {response.map((queryResult, index) =>
          renderQueryResults(queryResult, index)
        )}
      </>
    )
  }

  return (
    <div id='results'>
      <button
        onClick={() => exportToJson(response, 'results')}
        className='export-button'
      >
        Export Results to JSON
      </button>
      {renderResults()}
    </div>
  )
}

export default Results
