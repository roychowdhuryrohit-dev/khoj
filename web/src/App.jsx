import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import FileManager from './components/FileManager'
import ChatManager from './components/ChatManager'
import './App.css'

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-[#fffdf0] font-sans">
        <div className="container mx-auto p-4">
          <h1 className="text-4xl font-bold mb-4 text-gray-800">Khoj &#128373;</h1>
          <Routes>
            <Route path="/" element={<FileManager />} />
            <Route path="/chat" element={<ChatManager />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App