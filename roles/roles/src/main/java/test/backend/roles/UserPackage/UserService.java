package test.backend.roles.UserPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private static final String USERS_ENDPOINT = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/users";
    private static final String USER_ENDPOINT_TEMPLATE = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/users/%s";

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        try {
            ResponseEntity<List<User>> response = restTemplate.exchange(
                    USERS_ENDPOINT,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<User>>() {
                    }
            );

            int statusCodeValue = response.getStatusCode().value();
            HttpStatus statusCode = HttpStatus.valueOf(statusCodeValue);
            if (statusCode == HttpStatus.OK) {
                List<User> users = response.getBody();


                if (users == null || users.isEmpty()) {
                    throw new RuntimeException("No users found in the response.");
                }

                userRepository.saveAll(users);
                return users;
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Users not found.");
            } else {
                throw new RuntimeException("Failed to retrieve users. Status code: " + statusCodeValue);
            }
        } catch (RestClientException e) {

            throw new RuntimeException("Failed to retrieve users due to network error: " + e.getMessage());
        }
    }

    public User getUserById(String id) {
        String userEndpoint = String.format(USER_ENDPOINT_TEMPLATE, id);

        try {
            ResponseEntity<User> response = restTemplate.getForEntity(userEndpoint, User.class);

            int statusCodeValue = response.getStatusCode().value();
            HttpStatus statusCode = HttpStatus.valueOf(statusCodeValue);
            if (statusCode == HttpStatus.OK) {
                User user = response.getBody();

                if (user == null) {
                    throw new RuntimeException("Empty response received for user with id: " + id);
                }

                userRepository.save(user);
                return user;
            } else if (statusCode == HttpStatus.NOT_FOUND) {
                throw new NoSuchElementException("User not found with id: " + id);
            } else {
                throw new RuntimeException("Failed to retrieve user with id: " + id);
            }
        } catch (RestClientException e) {

            throw new RuntimeException("Failed to retrieve user with id: " + id + " due to network error: " + e.getMessage());
        }
    }
}