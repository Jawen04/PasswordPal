export async function getAllStoredLogins({ username }) {
  try {
    const response = await fetch('http://localhost:8080/api/user/getAllLogins', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();

    const list = data.map(entry => ({
      service: entry.service,
      username: entry.username,
      password: entry.password
    }));

    return list;

  } catch (error) {
    console.error('Error:', error);
    return []; 
  }
}
