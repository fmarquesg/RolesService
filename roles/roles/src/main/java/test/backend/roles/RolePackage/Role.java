package test.backend.roles.RolePackage;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import test.backend.roles.UserPackage.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Table(name = "roles")
@Access(value = AccessType.FIELD)
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany
    private List<User> users = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    public Role() {
        this("Developer");
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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

