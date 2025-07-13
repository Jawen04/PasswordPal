import { Navigate, useNavigate } from 'react-router-dom';
import { Plus } from 'lucide-react';


export default function TitleAndBtn( {title, message }) {
    const navigate = useNavigate();

    return (
        <div className='flex flex-row mt-10 mb-10 justify-between'>
            <div className='flex flex-col'>
              <p className='text-black text-left font-bold text-4xl'>{title}</p>
              <p className='text-gray-400'>{message}</p>
            </div>
            
            <button
              className='text-white justify-center items-center bg-red-400 h-15 rounded-xl p-4 hover:cursor-pointer hover:bg-red-300 flex flex-row'
              onClick={() => navigate("/generatePassword")}
              >
              <Plus  className='pr-1' /> Add Password

            </button>
        </div>
    )
}