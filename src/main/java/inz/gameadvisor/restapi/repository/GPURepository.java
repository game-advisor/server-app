package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.GPU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GPURepository extends JpaRepository<GPU,Long> {
    List<GPU> findAllBySeries(String series);
}
