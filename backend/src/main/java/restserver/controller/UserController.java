package restserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;
import restserver.dto.ServiceLoginDTO;
import restserver.dto.UserDTO;

import restserver.entity.User;
import restserver.db.UserDAO;
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

    // if(userDTO.getUsername().isBlank() || userDTO.getPassword().isBlank()) { ... }

    if(userDTO.getUsername().replaceAll("\\s+", "").equals("") || userDTO.getPassword().replaceAll("\\s+", "").equals("")) {
      System.out.println("Username and password cannot be empty");
      response.put("status", "NOT_OK");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    SQLservice.registerUser(userDTO.getUsername(), userDTO.getPassword());
    System.out.println("USER REGISTERED");
    response.put("status", "OK");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/remove")
  public ResponseEntity<Map<String, String>> removeUser(@RequestBody UserDTO userDTO) {
    Map<String, String> response = new HashMap<>();

    if(userDTO.getUsername().replaceAll("\\s+", "").equals("") || userDTO.getPassword().replaceAll("\\s+", "").equals("")) {
      System.out.println("Username and password cannot be empty");
      response.put("status", "NOT_OK");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    else if(SQLservice.removeUser(userDTO.getUsername())) {
      response.put("status", "User removed successfully");
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } 
    response.put("status", "Failed to remove user");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }


  /*
  @GetMapping("/getAll")
  public Map<String, String> getAllUsers() {
    Map<String, String> userMap = new HashMap<>();
    for(User user : SQLservice.getAllUsers()) {
      userMap.put(user.getUsername(), user.getPassword());
    }

    return userMap;
  }

  */
  

  @PostMapping("/getCurrentSignedInUser")
  public String getCurrentSignedInUser() {
    return SQLservice.getUsernameById(SQLservice.getCurrentSignedInUser());
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

  @PostMapping("/addNewLogin")
  public ResponseEntity<Map<String, String>> addNewLogin(@RequestBody ServiceLoginDTO serviceLoginDTO) {
    Map<String, String> response = new HashMap<>();

    if(SQLservice.addStoredLogin(serviceLoginDTO.getOwnerUsername(), serviceLoginDTO.getServiceName(), serviceLoginDTO.getServiceUsername(), serviceLoginDTO.getServicePassword())) {
      System.out.println("New service login added for user: " + serviceLoginDTO.getOwnerUsername() + ", service: " + serviceLoginDTO.getServiceName());
      response.put("status", "OK");
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } else {
      System.out.println("Failed to add new service login for user: " + serviceLoginDTO.getOwnerUsername() + ", service: " + serviceLoginDTO.getServiceName());
      response.put("status", "NOT_OK");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PostMapping("/getAllLogins")
  public List<Map<String,String>> getAllLogins(@RequestBody UserDTO userDTO) {
    System.out.println("Fetching all logins for user: " + userDTO.getUsername());  
    return SQLservice.getStoredLogins(userDTO.getUsername());
  }
}




  
