import { useState } from "react"


export default function Switch({ isOn, onToggle }) {
  return (
    <button
      onClick={() => onToggle(!isOn)}
      className={`w-12 h-6 rounded-full p-1 flex items-center transition duration-300 ${
        isOn ? 'bg-green-500' : 'bg-gray-300'
      }`}
    >
      <div
        className={`bg-white w-4 h-4 rounded-full shadow-md transform transition ${
          isOn ? 'translate-x-6' : ''
        }`}
      />
    </button>
  );
}
