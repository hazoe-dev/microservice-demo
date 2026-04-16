package dev.hazoe.user_service.controller;

import dev.hazoe.user_service.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Map<Long, UserResponse> USERS = Map.of(
        1L, new UserResponse(1L, "Ha Tran", "ha@gmail.com"),
        2L, new UserResponse(2L, "Nguyen Van A", "nva@gmail.com"),
        3L, new UserResponse(3L, "Tran Thi B", "ttb@gmail.com")
    );

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = USERS.get(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
