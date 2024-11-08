import { useState } from 'react'
import FileManager from './components/FileManager'
import './App.css'

function App() {


  return (
    <main className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">Khoj</h1>
      <FileManager />
    </main>
  );
}

export default App
