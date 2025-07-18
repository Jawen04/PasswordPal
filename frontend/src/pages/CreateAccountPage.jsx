import { useContext, useState } from 'react';
import backgroundImg from '../assets/nature.jpg' 
import CreateUser from '../util/CreateUser';
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from "react-router-dom";

export default function CreateAccountPage() {
    const [username, setUsername] = useState("");
    const [password1, setPassword1] = useState("");
    const [password2, setPassword2] = useState("");


    const navigate = useNavigate();

    const credOK = () => {
        return (
            username.trim() !== "" &&
            password1.trim() !== "" &&
            password2.trim() !== "" &&
            password1 === password2
        );
    };


    const submit = async (e) => {
        if(!credOK()) {
            alert("Enter valid username and password!")
            return;
        }
        const response = await CreateUser(username, password2);
        if(response) {
          navigate("/auth/login")
        }
    }

  const handleKeyDown = async (e) => {
    if (e.key === 'Enter') {
      if  (!credOK()) {
        alert("select a username and a password");
      } else {

      }
    }
  };

  return (
    <div className='content'>
      <div className='background'>      
        <div className='login-container'>
          <div className='welcome-text'>
            <h1>Create your account at PasswordPal</h1>
          </div>
          <div className='pass-and-username'>
            <h5 >Choose username</h5>            
            <input
              type='text'
              className='text-input'
              placeholder='E.g CoolBoi83'
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              style={{marginBottom: '20px'}}
            />
             <h5 >Choose password</h5>
            <input
              type='text'
              className='text-input'
              placeholder='E.g keySnake54 '
              value={password1}
              onChange={(e) => setPassword1(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <h5>Re-enter password</h5>
            <input
              type='text'
              className='text-input'
              placeholder='E.g keySnake54 '
              value={password2}
              onChange={(e) => setPassword2(e.target.value)}
              onKeyDown={handleKeyDown}
              style={{marginBottom:'40px'}}
            />
            <button 
                className='create-account-btn'
                onClick={submit}
                >
                Create account

                
            </button>

            
          </div>
        </div>
      </div>
    </div>
    
  );
}
