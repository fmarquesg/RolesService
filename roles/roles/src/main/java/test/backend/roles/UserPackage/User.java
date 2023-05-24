package test.backend.roles.UserPackage;

import jakarta.persistence.*;
import lombok.Data;
import test.backend.roles.RolePackage.Role;
import test.backend.roles.TeamPackage.Team;

import java.util.List;

@Entity
@Data
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    @ManyToMany(mappedBy = "teamMembers")
    private List<Team> teams;

    @ManyToOne
    private Role role;

    public User() {
        this.role.setName("Developer");
    }
}