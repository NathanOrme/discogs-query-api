describe("Dark Mode Toggle", () => {
  beforeEach(() => {
    // Simulate the HTML structure
    document.body.innerHTML = `
      <header>
          <button id="toggleDarkMode">Toggle Dark Mode</button>
      </header>
    `;
    require("./../modules/dark-mode.js"); // Adjust this path if needed

    // Trigger DOMContentLoaded to ensure event listeners are attached
    document.dispatchEvent(new Event("DOMContentLoaded"));
  });

  afterEach(() => {
    localStorage.clear();
    document.body.className = ""; // Reset the className after each test
  });

  test('should enable dark mode if localStorage is set to "enabled"', () => {
    localStorage.setItem("dark-mode", "enabled");

    // Trigger DOMContentLoaded event to apply the stored preference
    document.dispatchEvent(new Event("DOMContentLoaded"));

    expect(document.body.classList.contains("dark-mode")).toBe(true);
  });

  test("should toggle dark mode and update localStorage when button is clicked", () => {
    const toggleButton = document.getElementById("toggleDarkMode");

    // Simulate the first click (Enable dark mode)
    toggleButton.click();
    expect(document.body.classList.contains("dark-mode")).toBe(true);
    expect(localStorage.getItem("dark-mode")).toBe("enabled");

    // Simulate the second click (Disable dark mode)
    toggleButton.click();
    expect(document.body.classList.contains("dark-mode")).toBe(false);
    expect(localStorage.getItem("dark-mode")).toBe("disabled");
  });
});
