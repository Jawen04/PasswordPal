
export async function getCurrentSignedInUser() {
  
  try {
    const response = await fetch('http://localhost:8080/api/user/getCurrentSignedInUser', {
      method: 'GET',
      headers: { 'Content-Type': 'application/json' },
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(`HTTP error! status: ${response.status}, message: ${text}`);
    }

    return await response.json();
    
  } catch (error) {
    console.error('Error:', error);
    return null; 
  }
}
