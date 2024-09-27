// src/modules/Api.js

/**
 * Retrieves the appropriate API URL based on the current hostname.
 * @returns {string} The API URL for fetching data.
 */
export const getApiUrl = () => {
  const hostname = window.location.hostname;
  const urlMapping = {
    render: "https://discogs-query-api.onrender.com/discogs-query/search",
    koyeb: "https://discogs-query-api-rgbnathan.koyeb.app/discogs-query/search",
    b4a: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/search",
    netlify: "https://discogsqueryapi1-fthsfv0p.b4a.run/discogs-query/search",
  };

  for (const [key, url] of Object.entries(urlMapping)) {
    if (hostname.includes(key)) {
      return url;
    }
  }

  return "http://localhost:9090/discogs-query/search";
};
