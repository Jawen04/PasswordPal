import { useContext } from "react";
import { CredentialsContext } from "./LoginContext";

export default function useAddServicePassword() {

  const addServicePassword = async ({ user, serviceName, username, password }) => {
    const apiURL = "http://localhost:8080/api/user/addNewLogin";
    console.log("FETCHING...");

    try {   
      const response = await fetch(apiURL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
          ownerUsername: user,
          serviceName,
          serviceUsername: username,
          servicePassword: password,
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
  };

  return addServicePassword;
}
