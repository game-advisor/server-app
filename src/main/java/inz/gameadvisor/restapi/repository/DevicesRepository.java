package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevicesRepository extends JpaRepository<Devices, Long> {
    List<Devices> findDevicesByuserID(long userID);
}
