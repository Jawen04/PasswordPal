package restserver.controller;

import restserver.db.UserDAO;
import restserver.dto.UserDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@CookieValue(value = "sessionId", defaultValue = "") String sessionId, @RequestBody UserDTO userDTO, HttpServletResponse httpResponse) { 
        Map<String, String> response = new HashMap<>();

        logger.info("Trying to login user: " + userDTO.getUsername());
        // Check if username/password are empty
        if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            response.put("status", "NOT_OK");
            response.put("message", "Username or password cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        boolean valid = SQLservice.validateUser(userDTO.getUsername(), userDTO.getPassword());

        if (valid) {


            // Check if the user already has an active session
            // TODO: issue a new sessionId even if the user has one already, for security 
            String possibleActiveSession = SQLservice.getActiveSessionId(SQLservice.getUserIdByUsername(userDTO.getUsername()));
            String newSessionId = "";
            if(possibleActiveSession == null) {
                newSessionId = SQLservice.createNewUserSession(userDTO.getUsername());
            } else {
                // Check if the browser has an active session, if it has and does not belong to the user wanting to log in - terminate
                if(SQLservice.checkActiveSessionForSessionId(sessionId) && !possibleActiveSession.equals(sessionId)) {
                    SQLservice.terminateSession(sessionId);
                }
                newSessionId = possibleActiveSession;
            }
           
            


            Cookie cookie = new Cookie("sessionId", newSessionId);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // set to false for local dev
            cookie.setPath("/"); // valid for entire app
            cookie.setMaxAge(60 * 10); // 10 min
            httpResponse.addCookie(cookie); 
            

            return ResponseEntity.ok(Map.of("status", "OK"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "FAIL"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "sessionId", defaultValue = "") String sessionId, HttpServletResponse response) {

        logger.info("Login out user: " + SQLservice.getUserIdForSessionId(sessionId) + " with sessionId: " + sessionId);
        // Expire the cookie
        Cookie cookie = new Cookie("sessionId", "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // expires instantly
        response.addCookie(cookie);

        // Terminate session server-side
        if (!sessionId.isEmpty()) {
            SQLservice.terminateSession(sessionId);
            return ResponseEntity.ok(Map.of("status", "OK"));
        }

        return ResponseEntity.ok(Map.of("status", "NOT_OK"));




        
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
            logger.info("Testing database connection...");
            SQLservice.testConnection(); 
            logger.info("Database connection successful!");
            response.put("status", "SUCCESS");
        } catch (Exception e) {
            logger.warn("Database connection failed:");
            response.put("status", "ERROR");
            e.printStackTrace();
        }

        return response;
    }


}
