package restserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import restserver.dto.ServiceLoginDTO;
import restserver.dto.UserDTO;
import restserver.db.UserDAO;
import restserver.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserDAO SQLservice;

    // 0 -> user registered succesfully
    // 1 -> username already taken, unsuccessful
    // -1 -> error
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();
        if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int dbResponse = SQLservice.registerUser(userDTO.getUsername(), userDTO.getPassword());
        if(dbResponse == 1) {
            response.put("status", "OCCUPIED");     
        } else if (dbResponse == 0) {
            response.put("status", "OK");
        } else {
            response.put("status", "ERROR");
        }

       
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/removeUser")
    public ResponseEntity<Map<String, String>> removeUser(@RequestBody Map<String, String> payload) {
        Map<String, String> response = new HashMap<>();

        if(!payload.get("password").equals(System.getenv("ADMIN_DASHBOARD_PASSWORD"))) {
            System.out.println("Unauthorized admin key!");
            response.put("status", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if (payload.get("username").trim().isEmpty()) {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (SQLservice.removeUser(payload.get("username"))) {
            response.put("status", "OK");
            return ResponseEntity.ok(response);
        }
        response.put("status", "Failed to remove user");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/currentUser")
    public ResponseEntity<?> currentUser(@CookieValue(value = "sessionId", defaultValue = "") String sessionId) {

        if (sessionId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = SQLservice.getUserIdForSessionId(sessionId);
        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = SQLservice.getUsernameById(userId);
        return ResponseEntity.ok(Map.of("username", username));
    }

    @GetMapping("/hasActiveSession")
    public ResponseEntity<Map<String, String>> hasActiveSession(@CookieValue(value = "sessionId", required = false) String sessionId) {
        Map<String, String> response = new HashMap<>();

        if (sessionId == null || sessionId.isEmpty()) {
            response.put("status", "NO_SESSION");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if(SQLservice.isValidSession(sessionId)) {
            response.put("status", "OK");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "INVALID_SESSION");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        

    }



    @PostMapping("/isExisting")
    public ResponseEntity<Map<String, String>> isExistingUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();
        if (SQLservice.isExistingUser(userDTO.getUsername())) {
            response.put("status", "User " + userDTO.getUsername() + " already exists");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "User " + userDTO.getUsername() + " does NOT exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/addNewLogin")
    public ResponseEntity<Map<String, String>> addNewLogin(@RequestBody ServiceLoginDTO serviceLoginDTO) {
        Map<String, String> response = new HashMap<>();
        if (SQLservice.addStoredLogin(serviceLoginDTO.getServiceName(), serviceLoginDTO.getServiceUsername(), serviceLoginDTO.getServicePassword())) {
            response.put("status", "OK");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/removeLogin")
    public ResponseEntity<Map<String, String>> removeLogin(@CookieValue(value = "sessionId", required = false) String sessionId, @RequestBody ServiceLoginDTO serviceLoginDTO) {
        System.out.println("received dto: " + serviceLoginDTO.getServiceName() + " " + serviceLoginDTO.getServiceUsername() + " " + serviceLoginDTO.getServicePassword());
        Map<String, String> response = new HashMap<>();
        if (SQLservice.removeStoredLogin(sessionId, serviceLoginDTO.getServiceName(), serviceLoginDTO.getServiceUsername(), serviceLoginDTO.getServicePassword())) {
            response.put("status", "OK");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }



    } 

    @PostMapping("/getAllLogins")
    public List<Map<String, String>> getAllLogins(@CookieValue(value = "sessionId", required = false) String sessionId) {
        return SQLservice.getStoredLogins(sessionId);
    }


    @PostMapping("/getAllUsers")
    public List<Map<String, String>> getAllUsers(@RequestBody Map<String, String> payload) {
        System.out.println("RUN!");
        return SQLservice.getAllUsers(payload.get("key"));
    }


    @PostMapping("/getLoginsForUsername")
    public List<Map<String, String>> getLoginsForUsername(@RequestBody Map<String,String> payload) {
        String username = payload.get("username");
        return SQLservice.getLoginsForUsername(username);
    }

    

   






}
