package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.CPU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CPURepository extends JpaRepository<CPU,Long> {
    List<CPU> findAllBySeries(String series);
}
