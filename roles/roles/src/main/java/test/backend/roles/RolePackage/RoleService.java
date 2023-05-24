package test.backend.roles.RolePackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.backend.roles.TeamPackage.Team;
import test.backend.roles.TeamPackage.TeamRepository;
import test.backend.roles.UserPackage.User;
import test.backend.roles.UserPackage.UserRepository;

import java.util.*;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository, TeamRepository teamRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public void createRole(String name) {
        try {
            validateRoleName(name);
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to create role: " + e.getMessage());
        }
    }

    private void validateRoleName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty.");
        }

        if (!name.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Role name must contain only alphanumeric characters.");
        }

        if (roleRepository.existsByName(name)) {
            throw new IllegalArgumentException("Role with name '" + name + "' already exists.");
        }
    }

    public Role assignRoleToUser(String roleId, String userId) {
        if (roleId == null || roleId.isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be null or empty.");
        }

        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + roleId));

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        user.setRole(role);
        userRepository.save(user);

        return role;
    }

    public Role getRoleForMembership(String userId, String teamId) {
        if (teamId == null || teamId.isEmpty()) {
            throw new IllegalArgumentException("Team ID cannot be null or empty.");
        }

        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found with ID: " + teamId));

        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }

        List<User> teamMembers = team.getTeamMembers();
        for (User user : teamMembers) {
            if (user.getId().equals(userId)) {
                Role role = user.getRole();
                if (role == null) {
                    throw new NoSuchElementException("Role not assigned for User ID: " + userId);
                }
                return role;
            }
        }
        throw new NoSuchElementException("Membership not found for User ID: " + userId + " and Team ID: " + teamId);
    }

    public List<Map<String, Object>> getMembershipsForRole(String roleId) {

        if (roleId == null || roleId.isEmpty()) {
            throw new IllegalArgumentException("Role ID cannot be null or empty.");
        }

        Role role = roleRepository.findByRoleId(roleId)
                .orElseThrow(() -> new NoSuchElementException("Role not found with ID: " + roleId));

        List<User> users = role.getUsers();

        List<Map<String, Object>> memberships = new ArrayList<>();
        for (User user : users) {
            List<Team> teams = user.getTeams();
            for (Team team : teams) {

                String userId = user.getId();
                String teamId = team.getId();
                if (userId == null || userId.isEmpty() || teamId == null || teamId.isEmpty()) {
                    throw new IllegalStateException("Invalid user or team ID encountered for user: " + user.getId());
                }

                Map<String, Object> membership = new HashMap<>();
                membership.put("userId", userId);
                membership.put("userName", user.getName());
                membership.put("teamId", teamId);
                membership.put("teamName", team.getName());
                memberships.add(membership);
            }
        }

        return memberships;
    }
}
