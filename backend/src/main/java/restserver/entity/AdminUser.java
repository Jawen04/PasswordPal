package restserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AdminUser {

    private String username;
    private String password; // Replacing Time object with its ID

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public AdminUser() {}

    public AdminUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }
    
}
