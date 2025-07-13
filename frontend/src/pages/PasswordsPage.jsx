import React, { useState, useEffect } from "react";
import Card from "../components/Card";
import TitleAndBtn from "../components/TitleAndBtn";
import Banner from "../components/Banner";
import CredentialsCard from "../components/CredentialsCard";
import { getAllStoredLogins } from "../util/getAllStoredLogins";



export default function PasswordsPage() {

    return (
            <div>
                <Banner />
                <div className='flex flex-col justify-center items-center'>
                  <Content />
                </div>
            </div>
        )

    }
function Content() {
    return(
        <div className='w-full max-w-3xl px-4 ml-10 mr-10'>
            <TitleAndBtn title={"Passwords"} message={"Manage all your stored passwords"}/>
            <PasswordsCard />
        </div>
    )
}


function PasswordsCard() {
  const [credentials, setCredentials] = useState([]);

  useEffect(() => {
    async function fetchCredentials() {
      const creds = await getAllStoredLogins({ username: "InstaTest" });
      setCredentials(creds);
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

