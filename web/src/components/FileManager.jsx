import { useState, useEffect } from 'react'
import { Upload, RefreshCw, Download } from 'lucide-react'

const IconButton = ({ onClick, disabled, children, className }) => (
  <button
    onClick={onClick}
    disabled={disabled}
    className={`p-2 bg-black text-white rounded-full hover:bg-gray-800 disabled:opacity-50 disabled:cursor-not-allowed ${className}`}
  >
    {children}
  </button>
)

export default function FileManager() {
  const [files, setFiles] = useState([])
  const [isUploading, setIsUploading] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [downloadingFile, setDownloadingFile] = useState(null)

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
    if (file && file.type === 'application/pdf') {
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

  const handleFileDownload = async (filename) => {
    setDownloadingFile(filename)
    try {
      const response = await fetch(`/getPreSignedFileUrl?filename=${encodeURIComponent(filename)}`)
      if (response.ok) {
        const { preSignedUrl } = await response.json()
        const fileResponse = await fetch(preSignedUrl)
        const blob = await fileResponse.blob()
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = filename
        document.body.appendChild(link)
        link.click()
        link.remove()
        window.URL.revokeObjectURL(downloadUrl)
      } else {
        console.error('Failed to get pre-signed URL')
      }
    } catch (error) {
      console.error('Error downloading file:', error)
    }
    setDownloadingFile(null)
  }

  return (
    <div className="relative min-h-[300px]">
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
        {files.map((file, index) => (
          <div
            key={index}
            className="p-4 border rounded-lg shadow-sm hover:shadow-md transition-shadow cursor-pointer relative"
            onClick={() => handleFileDownload(file[0])}
          >
            <p className="text-sm truncate">{file[0]}</p>
            {downloadingFile === file[0] && (
              <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center rounded-lg">
                <Download className="h-6 w-6 text-white animate-pulse" />
              </div>
            )}
          </div>
        ))}
      </div>

      {files.length === 0 && !isRefreshing && (
        <p className="text-center text-gray-500 mt-8">No PDF files uploaded yet.</p>
      )}

      <div className="fixed bottom-4 right-4 flex flex-col gap-2 p-2 bg-white bg-opacity-50 rounded-lg">
        <IconButton onClick={fetchFiles} disabled={isRefreshing}>
          <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
        </IconButton>
        <div className="relative">
          <input
            type="file"
            accept=".pdf"
            onChange={handleFileUpload}
            className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            disabled={isUploading}
          />
          <IconButton disabled={isUploading}>
            <Upload className={`h-4 w-4 ${isUploading ? 'animate-pulse' : ''}`} />
          </IconButton>
        </div>
      </div>
    </div>
  )
}