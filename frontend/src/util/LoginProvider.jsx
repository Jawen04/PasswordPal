import { useState } from 'react';
import { CredentialsContext } from './LoginContext' // adjust path as needed

export const CredentialsProvider = ({ children }) => {
  const [currSignedInUser, setCurrSignedInUser] = useState("");

  return (
    <CredentialsContext.Provider value={{ currSignedInUser, setCurrSignedInUser }}>
      {children}
    </CredentialsContext.Provider>
  );
};
