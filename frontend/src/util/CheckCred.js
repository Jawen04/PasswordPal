export default async function CheckPassword(username, password) {
    const apiURL = 'http://localhost:8080/auth/login'

    try {
      const response = await fetch(apiURL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username, password }),
      });
  
      if (!response.ok) {
        throw new Error(`Server responded with status ${response.status}`);
      }  
      const data = await response.json();
      return data.status === "GRANTED";
    } catch (error) {
      console.error("Login check failed:", error);
      return false;
    }
  }
  