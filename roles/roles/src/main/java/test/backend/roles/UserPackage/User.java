package test.backend.roles.UserPackage;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.backend.roles.RolePackage.Role;
import test.backend.roles.TeamPackage.Team;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@Access(value=AccessType.FIELD)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    @ManyToMany
    private Set<Team> teams = new HashSet<Team>();

    @ManyToOne
    private Role role;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    public void setTeam(Team team) {
        this.teams.add(team);
    }
}