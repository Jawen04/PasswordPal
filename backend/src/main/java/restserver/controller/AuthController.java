package restserver.controller;

import restserver.db.UserDAO;
import restserver.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO userDTO, HttpServletResponse httpResponse) { 
        Map<String, String> response = new HashMap<>();

        System.out.println("Trying to login user: " + userDTO.getUsername());
        // Check if username/password are empty
        if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            response.put("status", "NOT_OK");
            response.put("message", "Username or password cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean valid = SQLservice.validateUser(userDTO.getUsername(), userDTO.getPassword());

        if (valid) {
            String possibleActiveSession = SQLservice.getActiveSessionId(SQLservice.getUserIdByUsername(userDTO.getUsername()));
            String sessionId = possibleActiveSession != null ? possibleActiveSession : SQLservice.createNewUserSession(userDTO.getUsername());

            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // set to false for local dev
            cookie.setPath("/"); // valid for entire app
            cookie.setMaxAge(60 * 10); // 10 min
            httpResponse.addCookie(cookie); 
            
            System.out.println("Login valid! Creating new user session ...");

            return ResponseEntity.ok(Map.of("status", "OK"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "FAIL"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "sessionId", defaultValue = "") String sessionId, HttpServletResponse response) {
        System.out.println("From controller sessionID: " + sessionId);
        System.out.println("Login out user: " + SQLservice.getUserIdBySession(sessionId));
        // Expire the cookie
        Cookie cookie = new Cookie("sessionId", "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // expires instantly
        response.addCookie(cookie);

        // Terminate session server-side
        if (!sessionId.isEmpty()) {
            SQLservice.terminateSession(sessionId);
        }

        return ResponseEntity.ok(Map.of("status", "LOGGED_OUT"));
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
