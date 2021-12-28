package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.userOriented.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DevicesRepository extends JpaRepository<Devices, Long> {
    List<Devices> findDevicesByUser(User user);
    Page<Devices> findAllByUser(User user, Pageable pageable);

}
