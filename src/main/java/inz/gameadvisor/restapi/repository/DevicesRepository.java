package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevicesRepository extends JpaRepository<Devices, Long> {
    List<Devices> findDevicesByuserID(long userID);
}
