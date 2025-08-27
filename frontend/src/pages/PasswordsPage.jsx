import React, { useState, useEffect } from "react";
import { useContext } from "react";
import Card from "../components/Card";
import TitleAndBtn from "../components/TitleAndBtn";
import Banner from "../components/Banner";
import CredentialsCard from "../components/CredentialsCard";
import { getAllStoredLogins } from "../util/getAllStoredLogins";
import useAddServicePassword from "../util/AddServicePassword";
import { CredentialsContext } from "../util/LoginContext";
import { getCurrentSignedInUser } from "../util/getCurrentSignedInUser";


export default function PasswordsPage() {
  return (
    <div>
      <Banner />
      <div className="flex flex-col justify-center items-center">
        <Content />
      </div>
    </div>
  );
}

function Content() {
  return (
    <div className="w-full max-w-3xl px-4 ml-10 mr-10">
      <TitleAndBtn
        title={"Passwords"}
        message={"Manage all your stored passwords"}
      />
      <AddPasswordBox />
      <PasswordsCard />
    </div>
  );
}

function PasswordsCard() {
  const [credentials, setCredentials] = useState([]);


 useEffect(() => {
    async function fetchCredentials() {
      const currSignedInUser = await getCurrentSignedInUser(); 
      if (currSignedInUser) {
        const creds = await getAllStoredLogins(currSignedInUser);
        setCredentials(creds);
      }
    }
    fetchCredentials();
  }, []);

  return (
    <Card className="p-6 max-w-4xl mx-auto w-full">
      <p className="text-black font-bold text-xl">All Passwords</p>
      <p className="text-gray-400">View, edit, and manage your saved passwords</p>
      <div className="flex flex-col space-y-2">
        {credentials.map((credObj, index) => (
          <CredentialsCard
            key={index}
            name={credObj.service}
            email={credObj.username}
            password={credObj.password}
          />
        ))}
      </div>
    </Card>
  );
}

const AddPasswordBox = () => {
  const [service, setService] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const { currSignedInUser } = useContext(CredentialsContext); 


  const [logins, setLogins] = useState([])


  // Correct: call the hook at the top level to get the function

  const handleEnter = async () => {
    if (service === "" || username === "" || password === "") return;

    const success = await useAddServicePassword({
      serviceName: service,
      serviceUsername: username,
      servicePassword: password,
    });

    if (success) {
      // Optionally clear inputs or refresh list
      setService("");
      setUsername("");
      setPassword("");
    } 


  };

  

  

  return (
    <Card className="p-6 max-w-4xl mx-auto w-full">
      <p className="text-black font-bold text-xl">Add New Password</p>
      <p className="text-gray-400 pb-8">Add a new password to your vault</p>

      <p className="text-sm text-black">Service</p>
      <input
        type="text"
        placeholder="Enter service name"
        value={service}
        onChange={(e) => setService(e.target.value)}
        className="w-full text-black h-10 rounded-lg border-slate-900 bg-pink-200 focus:border-primary focus:ring-primary/20 transition-all duration-200 mb-4 p-2"
      />

      <p className="text-sm text-black">Username</p>
      <input
        type="text"
        placeholder="Enter username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="w-full text-black h-10 rounded-lg border-slate-900 bg-pink-200 focus:border-primary focus:ring-primary/20 transition-all duration-200 mb-4 p-2"
      />

      <p className="text-sm text-black">Password</p>
      <input
        type="text"
        placeholder="Enter password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && handleEnter()}
        className="w-full text-black h-10 rounded-lg border-slate-900 bg-pink-200 focus:border-primary focus:ring-primary/20 transition-all duration-200 mb-4 p-2"
      />
      <div>
      




      </div>
      
    </Card>
  );
};
