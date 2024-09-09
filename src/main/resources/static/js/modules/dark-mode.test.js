import './dark-mode'; // Import the module to run the code

document.body.innerHTML = `
  <button id="toggleDarkMode">Toggle Dark Mode</button>
`;

test('toggles dark mode on button click', () => {
  const toggleButton = document.getElementById('toggleDarkMode');

  // Initial state should not have dark mode
  expect(document.body.classList.contains('dark-mode')).toBe(false);

  toggleButton.click();
  expect(document.body.classList.contains('dark-mode')).toBe(true);

  toggleButton.click();
  expect(document.body.classList.contains('dark-mode')).toBe(false);
});

test('loads dark mode from localStorage', () => {
  localStorage.setItem('dark-mode', 'enabled');
  document.body.innerHTML = `
    <button id="toggleDarkMode">Toggle Dark Mode</button>
  `;
  import('./dark-mode'); // Re-import to re-run the setup

  expect(document.body.classList.contains('dark-mode')).toBe(true);
});
