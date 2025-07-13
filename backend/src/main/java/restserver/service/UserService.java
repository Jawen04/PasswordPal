package restserver.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import restserver.entity.AdminUser;
import restserver.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    

    public String registerUser(String username, String password) {
        AdminUser adminUserEntity = new AdminUser(username, password);

        // List<User> users = userRepository.findAll();
        // for (User p : users) {
        //     if (p.getUsername().equals(username)) {
        //         return "ERROR";
        //     }
        // }
        userRepository.save(adminUserEntity);
        return "SUCCESS";

    }

    public Map<String, String> getAllUsers() {
        List<AdminUser> adminUserList = userRepository.findAll();
        Map<String, String> userMap = new HashMap<>();

        for (AdminUser p : adminUserList) {
            userMap.put(p.getUsername(), p.getPassword());
        }
        return userMap;
    }

    

}
