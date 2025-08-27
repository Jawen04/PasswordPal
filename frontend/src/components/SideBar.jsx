import { useNavigate } from 'react-router-dom';
import { Key, Lock, Shield, Plus, User, Settings, Calendar } from 'lucide-react';
import { useState } from "react"
import { logoutUser } from '../util/logoutUser';

import ErrorPopup from '../components/ErrorPopUp';



export default function SideBar({ isOpen }) {

  const navigate = useNavigate();



  const options = [
  {option: "Dashboard", symbol: <Key size={15} className='mr-4 ml-2' />, url: '/dashboard'},
  {option: "Passwords", symbol: <Lock size={15} className='mr-4 ml-2' />, url: '/passwords'},
  {option: "Add Password", symbol: <Plus size={15} className='mr-4 ml-2' />, url: '/AddPassword'},
  {option: "Password Generator", symbol: <Shield size={15} className='mr-4 ml-2' />, url: '/generatePassword'},
  {option: "Profile", symbol: <User size={15} className='mr-4 ml-2' />, url: '/profile'},
  {option: "Settings", symbol: <Settings size={15} className='mr-4 ml-2' />, url: '/settings'},
  {option: "Activity Log", symbol: <Calendar size={15} className='mr-4 ml-2' />, url: '/activity'},
]

  const [currentSelected, setCurrentSelected] = useState(options[0])
  const [previousSelected, setPreviousSelected] = useState(currentSelected)

  
  const handleOptionSwitch = ( optionObj ) => {
    navigate(optionObj.url) 
    setCurrentSelected(optionObj)
  }


  
  const handlelogout = async (e) => {
    
    try {
      if(await logoutUser()) {
        navigate("/auth/login")
      } else {
        <ErrorPopup message="Could not logout user" />
      }
    } catch (error) {
      <ErrorPopup message="Could not logout user" />
      console.error("Could not logout user: " + error)
    }
  }
    

  return (
    <div className={`flex flex-col justify-between fixed left-0 top-0 h-full w-auto bg-gray-100 shadow-md z-50 transform transition-transform duration-300 ease-in-out
    ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}>
    {/* Top section */}
    <div className="p-4 flex flex-col items-start text-gray-700">
      <p className='text-gray-400 text-sm font-medium text-left'>PASSWORD MANAGEMENT</p>
      {options.slice(0,4).map((optionObj, index) => (
        <button
          key={index}
          onClick={() => handleOptionSwitch(optionObj)}
          className={`flex flex-row items-center hover:cursor-pointer text-base hover:bg-gray-300 rounded-lg w-full pt-2 pb-2 font-medium mb-1 mr-2 ${currentSelected.option === optionObj.option ? 'bg-purple-500' : 'bg-gray-100'}`}
        >
          {optionObj.symbol} {optionObj.option}
        </button>
      ))}

      <p className='text-gray-400 text-sm font-medium text-left pt-5'>ACCOUNT</p>
      {options.slice(4).map((optionObj, index) => (
        <button
          key={index}
          onClick={() => handleOptionSwitch(optionObj)}
          className={`flex flex-row items-center hover:cursor-pointer text-base hover:bg-gray-300 rounded-lg w-full pt-2 pb-2 font-medium mb-1 mr-2 ${currentSelected.option === optionObj.option ? 'bg-purple-500' : 'bg-gray-100'}`}
        >
          {optionObj.symbol} {optionObj.option}
        </button>
      ))}
    </div>

    {/* Bottom section */}
    <div className=''>
       <button
          className={`flex flex-row items-center hover:cursor-pointer text-base text-gray-800 hover:bg-gray-300 rounded-lg w-full pt-2 pb-2 font-medium mb-1 mr-2`}
          onClick={handlelogout}
        >
          LOG OUT
        </button>
    </div>
  </div>

  );
}

