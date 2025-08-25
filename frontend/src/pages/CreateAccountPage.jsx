import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import { User, Lock, LogIn } from 'lucide-react';
import CreateUser from '../util/CreateUser';

export default function CreateAccountPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState("idle"); // "idle" | "loading" | "success" | "error"

  const navigate = useNavigate();

  const handleRegister = async () => {
    if (status === "loading") return; 

    if (username.trim() === "" || password.trim() === "") {
      setStatus("error");
      return;
    }

    setStatus("loading");

    const backendResponse = await CreateUser(username, password);

    if (backendResponse === 0) {
      setStatus("success");
      setTimeout(() => {
        navigate("/auth/login");
      }, 2000); 
    } else if(backendResponse === 1) {
      setStatus("occupied");
    } else  {
      setStatus("error");
    } 
  };

  const buttonColor =
    status === "loading" ? "bg-yellow-600" :
    status === "success" ? "bg-green-600" :
    status === "error"   ? "bg-red-600" :
    "bg-blue-600";

  const buttonText =
    status === "loading" ? "Registering..." :
    status === "success" ? "User registered!" :
    status === "error"   ? "Registration failed!" :
    status === "occupied" ? "Username already registered!" :
    "Register";

  return (
    <div className='flex justify-center items-center h-screen'>      
      <div className='flex flex-col pl-20 pr-20 pt-10 pb-5 border-2 rounded-2xl shadow-2xl'>
        
        <div className='flex flex-col justify-center items-center pb-10'>
          <p className='text-2xl font-bold text-black'>Register your new account</p>
          <p className='text-sm text-gray-400'>Fill in username and password</p>
        </div>

        {/* USERNAME FIELD */}
        <div className='w-full'>
          <p className='text-black text-sm'>Username</p>
          <div className="relative">
            <User className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Choose your username"
              onChange={(e) => setUsername(e.target.value)}
              className="text-black w-full pl-10 h-12 border-slate-200 focus:border-primary focus:ring-primary/20 transition-all duration-200"
              required
            />
          </div>
        </div>

        {/* PASSWORD FIELD */}
        <div className='mt-5 w-full'>
          <p className='text-black text-sm'>Password</p>
          <div className="relative">
            <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
            <input
              type="password"
              placeholder="Choose your password"
              onChange={(e) => setPassword(e.target.value)}
              className="text-black w-full pl-10 h-12 border-slate-200 focus:border-primary focus:ring-primary/20 transition-all duration-200"
              onKeyDown={(e) => {
                if (e.key === "Enter" && status !== "loading") {
                  handleRegister();
                }
              }}
              required
            />
          </div>
        </div>

        {/* REGISTER BUTTON */}
        <div
          className={`h-12 border-2 rounded-xl mt-5 transition-colors duration-500 ${buttonColor}`}
        >
          <button
            onClick={handleRegister}
            disabled={status === "loading"}
            className='text-white w-full h-full flex justify-center items-center hover:cursor-pointer font-bold'
          >
            <LogIn className='mr-3' /> 
            {buttonText}
          </button>
        </div>

        <div className='flex flex-row justify-center items-center pt-20'>
          <a className='text-black text-sm font-bold' href="/auth/login">
            &nbsp;Back to login
          </a>
        </div>

      </div>
    </div>
  );
}
