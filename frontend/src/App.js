import React, { useState } from 'react';
import './css/base.css';
import './css/components.css';
import './css/layout.css';
import './css/themes.css';
import QueryFields from './modules/QueryFields';
import SearchForm from './modules/SearchForm';

function App() {
  const [queryCounter, setQueryCounter] = useState(1);

  return (
    <div className="App">
      <header>
        <h1>Discogs Query App</h1>
        <button id="toggleDarkMode">Toggle Dark Mode</button>
      </header>
      <main>
        <div className="instructions">
          {/* Instructions go here */}
        </div>
        <SearchForm />
        <div id="loading" style={{ display: 'none' }}>Loading...</div>
        <div id="results"></div>
      </main>
    </div>
  );
}

export default App;
