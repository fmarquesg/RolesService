package test.backend.roles.RolePackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(String id);

    boolean existsByName(String name);

    Optional<Role> findByName(String roleName);
}
