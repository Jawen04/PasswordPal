package restserver.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.UUID;

import javax.crypto.SecretKey;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import restserver.config.AESKeyProvider;
import restserver.entity.User;
import restserver.util.AESUtils;

@Repository
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
                    return rs.getBoolean(1); 
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
                    return rs.getString("session_id");
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
                    return passwordEncoder.matches(enteredPassword, rs.getString("password"));
                } else {
                    return false;
                }
            }

        } catch (SQLException e ) {
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

            
            if(pstmt.executeUpdate() > 0) {
                logger.info("User: " + username + " registered successfully.");
                return 0;
            } else {
                logger.info("Could not register user");
                return -1;
            }
            
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

        String sql = "INSERT INTO user_accounts (user_id, service_name, login_username, login_password) VALUES (?, ?, ?, ?)";

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
    int userId = getUserIdForSessionId(sessionId);
    String ownerUsername = getUsernameById(userId);

    if (userId == -1) {
        logger.warn("User not found: " + ownerUsername);
        return false;
    }

    String selectSql = "SELECT id, login_password FROM user_accounts WHERE user_id = ? AND service_name = ? AND login_username = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

        selectStmt.setInt(1, userId);
        selectStmt.setString(2, serviceName);
        selectStmt.setString(3, serviceUsername);

        try (ResultSet rs = selectStmt.executeQuery()) {
            while (rs.next()) {
                int accountId = rs.getInt("id");
                String storedEncrypted = rs.getString("login_password");

                String decryptedPassword;
                try {
                    decryptedPassword = AESUtils.decrypt(storedEncrypted, getSecretKey());
                } catch (Exception e) {
                    logger.warn("Error decrypting password for deletion: " + e.getMessage());
                    continue;
                }

                if (decryptedPassword.equals(servicePassword)) {
                    String deleteSql = "DELETE FROM user_accounts WHERE id = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, accountId);
                        int rowsAffected = deleteStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            logger.info("Removed stored login for " + ownerUsername + ", service: " + serviceName);
                            return true;
                        }
                    }
                }
            }
        }

        logger.warn("No stored login found to remove for " + ownerUsername);
        return false;

    } catch (SQLException e) {
        logger.warn("SQL Error removing stored login: " + e.getMessage());
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

        int userId = getUserIdByUsername(username);
        if (userId == -1) {
            logger.warn("No user found with username: " + username);
            return false;
        }

        String deleteLogins = "DELETE FROM user_accounts WHERE user_id = ?";
        String deleteSessions = "DELETE FROM user_sessions WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmtLogins = conn.prepareStatement(deleteLogins);
            PreparedStatement stmtSessions = conn.prepareStatement(deleteSessions);
            PreparedStatement stmtUser = conn.prepareStatement(deleteUser)) {

            stmtLogins.setInt(1, userId);
            stmtLogins.executeUpdate();

            stmtSessions.setInt(1, userId);
            stmtSessions.executeUpdate();

            stmtUser.setInt(1, userId);
            int rowsAffected = stmtUser.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("User " + username + " has been fully removed.");
                return true;
            } else {
                logger.warn("Failed to delete user " + username);
            }

        } catch (SQLException e) {
            logger.warn("Error removing user: " + e.getMessage());
        }

        return false;
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


    public List<Map<String, String>> getAllUsers(String key) {
        List<Map<String, String>> userList = new ArrayList<>();

        if(!key.equals(System.getenv("ADMIN_DASHBOARD_PASSWORD"))) {
            System.out.println(System.getenv("ADMIN_DASHBOARD_PASSWORD"));
            logger.warn("Authentication failed!");
            return userList;
        }

        String sql = "SELECT * FROM users";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, String> user = new HashMap<>();
                user.put("username", rs.getString("username"));
                user.put("password", rs.getString("password"));
                userList.add(user);
            }
        } catch (SQLException e) {
            logger.warn("Error: " + e.getMessage());
        }

        return userList;
    }




    public List<Map<String, String>> getLoginsForUsername(String username) {
        List<Map<String, String>> logins = new ArrayList<Map<String,String>>();
        int userId = getUserIdByUsername(username);
        if(userId == -1) {
            logger.warn("Could not find username: " + username);
            return logins;
        }


        String sql = "SELECT service_name, login_username, login_password FROM user_accounts WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try(ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, String> loginEntry = new HashMap<>();
                        loginEntry.put("serviceName",   rs.getString("service_name"));
                        loginEntry.put("serviceUsername", rs.getString("login_username"));
                        try {
                            loginEntry.put("servicePassword", AESUtils.decrypt(rs.getString("login_password"), getSecretKey())); // plaintext for now
                        } catch (Exception e) {
                            logger.warn("Could not decrypt login password! " + e.getMessage());
                        }
                        logins.add(loginEntry);
                    }
                }
        } catch (SQLException e) {
            logger.warn("Error: " + e.getMessage());
        }

        return logins;
    }



    


    public boolean changeServiceLogin(String sessionId, String oldServiceName, String oldUsername, String oldServicePassword, String newServiceName, String newUsername, String newServicePassword) {
    int userId = getUserIdForSessionId(sessionId);
    if (userId == -1) {
        logger.warn("Could not find user for session: " + sessionId);
        return false;
    }

    String selectSql = "SELECT id, login_password FROM user_accounts WHERE user_id = ? AND service_name = ? AND login_username = ?";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

        selectStmt.setInt(1, userId);
        selectStmt.setString(2, oldServiceName);
        selectStmt.setString(3, oldUsername);

        try (ResultSet rs = selectStmt.executeQuery()) {
            while (rs.next()) {
                int accountId = rs.getInt("id");
                String storedEncrypted = rs.getString("login_password");

                String decryptedPassword;
                try {
                    decryptedPassword = AESUtils.decrypt(storedEncrypted, getSecretKey());
                } catch (Exception e) {
                    logger.warn("Error decrypting old service password: " + e.getMessage());
                    continue; 
                }

                if (decryptedPassword.equals(oldServicePassword)) {
                    String updateSql = "UPDATE user_accounts SET service_name = ?, login_username = ?, login_password = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, newServiceName);
                        updateStmt.setString(2, newUsername);
                        updateStmt.setString(3, AESUtils.encrypt(newServicePassword, getSecretKey()));
                        updateStmt.setInt(4, accountId);

                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            logger.info("Updated service login for user_id " + userId + ", service: " + oldServiceName);
                            return true;
                        }
                    } catch (Exception e) {
                        logger.warn("Error encrypting new service password: " + e.getMessage());
                        return false;
                    }
                }
            }
        }

        logger.warn("No matching service login found for user_id " + userId);
        return false;

    } catch (SQLException e) {
        logger.warn("SQL Error during service login update: " + e.getMessage());
        return false;
    }
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
