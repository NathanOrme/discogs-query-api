import { handleSearchFormSubmit } from './searchHandler';
import '@testing-library/jest-dom/extend-expect';

// Mock DOM elements
document.body.innerHTML = `
  <form>
    <div class="query">
      <input class="artist" value="Artist Name">
      <input class="barcode" value="123456">
      <input class="album" value="Album Name">
      <input class="track" value="Track Name">
      <select class="format"><option value="vinyl">Vinyl</option></select>
      <select class="country"><option value="US">US</option></select>
      <select class="types"><option value="RELEASE">Release</option></select>
    </div>
    <button id="searchButton">Search</button>
    <div id="loading" style="display:none;"></div>
  </form>
`;

describe('handleSearchFormSubmit', () => {
  it('should handle the form submission and call API', async () => {
    const fetchMock = jest.spyOn(global, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => [{ results: [] }]
    });

    const form = document.querySelector('form');
    form.addEventListener('submit', handleSearchFormSubmit);
    await form.dispatchEvent(new Event('submit', { bubbles: true }));

    expect(document.getElementById('searchButton').disabled).toBe(false);
    expect(document.getElementById('loading').style.display).toBe('none');
    expect(fetchMock).toHaveBeenCalledTimes(1);

    fetchMock.mockRestore();
  });

  it('should handle API errors', async () => {
    jest.spyOn(global, 'fetch').mockRejectedValue(new Error('API error'));

    const form = document.querySelector('form');
    form.addEventListener('submit', handleSearchFormSubmit);
    await form.dispatchEvent(new Event('submit', { bubbles: true }));

    expect(document.getElementById('searchButton').disabled).toBe(false);
    expect(document.getElementById('loading').style.display).toBe('none');

    jest.restoreAllMocks();
  });
});
