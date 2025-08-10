
export async function getCurrentSignedInUser() {
  
  try {
    const response = await fetch('http://localhost:8080/api/user/getCurrentSignedInUser', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();

  } catch (error) {
    console.error('Error:', error);
    return []; 
  }
}
