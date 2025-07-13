package restserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restserver.entity.AdminUser;


@Repository  // Ensure this is present
public interface UserRepository extends JpaRepository<AdminUser, Long> {
}
