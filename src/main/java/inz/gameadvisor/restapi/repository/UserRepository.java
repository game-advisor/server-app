package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.userOriented.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
