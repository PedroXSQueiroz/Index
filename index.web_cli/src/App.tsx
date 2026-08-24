import { useState } from 'react'
import { useMatch } from 'react-router'
import { Route, Routes } from 'react-router'
import './App.css'

import ContentSubPage from './pages/content-sub-page/content-sub-page'

function App() {
  
  function logo(){
    return (
      <h1 className={useMatch('/') ? 'brand' : 'brand-sm'}>
        <span className="with-marker">Index</span>
      </h1>
    );
  }
  
  return (
    <div className="page-start">
      {logo()}
      <Routes>
        <Route path="/" element={<ContentSubPage />} />
      </Routes>

    </div>
  )
}

export default App
