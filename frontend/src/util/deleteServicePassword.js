import { useContext } from "react";
import { CredentialsContext } from "./LoginContext";

export default async function deleteServicePassword( { serviceName, serviceUsername, servicePassword }) {

console.log("received: " + serviceName + " " + serviceUsername + " " + servicePassword)
  try {   
    const response = await fetch("http://localhost:8080/api/user/removeLogin", {
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
      return false;
    }

    const data = await response.json();
    return data.status === "OK";

  } catch (error) {
    console.error("Login check failed:", error);
    return false;
  }
  

}
