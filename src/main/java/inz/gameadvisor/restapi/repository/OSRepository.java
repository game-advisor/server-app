package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.OS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OSRepository extends JpaRepository<OS,Long> {
}
