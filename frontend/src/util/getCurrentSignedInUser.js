
export async function getCurrentSignedInUser() {
  try {
    const response = await fetch('http://localhost:8080/api/user/currentUser', {
      method: 'GET',
      credentials: 'include'
    });

    if(!response.ok) {
      throw new Error(response)
    }

    const data = await response.json(); // this works now
    return data;
  } catch (err) {
    console.error('Error fetching current user:', err);
  }
}

