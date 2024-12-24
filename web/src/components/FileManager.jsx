import { useState, useEffect, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import { Upload, RefreshCw, MessageSquare } from 'lucide-react'
import DocumentViewer from './DocumentViewer'

const IconButton = ({ onClick, disabled, children, className }) => (
  <button
    onClick={(e) => {
      e.stopPropagation();
      onClick(e);
    }}
    disabled={disabled}
    className={`p-2 bg-black text-white rounded-full hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed w-12 h-12 flex items-center justify-center ${className}`}
  >
    {children}
  </button>
)

export default function FileManager() {
  const [files, setFiles] = useState([])
  const [isUploading, setIsUploading] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [selectedFiles, setSelectedFiles] = useState([])
  const [isChatMode, setIsChatMode] = useState(false)
  const [viewingFile, setViewingFile] = useState(null)
  const navigate = useNavigate()
  const fileGridRef = useRef(null)

  useEffect(() => {
    fetchFiles()
  }, [])

  const fetchFiles = async () => {
    setIsRefreshing(true)
    try {
      const response = await fetch('/listUserFiles')
      if (response.ok) {
        const data = await response.json()
        setFiles(Object.entries(data.files))
      } else {
        console.error('Failed to fetch files')
      }
    } catch (error) {
      console.error('Error fetching files:', error)
    }
    setIsRefreshing(false)
  }

  const handleFileUpload = async (event) => {
    const file = event.target.files[0]
    if (file) {
      setIsUploading(true)
      const formData = new FormData()
      formData.append('file', file)

      try {
        const response = await fetch('/upload', {
          method: 'POST',
          body: formData,
        })

        if (response.ok) {
          await fetchFiles()
        } else {
          console.error('Failed to upload file')
        }
      } catch (error) {
        console.error('Error uploading file:', error)
      }
      setIsUploading(false)
    }
  }

  const handleFileClick = async (filename) => {
    if (isChatMode) {
      toggleFileSelection(filename)
      return
    }

    setViewingFile(filename)
  }

  const toggleFileSelection = (filename) => {
    setSelectedFiles(prev =>
      prev.includes(filename)
        ? prev.filter(f => f !== filename)
        : [...prev, filename]
    )
  }

  const handleStartChat = () => {
    navigate('/chat', { state: { selectedFiles } })
  }

  const handleChatModeToggle = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setIsChatMode(!isChatMode)
    setSelectedFiles([])
    setViewingFile(null)
  }

  return (
    <div className="relative min-h-[300px]">
      <div ref={fileGridRef} className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {files.map((file, index) => (
          <div
            key={index}
            className={`p-4 border rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer relative ${selectedFiles.includes(file[0]) ? 'bg-gray-300 text-gray-600' : 'bg-white'
              }`}
            onClick={() => handleFileClick(file[0])}
          >
            <p className="text-sm truncate">{file[0]}</p>
          </div>
        ))}
      </div>

      {files.length === 0 && !isRefreshing && (
        <p className="text-center text-gray-500 mt-8">No files uploaded yet.</p>
      )}

      {viewingFile && (
        <DocumentViewer filename={viewingFile} onClose={() => setViewingFile(null)} />
      )}

      <div className="fixed bottom-4 right-4 flex items-end gap-2 p-2 bg-white bg-opacity-50 rounded-lg">
        {isChatMode ? (
          <div className="flex-1">
            <button
              onClick={handleStartChat}
              disabled={selectedFiles.length === 0}
              className="px-6 py-3 bg-blue-500 text-white rounded-md hover:bg-blue-600 disabled:opacity-50 disabled:cursor-not-allowed text-xl"
            >
              Start Chatting ({selectedFiles.length})
            </button>
          </div>
        ) : (
          <>
            <IconButton onClick={fetchFiles} disabled={isRefreshing}>
              <RefreshCw className={`h-5 w-5 ${isRefreshing ? 'animate-spin' : ''}`} />
            </IconButton>
            <div className="relative">
              <input
                type="file"
                onChange={handleFileUpload}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                disabled={isUploading}
              />
              <IconButton disabled={isUploading}>
                <Upload className={`h-5 w-5 ${isUploading ? 'animate-pulse' : ''}`} />
              </IconButton>
            </div>
          </>
        )}

        <IconButton
          onClick={handleChatModeToggle}
          className={isChatMode ? 'bg-blue-500 hover:bg-blue-600' : ''}
        >
          <MessageSquare className="h-5 w-5" />
        </IconButton>
      </div>
    </div>
  )
}