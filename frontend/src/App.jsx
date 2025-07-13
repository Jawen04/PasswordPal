import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import LoginPage from './pages/LoginPage.jsx';
import { CredentialsProvider } from './util/LoginProvider.jsx';
import DashBoard from './pages/DashboardPage.jsx'
import CreateAccountPage from './pages/CreateAccountPage.jsx'
import PasswordGeneratorPage from './pages/PasswordGeneratorPage.jsx'

import './App.css';
import PasswordsPage from "./pages/PasswordsPage.jsx";

function App() {
  return (
    <CredentialsProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/auth/login" replace />} /> 
          <Route path="/auth/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<DashBoard />} />
          <Route path="/createAccount" element={<CreateAccountPage />} />
          <Route path="/generatePassword" element={<PasswordGeneratorPage />} />
          <Route path="/passwords" element={<PasswordsPage />}/>
        </Routes>
      </Router>
    </CredentialsProvider>
  );
}

export default App;

// /auth/login