package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Devices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevicesRepository extends JpaRepository<Devices, Long> {
}
