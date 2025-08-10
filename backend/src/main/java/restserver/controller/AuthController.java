package restserver.controller;

import restserver.db.UserDAO;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

import restserver.service.UserService;
import restserver.entity.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    UserDAO SQLservice;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials, HttpSession session) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, String> response = new HashMap<>();

        int userId = SQLservice.getUserIdByUsername(username);
        User user = SQLservice.getUserByUsername(username);

        if (userId == -1) {
            response.put("status", "DENIED");
            return response;
        }
        if (password.equals(user.getPassword())) {
            String sessionId = SQLservice.createNewUserSession(userId);
            response.put("status", "GRANTED");
            response.put("sessionId", sessionId);
            System.out.println("User: " + username + " logged in successfully");
            System.out.println("New session created for user: " + username + "(id: " + sessionId + ")");
            return response;
        }
        response.put("status", "DENIED");
        System.out.println("ERROR: Could not log in user " + username);
        return response;
    }

    @GetMapping("/check")
    public Map<String, String> checkSession(HttpSession session) {
        Map<String, String> response = new HashMap<>();
        if (session.getAttribute("user") != null) {
            response.put("status", "LOGGED_IN");
        } else {
            response.put("status", "NOT_LOGGED_IN");
        }
        return response;
    }

     @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        session.invalidate(); // Destroy session
        Map<String, String> response = new HashMap<>();
        // Change login flag to false here
        response.put("status", "LOGGED_OUT");
        return response;
    } 

    @PostConstruct
    public void testConnection() {
        try {
            System.out.println("Testing database connection...");
            SQLservice.getAllUsers(); // Or any simple query
            System.out.println("Database connection successful!");
        } catch (Exception e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
    }


    @PostMapping("/testSQL")
    public Map<String, String> testSQLConnection() {
        Map<String, String> response = new HashMap<>();


        try {
            System.out.println("Testing database connection...");
            SQLservice.getAllUsers(); 
            System.out.println("Database connection successful!");
            response.put("status", "SUCCESS");
        } catch (Exception e) {
            System.err.println("Database connection failed:");
            response.put("status", "ERROR");
            e.printStackTrace();
        }

        return response;
    }


}
