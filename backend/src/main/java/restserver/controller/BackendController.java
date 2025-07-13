package restserver.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackendController {

    @GetMapping("/error")
    public String errorMsg() {
        return "Internal server error";
    }
}