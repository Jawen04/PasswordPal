



import { useEffect, useRef, useState } from 'react';
import { Eye, EyeOff, Copy, Trash } from 'lucide-react';

export default function CredentialsCard({ name, email, password, onDelete }) {
  const [showPassword, setShowPassword] = useState(false);
  const containerRef = useRef(null);
  const [passwordVisible, setPasswordVisible] = useState(false)

  const handleEyeToggle = () => {
    setShowPassword((prev) => !prev)
    setPasswordVisible(!passwordVisible)
  }




  // Hide password when clicking outside the component
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setShowPassword(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div
      ref={containerRef}
      className="flex flex-row items-center border border-gray-200 rounded-lg h-30 p-4 space-x-4 hover:shadow-lg"
    >
      <div className="flex justify-center items-center w-15 h-15 bg-gray-200 rounded-lg">
        <p className="text-gray-400 font-bold text-3xl">{name.charAt(0).toUpperCase()}</p>
      </div>

      <div>
        <p className="text-black font-semibold">{name}</p>
        <p className="text-gray-400 text-sm font-light">{email}</p>

        <div className="flex flex-row items-center space-x-2 mt-1">
          <div
            className="w-[160px] overflow-x-auto whitespace-nowrap px-1 text-black select-text hover:cursor-ew-resize"
            style={{
                WebkitOverflowScrolling: 'touch',
                scrollbarWidth: 'none', // Firefox
                msOverflowStyle: 'none', // IE 10+
            }}>
            <p className="inline-block">
              {showPassword ? password : '•'.repeat(password.length)}
            </p>
          </div>

          <button onClick={() => handleEyeToggle() } className="text-black shrink-0 hover:cursor-pointer  rounded-sm p-1 hover:bg-gray-200">
            {passwordVisible ? <EyeOff size={20} /> : <Eye size={20} />}
          </button>
          <button 
            className="text-black shrink-0 hover:cursor-pointer rounded-sm p-1 hover:bg-gray-200"
            onClick={() => navigator.clipboard.writeText(password)}
            >
            <Copy size={20} />
          </button>
          <button 
            className="text-black shrink-0 hover:cursor-pointer rounded-sm p-1 hover:bg-gray-200"
            onClick={onDelete}
            >
            <Trash size={20} />
          </button>
        </div>
      </div>
    </div>
  );
}



