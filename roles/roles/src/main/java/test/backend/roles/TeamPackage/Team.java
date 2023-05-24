package test.backend.roles.TeamPackage;

import jakarta.persistence.*;
import lombok.Data;
import test.backend.roles.UserPackage.User;

import java.util.List;

@Entity
@Data
public class Team {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    @OneToOne
    private User teamLead;

    @ManyToMany
    @JoinTable(
            name = "team_user",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamMembers;
}