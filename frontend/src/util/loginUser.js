export async function loginUser(username, password) {
  try {
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password }),
      credentials: 'include'
    });

    const data = await response.json();
    

    if (data.status === 'OK') {
      // Save session ID in localStorage
      localStorage.setItem('sessionId', data.sessionId);
      return true;
    } else {
      console.error('Login failed:', data.message);
      return false;
    }
  } catch (err) {
    console.error('Error during login:', err);
    return false;
  }
}
