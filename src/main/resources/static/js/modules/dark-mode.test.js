import './dark-mode'; // Import the script to run it

describe('Dark Mode Toggle', () => {
  beforeEach(() => {
    document.body.innerHTML = `
      <button id="toggleDarkMode"></button>
    `;
    localStorage.clear();
  });

  test('should apply dark mode if localStorage has enabled value', () => {
    localStorage.setItem('dark-mode', 'enabled');
    require('./dark-mode');
    expect(document.body.classList.contains('dark-mode')).toBe(true);
  });

  test('should toggle dark mode on button click', () => {
    const toggleButton = document.getElementById('toggleDarkMode');
    require('./dark-mode'); // Import again to attach event listeners

    // Initially, dark mode should not be enabled
    expect(document.body.classList.contains('dark-mode')).toBe(false);

    toggleButton.click();
    expect(document.body.classList.contains('dark-mode')).toBe(true);
    expect(localStorage.getItem('dark-mode')).toBe('enabled');

    toggleButton.click();
    expect(document.body.classList.contains('dark-mode')).toBe(false);
    expect(localStorage.getItem('dark-mode')).toBe('disabled');
  });
});
