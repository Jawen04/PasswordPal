package restserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.origin.SystemEnvironmentOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();
        if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        SQLservice.registerUser(userDTO.getUsername(), userDTO.getPassword());
        response.put("status", "OK");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, String>> removeUser(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();
        if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (SQLservice.removeUser(userDTO.getUsername())) {
            response.put("status", "User removed successfully");
            return ResponseEntity.ok(response);
        }
        response.put("status", "Failed to remove user");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/getCurrentSignedInUser")
    public ResponseEntity<Map<String, String>> getCurrentSignedInUser(@RequestHeader("Session-Id") String sessionId) {
        Map<String, String> response = new HashMap<>();

        int userId = SQLservice.getUserIdBySession(sessionId); // new method
        System.out.println("COOLBOIII:" + userId + "found");
        if(userId == -1) {
            response.put("status", "NOT_OK");
            response.put("message", "Invalid session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String username = SQLservice.getUsernameById(userId);
        response.put("status", "OK");
        response.put("username", username);
        return ResponseEntity.ok(response);
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
        if (SQLservice.addStoredLogin(serviceLoginDTO.getOwnerUsername(), serviceLoginDTO.getServiceName(),
                serviceLoginDTO.getServiceUsername(), serviceLoginDTO.getServicePassword())) {
            response.put("status", "OK");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", "NOT_OK");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/getAllLogins")
    public List<Map<String, String>> getAllLogins(@RequestBody UserDTO userDTO) {
        return SQLservice.getStoredLogins(userDTO.getUsername());
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO userDTO) {
      Map<String, String> response = new HashMap<>();

      // Check if username/password are empty
      if (userDTO.getUsername().trim().isEmpty() || userDTO.getPassword().trim().isEmpty()) {
          response.put("status", "NOT_OK");
          response.put("message", "Username or password cannot be empty");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      User user = SQLservice.getUserByUsername(userDTO.getUsername());
      if (user == null || !user.getPassword().equals(userDTO.getPassword())) {
          response.put("status", "NOT_OK");
          response.put("message", "Invalid username or password");
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }

      // Create a new session
      String sessionId = SQLservice.createNewUserSession(user.getId());
      if (sessionId == null) {
          response.put("status", "NOT_OK");
          response.put("message", "Failed to create session");
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      response.put("status", "OK");
      response.put("sessionId", sessionId);
    return ResponseEntity.ok(response);
  }

}
