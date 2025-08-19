package restserver.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.UUID;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import restserver.entity.User;

@Repository
public class UserDAO {

    private final DataSource dataSource;

    @Autowired
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String createNewUserSession(int userId) {
        String sql = "INSERT INTO user_sessions (session_id, user_id) VALUES (?, ?)";
        String sessionId = UUID.randomUUID().toString();

        if(userId == -1) {
            System.out.println("Could not find the requested user ID");
            return "";
        }

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            System.out.println("Attempting to create a new session");
            pstmt.setString(1, sessionId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("New session created!");
            return sessionId;
        } catch (SQLException e) {
            System.err.println("SQL Error during session initialization: " + e.getMessage());
        }
        return null;
    }


    public boolean terminateSession(String sessionId) {
        String sql = "DELETE FROM user_sessions WHERE session_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Session terminated!");
                return true;
            } else {
                System.out.println("Could not terminate session");
                
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    public boolean isUserLoggedIn(String sessionId, int userId) {
        String sql = "SELECT 1 FROM user_sessions WHERE session_id = ? AND user_id = ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            System.err.println("Error checking login status: " + e.getMessage());
        }
        return false;
    }

    public int getCurrentSignedInUser() {
        String sql = "SELECT * FROM user_sessions ORDER BY user_id ASC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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

    public String getUsernameById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting username by ID: " + e.getMessage());
        }
        return null; 
    }


    public int getUserIdByUsername(String username) {
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
        return -1;        
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + e.getMessage());
        }
        return null;
        }

        public int getUserIdBySession(String sessionId) {
        String sql = "SELECT user_id FROM user_sessions WHERE session_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID by session: " + e.getMessage());
        }
        return -1;
    }




    // TODO: hash the following credentials
    public boolean addStoredLogin(String ownerUsername, String serviceName, String loginUsername, String loginPassword) {
        System.out.println("Adding stored login for user: " + ownerUsername + ", service: " + serviceName + ", username: " + loginUsername);
        int userId = getUserIdByUsername(ownerUsername);

        if(userId == -1) {
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


    public List<Map<String, String>> getStoredLogins(String username) {
        List<Map<String, String>> storedLogins = new ArrayList<>();
        int userId = getUserIdByUsername(username);

        if (userId == -1) {
            System.out.println("User not found: " + username);
            return storedLogins;
        }

        String sql = "SELECT service_name, login_username, login_password FROM user_accounts WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> loginEntry = new HashMap<>();
                    loginEntry.put("service", rs.getString("service_name"));
                    loginEntry.put("username", rs.getString("login_username"));
                    loginEntry.put("password", rs.getString("login_password")); // plaintext for now
                    storedLogins.add(loginEntry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving stored logins: " + e.getMessage());
        }

        return storedLogins;
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

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                userList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return userList;
    }



    public boolean isExistingUser(String username) {
    String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username.trim());
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        System.err.println("Error: " + e.getMessage());
    }
    return false;
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
