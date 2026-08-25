import { useState } from 'react'
import { useMatch } from 'react-router'
import { Route, Routes } from 'react-router'
import './App.css'

import ContentSubPage from './pages/content-sub-page/content-sub-page'
import ConceptsPage from './pages/concepts-page/concepts-page'

function App() {
  
  function logo(){
    return (
      <h1 className={useMatch('/') ? 'brand' : 'brand-sm'}>
        <span className="with-marker">Index</span>
      </h1>
    );
  }
  
  return (
    <div className="main-container">
      {logo()}
      <Routes>
        <Route path="/" element={<ContentSubPage />} />
        <Route path="/concepts" element={<ConceptsPage />} />
      </Routes>

    </div>
  )
}

export default App
