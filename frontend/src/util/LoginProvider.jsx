import { useState } from 'react';
import { CredentialsContext } from './LoginContext' // adjust path as needed

export const CredentialsProvider = ({ children }) => {
  const [credentials, setCredentials] = useState(["", ""]);

  return (
    <CredentialsContext.Provider value={{ credentials, setCredentials }}>
      {children}
    </CredentialsContext.Provider>
  );
};
