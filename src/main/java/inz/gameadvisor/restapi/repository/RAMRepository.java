package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.RAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RAMRepository extends JpaRepository<RAM, Long> {
}
