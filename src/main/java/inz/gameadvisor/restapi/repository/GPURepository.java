package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.GPU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GPURepository extends JpaRepository<GPU,Long> {
}
