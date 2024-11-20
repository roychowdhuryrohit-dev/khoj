import { useState, useEffect } from 'react'
import { useLocation, Link } from 'react-router-dom'

export default function ChatManager() {
  const [messages, setMessages] = useState([])
  const [inputMessage, setInputMessage] = useState('')
  const location = useLocation()

  useEffect(() => {
    const { state } = location
    if (state && state.message) {
      setMessages([{ text: state.message, isBot: true }])
    }
  }, [location])

  const handleSendMessage = (e) => {
    e.preventDefault()
    if (inputMessage.trim()) {
      setMessages(prev => [...prev, { text: inputMessage, isBot: false }])
      setInputMessage('')
      // Here you would typically send the message to your backend
      // and then add the response to the messages
    }
  }

  return (
    <div className="flex flex-col h-screen">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-2xl font-bold">Chat with Your Documents</h1>
        <Link to="/" className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300">
          Back to Files
        </Link>
      </div>
      <div className="flex-1 overflow-y-auto p-4 border rounded-lg">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`mb-4 ${
              message.isBot ? 'text-left' : 'text-right'
            }`}
          >
            <div
              className={`inline-block p-2 rounded-lg ${
                message.isBot ? 'bg-gray-200' : 'bg-blue-500 text-white'
              }`}
            >
              {message.text}
            </div>
          </div>
        ))}
      </div>
      <form onSubmit={handleSendMessage} className="mt-4">
        <div className="flex">
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            className="flex-1 p-2 border rounded-l-md"
            placeholder="Type your message..."
          />
          <button
            type="submit"
            className="px-4 py-2 bg-blue-500 text-white rounded-r-md hover:bg-blue-600"
          >
            Send
          </button>
        </div>
      </form>
    </div>
  )
}