package test.backend.roles.UserPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                throw new RuntimeException("No users found in the response.");
            }

            return users;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Users not found.");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to retrieve users. " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable String id) {
        try {
            return userService.getUserById(id);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found with id: " + id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to retrieve user with id: " + id + ". " + e.getMessage());
        }
    }
}

