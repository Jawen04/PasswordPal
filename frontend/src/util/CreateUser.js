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
      const status = data.status;

      if(status === "OCCUPIED") {
        return 1;
      } else if(status === "OK") {
        return 0;
      } else if(status === "ERROR") {
        return -1;
      }

      } catch (error) {
        console.error("User registration failed", error);
        return -1;
    }

}
