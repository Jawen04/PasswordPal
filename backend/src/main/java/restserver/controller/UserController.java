package restserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import restserver.service.UserService;
import restserver.dto.UserDTO;

import restserver.db.UserDAO;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired
  private UserService userService;
  @Autowired
  private UserDAO SQLservice;

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDTO userDTO) {
    Map<String, String> response = new HashMap<>();

    if(userDTO.getUsername().replaceAll("\\s+", "").equals("") || userDTO.getPassword().replaceAll("\\s+", "").equals("")) {
      System.out.println("Username and password cannot be empty");
      response.put("status", "NOT_OK");
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
    SQLservice.registerUser(userDTO.getUsername(), userDTO.getPassword());
    System.out.println("USER REGISTERED");
    response.put("status", "OK");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/remove")
  public ResponseEntity<Map<String, String>> removeUser(@RequestBody UserDTO userDTO) {
    Map<String, String> response = new HashMap<>();
    if(SQLservice.removeUser(userDTO.getUsername())) {
      response.put("status", "User removed successfully");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    response.put("status", "Failed to remove user");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @GetMapping("/getAll")
  public Map<String, String> getAllUsers() {
    return SQLservice.getAllUsers();
  }

  @PostMapping("/isExisting")
  public ResponseEntity<Map<String, String>> isExistingUser(@RequestBody UserDTO userDTO) {
    Map<String, String> response = new HashMap<>();
    if(SQLservice.isExistingUser(userDTO.getUsername())) {
      response.put("status", "User " + userDTO.getUsername() + " is already existing in the database");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } else {
      response.put("status", "User " + userDTO.getUsername() + " is NOT existing in the database");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
  }
}
