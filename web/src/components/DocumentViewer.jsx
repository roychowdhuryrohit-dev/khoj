import React, { useState, useEffect } from 'react'
import PropTypes from 'prop-types'
import { Viewer, Worker } from '@react-pdf-viewer/core'
import { defaultLayoutPlugin } from '@react-pdf-viewer/default-layout'
import '@react-pdf-viewer/core/lib/styles/index.css'
import '@react-pdf-viewer/default-layout/lib/styles/index.css'
import { X } from 'lucide-react'

const DocumentViewer = ({ filename, onClose }) => {
  const [fileUrl, setFileUrl] = useState('')
  const [fileType, setFileType] = useState('')
  const [textContent, setTextContent] = useState('')
  const defaultLayoutPluginInstance = defaultLayoutPlugin()

  useEffect(() => {
    const fetchFileUrl = async () => {
      try {
        const response = await fetch(`/getPreSignedUrl?fileName=${encodeURIComponent(filename)}`)
        if (response.ok) {
          const { preSignedUrl } = await response.json()
          setFileUrl(preSignedUrl)
          const type = getFileType(filename)
          setFileType(type)

          if (type === 'text/plain') {
            const textResponse = await fetch(preSignedUrl)
            const text = await textResponse.text()
            setTextContent(text)
          }
        } else {
          console.error('Failed to get pre-signed URL')
        }
      } catch (error) {
        console.error('Error fetching file URL:', error)
      }
    }

    fetchFileUrl()
  }, [filename])

  const getFileType = (filename) => {
    const extension = filename.split('.').pop().toLowerCase()
    switch (extension) {
      case 'pdf':
        return 'application/pdf'
      case 'docx':
        return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
      case 'pptx':
        return 'application/vnd.openxmlformats-officedocument.presentationml.presentation'
      case 'txt':
        return 'text/plain'
      default:
        return 'application/octet-stream'
    }
  }

  if (!fileUrl && !textContent) {
    return <div>Loading...</div>
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onClick={(e) => e.stopPropagation()}>
      <div className="bg-white rounded-lg p-4 w-full max-w-4xl h-full max-h-[90vh] flex flex-col">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">{filename}</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
            <X className="h-6 w-6" />
          </button>
        </div>
        <div className="flex-1 overflow-auto">
          {fileType === 'application/pdf' && (
            <div className="h-full">
              <Worker workerUrl="https://unpkg.com/pdfjs-dist@3.4.120/build/pdf.worker.min.js">
                <Viewer fileUrl={fileUrl} plugins={[defaultLayoutPluginInstance]} />
              </Worker>
            </div>
          )}
          {(fileType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' ||
            fileType === 'application/vnd.openxmlformats-officedocument.presentationml.presentation') && (
            <iframe
              src={`https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(fileUrl)}`}
              width="100%"
              height="100%"
              style={{ border: 'none' }}
            ></iframe>
          )}
          {fileType === 'text/plain' && (
            <div className="whitespace-pre-wrap font-mono text-sm p-4 h-full overflow-auto bg-gray-100 rounded">
              {textContent}
            </div>
          )}
          {fileType === 'application/octet-stream' && <div>Unsupported file type</div>}
        </div>
      </div>
    </div>
  )
}

DocumentViewer.propTypes = {
  filename: PropTypes.string.isRequired,
  onClose: PropTypes.func.isRequired,
}

export default DocumentViewer