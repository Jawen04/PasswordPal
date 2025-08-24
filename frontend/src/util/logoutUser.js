

export async function logoutUser() {
    try {
        const response = await fetch('http://localhost:8080/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        });

        const data = await response.json();
    

        if (data.status === 'OK') {
            console.log("Successfully logged out")
            return true;
        }
    } catch (err) {
        console.error('Error during login:', err);
        return false;
    }
    return false
    
}