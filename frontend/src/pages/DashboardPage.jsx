import { useContext, useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import TitleAndBtn from '../components/TitleAndBtn';

import Card from '../components/Card';
import Banner from '../components/Banner';
import { getCurrentSignedInUser } from '../util/getCurrentSignedInUser';
import { checkActiveSession } from '../util/checkActiveSession';




export default function DashBoard() {

    const [hasActiveSession, setHasActiveSession] = useState(false)

    useEffect( () =>{
      async function handleCheckActiveSession() {
        try {
          if(await checkActiveSession()) {
            setHasActiveSession(true)
            console.log("Active Session now set to true!")
          }
        } catch (error) {
          console.log("Error when checking active user session: " + error)
        }
      }
      handleCheckActiveSession()
    }, []) 
      


    if(hasActiveSession) {
      return (
        <div>
            <Banner />
            <div className='flex flex-col justify-center items-center'>
              <Content />
            </div>
        </div>
        
      )
    } else {
      return (
        <div className='h-full w-full'>
          <div className='flex flex-col justify-center items-center'>
            <p className='bg-red-500 flex flex-col text-xl'>
              USER NOT LOGGED IN
            </p>
          </div>
        </div>

      )


    }
    
} 




function Content() {
    const [currUser, setCurrUser] = useState("");

    useEffect(() => {
      async function fetchUser() {
        try {
          const data = await getCurrentSignedInUser();
          
          setCurrUser(data?.username)
          console.log("username: " + data.username)
        } catch (error) {
          console.error(error)
        }
      }
      fetchUser()
    }, [])
    return (
        <div className='w-full max-w-3xl px-4 ml-10 mr-10'>
          <TitleAndBtn  title={`Welcome back ${currUser ?? ""}`} message="Manage your passwords and account security" />            
          <PasswordHealthCard />
        </div>
    )

}

function PasswordHealthCard() {
  const passwordTypes = [
    { type: "Total Passwords", value: null, color: "text-black" },
    { type: "Strong Passwords", value: null, color: "text-green-500" },
    { type: "Moderate Passwords", value: null, color: "text-orange-500" },
    { type: "Weak Passwords", value: null, color: "text-red-500" },
    { type: "Reused Passwords", value: null, color: "text-red-500" },
  ];

  return (
    <Card className="p-6 max-w-4xl mx-auto w-full">
      <p className="text-xl text-black font-bold mb-1">Password Health</p>
      <p className="text-gray-400 mb-6">Overview of your password security stats</p>

      <div className="flex flex-col md:flex-row gap-6">
        <div className="flex flex-col rounded-lg p-4 flex-1 min-w-0">
          <p className="text-black font-bold mb-4 text-center md:text-left">
            Password statistics
          </p>

          <div className="space-y-3">
            {passwordTypes.map((typeObj, index) => (
              <div
                key={index}
                className="flex justify-between w-full min-w-0"
              >
                <p className="text-gray-700 truncate">{typeObj.type}</p>
                <p
                  className={`${typeObj.color} font-semibold text-right min-w-[50px]`}
                >
                  {typeObj.value ?? "N/A"}
                </p>
              </div>
            ))}
          </div>
        </div>

        <div className="flex flex-col border border-gray-200 rounded-lg p-6 flex-none w-full md:w-1/3 items-center text-center text-white">
          <p className="text-black text-lg mb-4">Security score</p>
          <p className="text-5xl text-green-400 font-extrabold mb-4">75%</p>
          <p className="text-gray-400 text-sm">Excellent security score!</p>
        </div>
      </div>
    </Card>
  );
}






