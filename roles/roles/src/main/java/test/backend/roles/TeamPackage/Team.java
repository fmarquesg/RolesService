package test.backend.roles.TeamPackage;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.backend.roles.UserPackage.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "teams")
@Access(value=AccessType.FIELD)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    private User teamLead;

    @ManyToMany
    private Set<User> teamMembers = new HashSet<User>();

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Team(String name) {
        this.name = name;
    }
}