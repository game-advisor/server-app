package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.OS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OSRepository extends JpaRepository<OS,Long> {
    List<OS> findAllByCompany(Companies companies);
}
