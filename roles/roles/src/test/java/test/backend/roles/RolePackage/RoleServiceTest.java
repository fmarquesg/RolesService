package test.backend.roles.RolePackage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import test.backend.roles.TeamPackage.TeamRepository;
import test.backend.roles.UserPackage.UserRepository;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private RoleService roleService;

    @Captor
    private ArgumentCaptor<Role> roleCaptor;

    public void setUp() {
        roleService = new RoleService(roleRepository, userRepository, teamRepository);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createRole_ValidName_SuccessfullyCreated() {
        String roleName = "Developer";

        boolean result = roleService.createRole(roleName);

        assertTrue(result);
        verify(roleRepository).save(roleCaptor.capture());
        Role savedRole = roleCaptor.getValue();
        assertNotNull(savedRole);
        assertEquals(roleName, savedRole.getName());
    }


    @Test
    public void createRole_NullName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(null));
    }

    @Test
    public void createRole_EmptyName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(""));
    }


    @Test
    public void getRoleForMembership_NullTeamId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.getRoleForMembership("userId", null));
    }

    @Test
    public void getMembershipsForRole_NullRoleId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> roleService.getMembershipsForRole(null));
    }

    @Test
    public void getMembershipsForRole_RoleNotFound_ThrowsNoSuchElementException() {
        assertThrows(NoSuchElementException.class, () -> roleService.getMembershipsForRole("nonexistent"));
    }

}
