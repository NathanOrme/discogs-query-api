// src/modules/Api.js

/**
 * Retrieves the appropriate API URL based on the current hostname and the desired endpoint.
 * @param {string} endpoint - The endpoint to retrieve the URL for ('search' or 'filter-uk').
 * @returns {string} The API URL for fetching data.
 */
export const getApiUrl = (endpoint) => {
  const hostname = window.location.hostname;
  const urlMapping = {
    render: {
      search: "https://discogs-query-api.onrender.com/discogs-query/search",
      filterUk: "https://discogs-query-api.onrender.com/discogs-query/filter-uk",
    },
    koyeb: {
      search: "https://discogs-query-api-rgbnathan.koyeb.app/discogs-query/search",
      filterUk: "https://discogs-query-api-rgbnathan.koyeb.app/discogs-query/filter-uk",
    },
    b4a: {
      search: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/search",
      filterUk: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/filter-uk",
    },
    netlify: {
      search: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/search",
      filterUk: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/filter-uk",
    },
  };

  for (const [key, urls] of Object.entries(urlMapping)) {
    if (hostname.includes(key)) {
      return urls[endpoint] || urls.search; // Default to search if endpoint is invalid
    }
  }

  // Localhost fallback
  return endpoint === 'filterUk'
    ? "http://localhost:9090/discogs-query/filter-uk"
    : "http://localhost:9090/discogs-query/search";
};

};
