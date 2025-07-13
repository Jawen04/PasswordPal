import React from "react"


export default function Card( {children} ) {
    return (
        <div className="h-auto p-2 w-auto flex-col justify-center border border-gray-200 rounded-md shadow-lg ">
            {children}

        </div>

    )

}
