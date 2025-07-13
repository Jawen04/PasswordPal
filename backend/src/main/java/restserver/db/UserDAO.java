package restserver.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDAO {

    private final DataSource dataSource;

    @Autowired
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("Attempting to register user: " + username);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            System.out.println("User: " + username + " registered successfully.");
        } catch (SQLException e) {
            System.err.println("SQL Error during user registration: " + e.getMessage());
        }
    }

    public Integer getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try(ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        return rs.getInt("id");
                    }
                }
            } catch (SQLException e) {
            System.err.println("Error getting user ID: " + e.getMessage());
        }
        return null;        
    }

    public boolean addStoredLogin(String ownerUsername, String serviceName, String loginUsername, String loginPassword) {
        Integer userId = getUserIdByUsername(ownerUsername);

        if(userId == null) {
            System.out.println("User not found: " + ownerUsername);
            return false;
        }

        String sql = "INSERT INTO user_accounts (user_id, service_name, login_username, login_password) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, serviceName);
            stmt.setString(3, loginUsername);

             // TODO: Encrypt loginPassword before saving, for now storing plain text
                stmt.setString(4, loginPassword);

                stmt.executeUpdate();
                System.out.println("Stored login added for user " + ownerUsername + " service " + serviceName);
                return true;
        } catch (SQLException e) {
            System.err.println("Error adding stored login: " + e.getMessage());
            return false;
        }

        



    }
    public boolean removeUser(String username) {
        if ("admin".equals(username)) {
            System.out.println("Cannot delete admin user.");
            return false;
        }

        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User " + username + " has been removed.");
                return true;
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return false;
    }

    public Map<String, String> getAllUsers() {
        Map<String, String> userMap = new HashMap<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                userMap.put(username, password);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return userMap;
    }

    public boolean isExistingUser(String username) {
        String sql = "SELECT * FROM users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                if(username.replaceAll("\\s+", "").equals(resultSet.getString("username").replaceAll("\\s+", ""))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return false;
    }


    public void addPassword(String username, String password) {






    }



    public boolean testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return false;
        }
    }
}
