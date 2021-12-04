package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Long> {
}
