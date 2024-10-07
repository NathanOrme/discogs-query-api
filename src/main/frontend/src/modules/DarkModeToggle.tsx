//src/modules/DarkModeToggle.js

import React, { useEffect, useState } from 'react'

/**
 * A component that toggles dark mode on and off.
 *
 * @returns {JSX.Element} The rendered toggle button.
 */
const DarkModeToggle: React.FC = () => {
  const [isDarkMode, setIsDarkMode] = useState<boolean>(() => {
    // Check for user preference in localStorage
    return localStorage.getItem('dark-mode') === 'enabled'
  })

  useEffect(() => {
    // Apply dark mode class based on state
    if (isDarkMode) {
      document.body.classList.add('dark-mode')
    } else {
      document.body.classList.remove('dark-mode')
    }

    // Save the user's preference
    localStorage.setItem('dark-mode', isDarkMode ? 'enabled' : 'disabled')
  }, [isDarkMode])

  /**
   * Toggles the dark mode state.
   */
  const toggleDarkMode = () => {
    setIsDarkMode(prevMode => !prevMode)
  }

  return (
    <button id='toggleDarkMode' onClick={toggleDarkMode}>
      Toggle Dark Mode
    </button>
  )
}

export default DarkModeToggle
