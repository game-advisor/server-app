package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Companies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompaniesRepository extends JpaRepository<Companies,Long> {
    Optional<Companies> findByName(String name);
}
