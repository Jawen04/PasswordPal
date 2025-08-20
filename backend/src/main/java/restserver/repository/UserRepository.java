package restserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restserver.entity.User;


@Repository  
public interface UserRepository extends JpaRepository<User, Integer> {
}
