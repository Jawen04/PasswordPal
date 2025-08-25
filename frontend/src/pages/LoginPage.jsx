import { useContext, useEffect, useState } from 'react';
import { CredentialsContext } from '../util/LoginContext';
import loginImg from '../assets/login.png';
import backgroundImg from '../assets/nature.jpg' 
import checkCred from '../util/CheckCred'
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { User, Lock, LogIn } from 'lucide-react'
import { loginUser } from "../util/loginUser"

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [granted, setGranted] = useState(false)
  const [loading, setLoading] = useState(false);


  const navigate = useNavigate();

  
  // useEffect(() => {
  //   async function handleActiveSession() {
  //     const handleActiveSession = await checkActiveSession()
  //     if(handleActiveSession) {
  //       navigate("/dashboard")
  //     }
  //   }
  //   handleActiveSession();
  // }, []);


  const handleLogIn = async (e) => {
    if(loading) return; 
    if (username === "" || password === "") {
      setGranted(false);
      setLoading(true);
      setTimeout(() => setLoading(false), 2000);
      return;
    }

    setLoading(true);
    const isValid = await loginUser(username, password);
    setLoading(isValid);
    if (isValid) {
      setGranted(true)
      navigate("/dashboard");
    } else {
      setGranted(false);
    }

  };



  return (

      <div className='flex justify-center items-center h-screen'>      
        
        <div className='flex flex-col pl-20 pr-20 pt-10 pb-5 border-2 rounded-2xl shadow-2xl'>
          <div className='flex flex-col justify-center items-center pb-10'>
            <p className='text-2xl font-bold text-black'>Sign In</p>
            <p className='text-sm text-gray-400'>Enter your credentials to access your account</p>
            
          </div>
          {/* USERNAME FIELD */}
            <div className='w-full'>
              <p className='text-black text-sm '>Username</p>
              <div className="relative">
                <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
                <input
                  id="email"
                  type="email"
                  placeholder="Enter your username"
                  onChange={(e) => setUsername(e.target.value)}
                  className="text-black w-full pl-10 h-12 border-slate-200 focus:border-primary focus:ring-primary/20 transition-all duration-200"
                  required
                />
              </div>
            </div>

          {/* PASSWORD FIELD */}
            <div className='mt-5 w-full'>
              <p className='text-black text-sm '>Password</p>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
                <input
                  id="password"
                  type="password"
                  placeholder="Enter your email"
                  onChange={(e) => setPassword(e.target.value)}
                  className="text-black w-full pl-10 h-12 border-slate-200 focus:border-primary focus:ring-primary/20 transition-all duration-200"
                  onKeyDown={(e) => {
                    if (e.key === "Enter" && !loading) {
                      handleLogIn();
                    }
                  }}
                  required
                />
              </div>
            </div>

            <div
              key="signIn-btn"
               className={`h-12 border-2 rounded-xl mt-5 transition-colors duration-500 ${
                  loading && granted ? "bg-green-600" :
                  loading && !granted ? "bg-red-600" :
                  "bg-blue-600"
                }`}
            >
              <button
                onClick={() => handleLogIn()}
                disabled={loading}
                className='text-white w-full h-full flex justify-center items-center hover:cursor-pointer font-bold'
              >
                <LogIn className='mr-3' /> {loading ? "Signing In..." : "Sign In"}
              </button>

            </div>
            <div className='flex flex-row justify-center items-center pt-20'>
              <p className='text-gray-500 text-sm' href="/createAccount">Don't have an account? </p>
              <a className='text-black text-sm font-bold' href="/createAccount">&nbsp;Sign Up</a>

            </div>
          </div>
          
        </div>
    
  );
}
