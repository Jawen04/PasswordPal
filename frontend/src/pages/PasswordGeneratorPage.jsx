import { useState } from "react"
import Banner from "../components/Banner"
import Card from '../components/Card'
import { RefreshCw } from 'lucide-react';
import { Copy } from 'lucide-react';
import { generatePassword } from "../util/GeneratePassword";
import Switch from "../components/Switch";
import ErrorPopup from "../components/ErrorPopUp";




export default function PasswordGeneratorPage() {
    return (
        <div>
            <Banner />    
            <div className='flex flex-col justify-center items-center'>
                <Content />
            </div>
        </div>
    )
}



function Content() {
    return(
        <div className='w-full max-w-3xl px-4 ml-10 mr-10'>
            <Title />
            <Generator />
        </div>
    )

}


function Title() {
    return (
        <div className="mt-10 mb-10">
            <p className='text-black text-left font-bold text-4xl'>Generate Password</p>
            <p className='text-gray-400'>Generate strong, secure passwords</p>
        </div>

    )
}   

function Generator() {

    const [includeUpper, setIncludeUpper] = useState(true);
    const [includeLower, setIncludeLower] = useState(true);
    const [includeNumbers, setIncludeNumbers] = useState(true);
    const [includeSpecial, setIncludeSpecial] = useState(true);


    const types = [
    { type: "Include Uppercase Letters", value: includeUpper, setValue: setIncludeUpper },
    { type: "Include Lowercase Letters", value: includeLower, setValue: setIncludeLower },
    { type: "Include Numbers", value: includeNumbers, setValue: setIncludeNumbers },
    { type: "Include Special Characters", value: includeSpecial, setValue: setIncludeSpecial },
  ];

    const [generatedPassword, setGeneratedPassword] = useState("")
    const [passwordLength, setPasswordLength] = useState(16)

    const [showError, setShowError] = useState(false);


    const handleGeneratePassword = () => {
        if(types.every(t => t.value === false)) {
            setShowError(false);
            setTimeout(() => setShowError(true), 10);
            return
        }
        const newPassword = generatePassword(passwordLength, {
            includeUppercase: types[0].value,
            includeLowercase: types[1].value,
            includeNumbers: types[2].value,
            includeSymbols: types[3].value
        });        
        setGeneratedPassword(newPassword)
    }

    const handleSliderChange = (e) => {
        setPasswordLength(Number(e.target.value))
    }



    return (
        <Card className="p-1 max-w-4xl mx-auto w-full bg">
            <div className="flex flex-row justify-between items-center mt-5 mb-3">
                <p className="text-black font-semibold">Generated Password</p>
                <div className="flex justify-center items-center space-x-2 ">
                    <button className="flex justify-center items-center border border-gray-200 rounded-lg p-3 hover:cursor-pointer hover:bg-gray-100 ">
                        <Copy color="black" size={15}  />
                    </button>
                    <button 
                        className="flex justify-center items-center border border-gray-200 rounded-lg p-3 hover:cursor-pointer hover:bg-gray-100"
                        onClick={handleGeneratePassword}
                        >
                        <RefreshCw color="black" size={15} />
                    </button>
                </div>
            </div>
            <input
                type="text"
                readOnly
                value={generatedPassword}
                className="border border-gray-200 w-full rounded-lg h-10 text-black pl-2 pr-2"

            >
            </input>
            <div className="">
                <p className="text-black">Password Length: {passwordLength}</p>
                <input
                type="range"
                className="w-full slider "
                min="1"
                max="32"
                onChange={handleSliderChange}
                ></input>
            </div>
            {types.map((typeObj, index) => (
                <div key={index} className="flex flex-row justify-between">
                    <p className="text-black">{typeObj.type} {typeObj.value ? "t" : "f"}</p>
                    <Switch isOn={typeObj.value}  onToggle={typeObj.setValue}></Switch>
                </div>

            ))}
            {showError && <ErrorPopup message="At least one character type must be selected" />}


            


            
            
            
        </Card>
    )
}


function TypeAndSwitch() {

    return (
        <div>



        </div>

    )
}


