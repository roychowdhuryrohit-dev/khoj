import React, { useState, useEffect, useRef } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import ReactMarkdown from 'react-markdown'
import { Send } from 'lucide-react'

const ChatBubble = ({ message, isServer }) => (
  <div className={`mb-4 ${isServer ? 'text-left' : 'text-right'}`}>
    <div
      className={`inline-block p-3 rounded-lg ${
        isServer ? 'bg-black text-white' : 'bg-white text-black border border-gray-300'
      }`}
    >
      <ReactMarkdown>{message}</ReactMarkdown>
    </div>
  </div>
)

const LoadingBubble = () => (
  <div className="mb-4 text-left">
    <div className="inline-block p-3 rounded-lg bg-black text-white">
      <span className="animate-pulse">...</span>
    </div>
  </div>
)

export default function ChatManager() {
  const [messages, setMessages] = useState([])
  const [inputMessage, setInputMessage] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [isConnected, setIsConnected] = useState(false)
  const location = useLocation()
  const navigate = useNavigate()
  const stompClient = useRef(null)

  useEffect(() => {
    const { state } = location
    if (!state || !state.selectedFiles || state.selectedFiles.length === 0) {
      navigate('/')
      return
    }

    createChatSession(state.selectedFiles)
  }, [location, navigate])

  const createChatSession = async (selectedFiles) => {
    try {
      const response = await fetch('/startChatSession', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ filenames: selectedFiles }),
      })

      if (response.ok) {
        const data = await response.json()
        setMessages([{ text: data.message, isServer: true }])
        initializeWebSocket()
      } else {
        console.error('Failed to start chat session')
        setMessages([{ text: "Failed to start chat session. Please try again.", isServer: true }])
        setIsLoading(false)
      }
    } catch (error) {
      console.error('Error starting chat session:', error)
      setMessages([{ text: "An error occurred while starting the chat session. Please try again.", isServer: true }])
      setIsLoading(false)
    }
  }

  const initializeWebSocket = () => {
    const socket = new SockJS('/chat')
    stompClient.current = Stomp.over(socket)

    stompClient.current.connect({}, () => {
      console.log('Connected to STOMP')
      setIsConnected(true)
      setIsLoading(false)
      stompClient.current.subscribe('/topic/messages', (message) => {
        const receivedMessage = JSON.parse(message.body)
        setMessages((prev) => [...prev, { text: receivedMessage.message, isServer: true }])
        setIsLoading(false)
      })
    }, (error) => {
      console.error('STOMP error:', error)
      setMessages((prev) => [...prev, { text: "Connection error. Please refresh the page and try again.", isServer: true }])
      setIsLoading(false)
    })
  }

  useEffect(() => {
    return () => {
      if (stompClient.current && stompClient.current.connected) {
        stompClient.current.disconnect()
      }
    }
  }, [])

  const handleSendMessage = (e) => {
    e.preventDefault()
    if (inputMessage.trim() && !isLoading && isConnected) {
      setMessages((prev) => [...prev, { text: inputMessage, isServer: false }])
      setIsLoading(true)
      stompClient.current.send("/app/sendMessage", {}, JSON.stringify({ prompt: inputMessage }))
      setInputMessage('')
    }
  }

  if (!isConnected && messages.length === 0) {
    return (
      <div className="flex items-center justify-center h-screen">
        <p>Initializing chat session...</p>
      </div>
    )
  }

  return (
    <div className="flex flex-col h-screen">
      <div className="flex-1 overflow-y-auto p-4 pb-20">
        {messages.map((message, index) => (
          <ChatBubble key={index} message={message.text} isServer={message.isServer} />
        ))}
        {isLoading && <LoadingBubble />}
      </div>
      <form onSubmit={handleSendMessage} className="p-4 border-t sticky bottom-0 bg-white">
        <div className="flex items-center gap-4">
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            className="flex-1 p-2 border rounded-md"
            placeholder="Type your message..."
            disabled={isLoading || !isConnected}
          />
          <button
            type="submit"
            className="px-6 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={isLoading || !isConnected || !inputMessage.trim()}
          >
            <Send className="h-6 w-6" />
          </button>
        </div>
      </form>
    </div>
  )
}