
export async function getCurrentSignedInUser(sessionId) {
  try {
    const response = await fetch('http://localhost:8080/api/user/getCurrentSignedInUser', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Session-Id': sessionId
      }
    });

    const data = await response.json(); // this works now
    console.log(data);
    return data;
  } catch (err) {
    console.error('Error fetching current user:', err);
  }
}

