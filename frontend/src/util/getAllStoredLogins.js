
export async function getAllStoredLogins() {
  
  try {
    const response = await fetch('http://localhost:8080/api/user/getAllLogins', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    const list = data.map(entry => ({
      serviceName: entry.serviceName,
      serviceUsername: entry.serviceUsername,
      servicePassword: entry.servicePassword
    }));


    
    return list;

  } catch (error) {
    console.error('Error:', error);
    return []; 
  }
}
