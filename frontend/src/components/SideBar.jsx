import { useNavigate } from 'react-router-dom';
import { Key, Lock, Shield, User, Settings, Calendar } from 'lucide-react';
import { useState } from "react"



export default function SideBar({ isOpen }) {

  const navigate = useNavigate();



  const options = [
  {option: "Dashboard", symbol: <Key size={15} className='mr-4 ml-2' />, url: '/dashboard'},
  {option: "Passwords", symbol: <Lock size={15} className='mr-4 ml-2' />, url: '/passwords'},
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

    


    // previousSelected.setIsSelected(false)
    // console.log(previousSelected.isSelected)
    // optionObj.setIsSelected(true)
    // setPreviousSelected(optionObj)

  }

  return (
    <div
      className={`fixed left-0 top-0 h-full w-auto bg-gray-100 shadow-md z-50 transform transition-transform duration-300 ease-in-out
      ${isOpen ? 'translate-x-0' : '-translate-x-full'}`}
    >
      <div className="p-4 flex flex-col items-start text-gray-700">
        <p className='text-gray-400 text-sm font-medium text-left'>PASSWORD MANAGEMENT</p>
        {options.slice(0,3).map((optionObj, index) => (
          <button key={index} onClick={() => handleOptionSwitch(optionObj)} className={`flex flex-row items-center hover:cursor-pointer text-base hover:bg-gray-300 rounded-lg w-full pt-2 pb-2 font-medium mb-1 mr-2 ${currentSelected.option === optionObj.option ? 'bg-purple-500' : 'bg-gray-100'}`}> {optionObj.symbol} {optionObj.option}</button>
        ))}
        
        <p className='text-gray-400 text-sm font-medium text-left pt-5'>ACCOUNT</p>
        {options.slice(3).map((optionObj, index) => (
            <button key={index} onClick={() => handleOptionSwitch(optionObj) } className={`flex flex-row items-center hover:cursor-pointer text-base hover:bg-gray-300 rounded-lg w-full pt-2 pb-2 font-medium mb-1 mr-2 ${currentSelected.option === optionObj.option ? 'bg-purple-500' : 'bg-gray-100'}`}> {optionObj.symbol} {optionObj.option}</button>
        ))}
      </div>
    </div>
  );
}

