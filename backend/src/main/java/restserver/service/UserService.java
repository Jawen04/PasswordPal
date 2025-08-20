package restserver.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import restserver.entity.User;
import restserver.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
}
