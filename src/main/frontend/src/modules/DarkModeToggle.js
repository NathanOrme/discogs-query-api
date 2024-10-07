// src/modules/DarkModeToggle.js

import React, { useEffect, useState } from 'react';

const DarkModeToggle = () => {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    // Check for user preference in localStorage
    return localStorage.getItem('dark-mode') === 'enabled';
  });

  useEffect(() => {
    // Apply dark mode class based on state
    if (isDarkMode) {
      document.body.classList.add('dark-mode');
    } else {
      document.body.classList.remove('dark-mode');
    }

    // Save the user's preference
    localStorage.setItem('dark-mode', isDarkMode ? 'enabled' : 'disabled');
  }, [isDarkMode]);

  const toggleDarkMode = () => {
    setIsDarkMode(prevMode => !prevMode);
  };

  return (
    <button id="toggleDarkMode" onClick={toggleDarkMode}>
      Toggle Dark Mode
    </button>
  );
};

export default DarkModeToggle;
