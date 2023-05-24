package test.backend.roles.RolePackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @PostMapping
    public ResponseEntity<?> createRole(@RequestParam String name) {
        try {
            roleService.createRole(name);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create role");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    @PostMapping("/{roleId}/users/{userId}")
    public ResponseEntity<Role> assignRoleToUser(@PathVariable String roleId, @PathVariable String userId) {
        try {
            Role role = roleService.assignRoleToUser(roleId, userId);
            return ResponseEntity.ok(role);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{roleId}/memberships")
    public ResponseEntity<List<Map<String, Object>>> getMembershipsForRole(@PathVariable String roleId) {
        try {
            List<Map<String, Object>> memberships = roleService.getMembershipsForRole(roleId);
            return ResponseEntity.ok(memberships);
        } catch (IllegalArgumentException | NoSuchElementException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{roleId}/memberships/{teamId}/{userId}")
    public ResponseEntity<Role> getRoleForMembership(@PathVariable String roleId, @PathVariable String teamId, @PathVariable String userId) {
        try {
            Role role = roleService.getRoleForMembership(userId, teamId);
            return ResponseEntity.ok(role);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
