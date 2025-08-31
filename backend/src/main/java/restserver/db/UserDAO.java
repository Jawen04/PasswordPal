package restserver.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restserver.config.AESKeyProvider;
import restserver.entity.User;
import restserver.util.AESUtils;

import restserver.util.HashUtils;

@Repository
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // key stored securely
    @Autowired
    private AESKeyProvider keyProvider;

    @Autowired
    public UserDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private SecretKey getSecretKey() {
        return keyProvider.getKey();
    }
    
    



    public String createNewUserSession(String username) {
        String sql = "INSERT INTO user_sessions (session_id, user_id) VALUES (?, ?)";
        String sessionId = UUID.randomUUID().toString();
        int userId = getUserIdByUsername(username);

        if(userId == -1) {
            logger.warn("Could not find the requested user ID with username: " + username);
            return null;
        }

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            logger.info("Attempting to create a new session");
            pstmt.setString(1, sessionId);
            pstmt.setInt(2, userId);
            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0) {
                logger.info("New session created with id: " + sessionId);
                return sessionId;
            } else {
                logger.warn("Could not insert new user session into db");
                return null;
            }
            
        } catch (SQLException e) {
            logger.warn("SQL Error during session initialization: " + e.getMessage());
        }
        return null;
    }

    public boolean isValidSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("SessionId cannot be null or empty!");
            return false;
        }

        String sql = "SELECT EXISTS(SELECT 1 FROM user_sessions WHERE session_id = ?)";


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1); // returns true if session exists
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.error("Error validating session: {}", e.getMessage(), e);
            return false;
        }
    }





    public boolean terminateSession(String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("SessionId field cannot be empty!");
            return false;
        }
        
        String sql = "DELETE FROM user_sessions WHERE session_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            logger.info("Attempting to terminate session with ID: " + sessionId);
            stmt.setString(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Session terminated!");
                return true;
            } else {
                logger.warn("Could not terminate session");
                return false;
            }
        } catch (SQLException e) {
            logger.warn("Error: " + e.getMessage());
        }
        return false;
    }

    public String getActiveSessionId(int userId) {
        String sql = "SELECT session_id FROM user_sessions WHERE user_id = ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("session_id"); // return the found sessionId
                }
            }

        } catch (SQLException e) {
            logger.warn("Error retrieving active session: " + e.getMessage());
        }

        return null; 
    }


    public boolean checkActiveSessionForSessionId(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("SessionId field cannot be empty!");
            return false;
        }

        String sql = "SELECT 1 FROM user_sessions WHERE session_id = ? LIMIT 1";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sessionId); 

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.warn("Error checking session: " + e.getMessage());
            return false;
        }


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




    public boolean validateUser(String username, String enteredPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    return passwordEncoder.matches(enteredPassword, hashedPassword);
                } else {
                    return false;
                }
            }

        } catch (SQLException e) {
            logger.warn("SQL Error during user validation: " + e.getMessage());
        }

        return false;
    }


   
    

    // returns an integer depending on the success of the registration
    // 0 -> user registered succesfully
    // 1 -> username already taken, unsuccessful
    // -1 -> error
    public int registerUser(String username, String plainPassword) {

        if(getUserByUsername(username) != null) {
            return 1;
        }
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        

        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, passwordEncoder.encode(plainPassword));
            pstmt.executeUpdate();
            logger.info("User: " + username + " registered successfully.");
            return 0;
        } catch (SQLException e) {
            logger.warn("SQL Error during user registration: " + e.getMessage());
            return -1;
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
            logger.warn("Error getting username by ID: " + e.getMessage());
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
            logger.warn("Error getting user ID: " + e.getMessage());
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
            logger.warn("Error fetching user by username: " + e.getMessage());
        }
        return null;
    }

    public int getUserIdForSessionId(String sessionId) {
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
            logger.warn("Error getting user ID by session: " + e.getMessage());
        }
        return -1;
    }


    public boolean addStoredLogin(String serviceName, String loginUsername, String loginPassword) {
        int userId = getCurrentSignedInUser();
        String ownerUsername = getUsernameById(userId); 

        if(userId == -1) {
            logger.warn("User not found: " + ownerUsername);
            return false;
        }

        String sql = "INSERT INTO user_accounts (user_id, service_name, login_username, login_password, password_hash) VALUES (?, ?, ?, ?, ?)";
        String passwordHash = HashUtils.SHA256(loginPassword);

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, serviceName);
            stmt.setString(3, loginUsername);
            try {
                stmt.setString(4, AESUtils.encrypt(loginPassword, getSecretKey()));
            } catch (Exception e) {
                logger.warn("Could not encrypt login password! " + e.getMessage());
                return false;
            }
            stmt.setString(5, passwordHash);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("Unable to add new service login for user: " + ownerUsername);
                return false;
            } 

            logger.info("Stored login added for user: " + ownerUsername + ", service: " + serviceName);
            return true;
        } catch (SQLException e) {
            logger.warn("Error adding stored login: " + e.getMessage());
            return false;
        }
    }


    public boolean removeStoredLogin(String sessionId, String serviceName, String serviceUsername, String servicePassword) {

        System.out.println("Trying to delete user with password: " + servicePassword);
        int userId = getUserIdForSessionId(sessionId);
        String ownerUsername = getUsernameById(userId);

        if(userId == -1) {
            logger.warn("User not found: " + ownerUsername);
            return false;
        }


        String sql = "DELETE FROM user_accounts WHERE user_id = ? AND service_name = ? AND login_username = ? AND password_hash = ?";
        String passwordHash = HashUtils.SHA256(servicePassword);

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, serviceName);
            stmt.setString(3, serviceUsername);
            stmt.setString(4, passwordHash);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                logger.warn("No stored login found to remove for " + ownerUsername);
                return false;
            } 
            logger.info("removed stored login for " + ownerUsername + ", service: " + serviceName);
            return true;
        } catch (SQLException e) {
            logger.warn("Error removing stored login: " + e.getMessage());
            return false;
        }



    }

    


    public List<Map<String, String>> getStoredLogins(String sessionId) {
        List<Map<String, String>> storedLogins = new ArrayList<>();

        
        
        int userId = getUserIdForSessionId(sessionId);
        if (userId == -1) {
            logger.warn("User not found: " + getUsernameById(userId));
            return storedLogins;
        }

        String sql = "SELECT service_name, login_username, login_password FROM user_accounts WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> loginEntry = new HashMap<>();
                    loginEntry.put("serviceName",   rs.getString("service_name"));
                    loginEntry.put("serviceUsername", rs.getString("login_username"));
                    try {
                        loginEntry.put("servicePassword", AESUtils.decrypt(rs.getString("login_password"), getSecretKey())); // plaintext for now
                    } catch (Exception e) {
                        logger.warn("Could not decrypt login password! " + e.getMessage());
                    }
                    storedLogins.add(loginEntry);
                }
            }
        } catch (SQLException e) {
            logger.warn("Error retrieving stored logins: " + e.getMessage());
            return storedLogins;
        }

        return storedLogins;
    }








    public boolean removeUser(String username) {
        if ("admin".equals(username)) {
            logger.warn("Cannot delete admin user.");
            return false;
        }

        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User " + username + " has been removed.");
                return true;
            } else {
                logger.warn("No user found with username: " + username);
            }
        } catch (SQLException e) {
            logger.warn("Error: " + e.getMessage());
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
            logger.warn("Error: " + e.getMessage());
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
        logger.warn("Error: " + e.getMessage());
    }
    return false;
}





    



    public boolean testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return conn != null;
        } catch (SQLException e) {
            logger.warn("Failed to connect to the database: " + e.getMessage());
            return false;
        }
    }
}
