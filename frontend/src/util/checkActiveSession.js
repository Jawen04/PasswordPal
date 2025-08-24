export async function checkActiveSession() {
  try {
    const response = await fetch('http://localhost:8080/api/user/hasActiveSession', {
      method: 'GET',
      credentials: 'include',
    });

    if (response.ok) {
      const data = await response.json();
      return data.status === "OK"; // true if session is valid
    } else if (response.status === 401) {
      return false; // no active session
    } else {
      console.error("Unexpected response:", response.status);
      return false;
    }
  } catch (err) {
    console.error('Error fetching current session:', err);
    return false; // always return boolean
  }
}
