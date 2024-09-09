import { handleSearchFormSubmit } from './../modules/searchHandler';

// Mock DOM elements
document.body.innerHTML = `
  <form>
    <div class="query">
      <input type="text" class="artist" value="Artist Name">
      <input type="text" class="barcode" value="123456">
      <input type="text" class="album" value="Album Name">
      <input type="text" class="track" value="Track Name">
      <select class="format">
        <option value="vinyl">Vinyl</option>
        <option value="cd">CD</option>
      </select>
      <select class="country">
        <option value="US">US</option>
        <option value="UK">UK</option>
      </select>
      <select class="types">
        <option value="RELEASE">Release</option>
        <option value="MASTER">Master</option>
      </select>
    </div>
    <button type="submit" id="searchButton">Search</button>
    <div id="loading" style="display: none;">Loading...</div>
  </form>
  <div id="results"></div>
`;

beforeEach(() => {
  jest.clearAllMocks();
});

test('handles search form submission', () => {
  const mockFetch = jest.fn(() => Promise.resolve({
    ok: true,
    json: () => Promise.resolve([{ results: [] }])
  }));
  global.fetch = mockFetch;

  const form = document.querySelector('form');
  form.addEventListener('submit', handleSearchFormSubmit);

  form.dispatchEvent(new Event('submit'));

  expect(mockFetch).toHaveBeenCalled();
  expect(mockFetch).toHaveBeenCalledWith(
    'http://localhost:9090/discogs-query/search', // or the correct URL based on the environment
    expect.objectContaining({
      method: 'POST',
      headers: expect.objectContaining({
        'Content-Type': 'application/json'
      }),
      body: JSON.stringify([{
        artist: 'Artist Name',
        barcode: '123456',
        album: 'Album Name',
        track: 'Track Name',
        format: 'vinyl',
        country: 'US',
        types: 'RELEASE'
      }])
    })
  );
});
