package test.backend.roles.RolePackage;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import test.backend.roles.UserPackage.User;

import java.util.List;
import java.util.Optional;

@Entity
@Data
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    private RoleRepository roleRepository;

    @PostConstruct
    public void initializeRoles() {
        createRoleIfNotExists("Developer");
        createRoleIfNotExists("Product Owner");
        createRoleIfNotExists("Tester");
    }

    private void createRoleIfNotExists(String roleName) {
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        if (existingRole.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
