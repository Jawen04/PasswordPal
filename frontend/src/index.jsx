import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { CredentialsProvider } from './util/LoginProvider.jsx'; // adjust path if different


createRoot(document.getElementById('root')).render(
  <StrictMode>
    <CredentialsProvider>
      <App />
    </CredentialsProvider>
  </StrictMode>,
)
