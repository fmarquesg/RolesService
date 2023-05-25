package test.backend.roles.UserPackage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String USERS_ENDPOINT = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/users";
    private static final String USER_ENDPOINT_TEMPLATE = "https://cgjresszgg.execute-api.eu-west-1.amazonaws.com/users/%s";

    @Mock
    private RestTemplate restTemplate = new RestTemplateBuilder().build();

    private final MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetAllUsers_Success() {
        List<User> expectedUsers = Arrays.asList(
                new User("1", "John"),
                new User("2", "Jane")
        );

        ResponseEntity<List<User>> responseEntity = new ResponseEntity<>(expectedUsers, HttpStatus.OK);

        when(restTemplate.exchange(eq(USERS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers.size(), actualUsers.size());
        assertTrue(actualUsers.containsAll(expectedUsers));
        verify(userRepository, times(1)).saveAll(expectedUsers);
    }

    @Test
    public void testGetAllUsers_NoUsersFound() {
        ResponseEntity<List<User>> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.exchange(eq(USERS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("No users found in the response.", exception.getMessage());
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllUsers_UsersNotFound() {
        ResponseEntity<List<User>> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.exchange(eq(USERS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("Users not found.", exception.getMessage());
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllUsers_FailedToRetrieveUsers() {
        ResponseEntity<List<User>> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(eq(USERS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("Failed to retrieve users. Status code: 500", exception.getMessage());
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetAllUsers_NetworkError() {
        when(restTemplate.exchange(eq(USERS_ENDPOINT), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("500 Server Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getAllUsers();
        });

        assertEquals("Failed to retrieve users due to network error: 500 Server Error", exception.getMessage());
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetUserById_Success() {
        String userId = "1";
        User expectedUser = new User();
        expectedUser.setId(userId);
        expectedUser.setName("John");

        String userEndpoint = String.format(USER_ENDPOINT_TEMPLATE, userId);
        ResponseEntity<User> responseEntity = new ResponseEntity<>(expectedUser, HttpStatus.OK);

        when(restTemplate.getForEntity(eq(userEndpoint), eq(User.class)))
                .thenReturn(responseEntity);

        User actualUser = userService.getUserById(userId);

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getName(), actualUser.getName());
        verify(userRepository, times(1)).save(expectedUser);
    }

    @Test
    public void testGetUserById_EmptyResponse() {
        String userId = "1";
        String userEndpoint = String.format(USER_ENDPOINT_TEMPLATE, userId);
        ResponseEntity<User> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.getForEntity(eq(userEndpoint), eq(User.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("Empty response received for user with id: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserById_UserNotFound() {
        String userId = "1";
        String userEndpoint = String.format(USER_ENDPOINT_TEMPLATE, userId);
        ResponseEntity<User> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        when(restTemplate.getForEntity(eq(userEndpoint), eq(User.class)))
                .thenReturn(responseEntity);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserById_FailedToRetrieveUser() {
        String userId = "1";
        String userEndpoint = String.format(USER_ENDPOINT_TEMPLATE, userId);
        ResponseEntity<User> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(eq(userEndpoint), eq(User.class)))
                .thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("Failed to retrieve user with id: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

}







