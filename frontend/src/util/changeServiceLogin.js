export async function changeServiceLogin({
  oldServiceName,
  oldUsername,
  oldServicePassword,
  newServiceName,
  newUsername,
  newServicePassword,
}) {
  try {
    const response = await fetch("http://localhost:8080/api/user/changeServiceLogin", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include", // send cookies like sessionId
      body: JSON.stringify({
        oldServiceName,
        oldUsername,
        oldServicePassword,
        newServiceName,
        newUsername,
        newServicePassword,
      }),
    });

    const data = await response.json();

    if (response.ok && data.status === "OK") {
      return true;
    } else {
      return false;
    }
  } catch (err) {
    console.error("Error calling changeServiceLogin:", err);
    return false;
  }
}
