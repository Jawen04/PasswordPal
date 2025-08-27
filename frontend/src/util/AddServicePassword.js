import { useContext } from "react";
import { CredentialsContext } from "./LoginContext";

export default async function useAddServicePassword( { serviceName, serviceUsername, servicePassword }) {
  try {   
    const response = await fetch("http://localhost:8080/api/user/addNewLogin", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
      body: JSON.stringify({
        serviceName: serviceName ,
        serviceUsername: serviceUsername,
        servicePassword: servicePassword,
      }),
    });

    if (!response.ok) {
      alert("Request failed");
      return false;
    }

    const data = await response.json();
    return data.status === "OK";

  } catch (error) {
    console.error("Login check failed:", error);
    alert("Could not connect to server or invalid response.");
    return false;
  }
  

}
