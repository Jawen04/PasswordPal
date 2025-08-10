package restserver.entity;

import java.security.Timestamp;

public class Session {
    
    private Timestamp loginTime;
    private Timestamp lastLogin;

    public Session(Timestamp loginTime, Timestamp lastLogin) {
        this.loginTime = loginTime;
        this.lastLogin = lastLogin;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }


}
