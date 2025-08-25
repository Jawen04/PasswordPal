export default async function CreateUser(username, password) {
    try {
      const response = await fetch('http://localhost:8080/api/user/register', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        throw new Error(`Server responded with status ${response.status}`);
      }  
      const data = await response.json();
      return data.status === "OK";

      } catch (error) {
        console.error("User registration failed", error);
        alert("Could not connect to server or invalid response.");
      return false;
    }

}