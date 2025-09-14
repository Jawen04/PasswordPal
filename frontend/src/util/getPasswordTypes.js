import { getAllStoredLogins } from "./getAllStoredLogins";

export async function getPasswordTypes() {
    const passwordTypes = {
        totalPasswords: 0,
        strongPasswords: 0,
        moderatePasswords: 0,
        weakPasswords: 0,
        reusedPasswords: 0,
        securityScore: 0
    }
    try {
        const allPasswords = await getAllStoredLogins()
        passwordTypes.totalPasswords = allPasswords.length


        allPasswords.forEach(entry => {
            const strength = passwordStrength(entry.servicePassword)
            if(strength === 2) {passwordTypes.strongPasswords++}
            if(strength === 1) {passwordTypes.moderatePasswords++}
            if(strength === 0) {passwordTypes.weakPasswords++}
        })

        const seen = new Set()
        allPasswords.forEach(entry => {
            if (seen.has(entry.servicePassword)) {
                passwordTypes.reusedPasswords++
            } else {
                seen.add(entry.servicePassword)
            }
        })

        passwordTypes.securityScore = calculateSecurityScore(allPasswords)


    } catch (error) {
        console.error(error)
    }

    return passwordTypes

}

function passwordStrength(password) {
    const length = password.length;
    const hasUppercase = /[A-Z]/.test(password);
    const hasLowercase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    const criteriaMet = [hasUppercase, hasLowercase, hasNumber, hasSpecialChar].filter(Boolean).length;

    // Strong
    if (length >= 8 && criteriaMet === 4) {
    return 2;
    // Moderate
    } else if (length >= 6 && criteriaMet >= 2) {
    return 1;

    // Weak
    } else {
    return 0;
    }
}




function calculateSecurityScore(allPasswords) {

    if (allPasswords.length === 0) return 100; // no passwords = perfect score

    let score = 0;
    let reusedCount = 0;
    const seenPasswords = new Set();

    allPasswords.forEach(entry => {
        const strength = passwordStrength(entry.servicePassword);

        // Assign points based on strength
        if (strength === 2) score += 3; // strong
        if (strength === 1) score += 2; // moderate
        if (strength === 0) score += 1; // weak

        // Check for reused passwords
        if (seenPasswords.has(entry.servicePassword)) {
            reusedCount++;
        } else {
            seenPasswords.add(entry.servicePassword);
        }
    });

    // Calculate max possible score
    const maxScore = allPasswords.length * 3; // 3 points for each strong password

    // Apply penalty for reused passwords
    const reusePenalty = reusedCount * 2; // subtract 2 points per reuse
    let finalScore = ((score - reusePenalty) / maxScore) * 100;

    // Ensure score is between 0-100
    finalScore = Math.max(0, Math.min(100, finalScore));

    return Math.round(finalScore);
}


