import Card from "../components/Card";
import TitleAndBtn from "../components/TitleAndBtn";
import Banner from "../components/Banner";
import CredentialsCard from "../components/CredentialsCard";



export default function PasswordsPage() {

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
            <TitleAndBtn title={"Passwords"} message={"Manage all your stored passwords"}/>
            <PasswordsCard />
        </div>
    )
}


function PasswordsCard() {

    const credentials = [
        {name: "Example", email: "example@email.com", password: "SuperPass123"},
        {name: "kdeokd", email: "example@email.com", password: "SuperPass123"},
        {name: "dkeo", email: "example@email.com", password: "SuperPass123"},
        {name: "qess", email: "example@email.com", password: "SuperPass123"},
        {name: "deokd", email: "example@email.com", password: "SuperPass123"}


    ]
    return (
        <Card className="p-6 max-w-4xl mx-auto w-full">
            <p className="text-black font-bold text-xl">All Passwords</p>
            <p className="text-gray-400">View, edit, and manage your saved passwords</p>
            <div className="flex flex-col space-y-2">
            {credentials.map((credObj, index) => (
                    <CredentialsCard name={credObj.name} email={credObj.email} password={credObj.password}/>


                

            ))}
            </div>
        </Card>
        



    )
}

