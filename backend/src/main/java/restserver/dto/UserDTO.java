package restserver.dto;

public class UserDTO {
    private String username;
    private String password;

<<<<<<< HEAD
=======


>>>>>>> master
    // No-args constructor (important for JSON deserialization)
    public UserDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
