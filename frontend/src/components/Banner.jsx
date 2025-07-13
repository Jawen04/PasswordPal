import { useContext, useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import SideBar from './SideBar';
import { Menu } from 'lucide-react';



export default function Banner() {
    const [isDropOpen, setIsDropOpen] = useState(false);
    const [isSideOpen, setIsSideOPen] = useState(false); 
    return (
        <div className=''>
            <div className='flex items-center bg-gradient-to-br from-red-900 via-red-700 to-red-400 shadow-lg'>
                <div className='flex items-center pt-4 pb-4'>
                    <button 
                        className='ml-2 hover:text-gray-700 '
                        onClick={() => setIsSideOPen(!isSideOpen)}
                        
                        ><Menu className='hover:cursor-pointer'/>
                    </button>
                    <h1 className='text-4xl font-bold ml-2'>PasswordPal</h1>
                </div>
            </div>
            <SideBar className='' isOpen={isSideOpen}/>
            {isSideOpen && (
            <div
                onClick={() => setIsSideOPen(false)}
                className="fixed inset-0 bg-opacity-40 backdrop-blur-sm z-40"
            />
            )}


        </div>
    )
}





function DropDown({isOpen}) {

    if(isOpen) {
        return (
            <div className=''>      
                <div className='dropdown-item' style={{paddingTop:'10px'}}>
                    <a href='https://www.youtube.com/' style={{color:'black', textDecoration:'none'}}>
                        My Account
                        </a>
                </div>
                <div className='dropdown-item'>
                    <a href='https://www.youtube.com/' style={{color:'black', textDecoration:'none'}}>
                        Profile
                        </a>
                </div>
                <div className='dropdown-item'>
                    <a href='https://www.youtube.com/' style={{color:'black', textDecoration:'none'}}>
                        Settings
                        </a>
                </div>
                <div className='dropdown-item'  style={{paddingBottom:'10px'}}>
                    <a href='/auth/login' style={{color:'black', textDecoration:'none'}}>
                    Log out
                    </a>
                </div>
                

            </div>
        )
    }
        
}