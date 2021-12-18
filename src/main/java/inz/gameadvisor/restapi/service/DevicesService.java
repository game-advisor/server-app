package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DevicesService extends CustomFunctions {

    private final DevicesRepository devicesRepository;
    private final CPURepository cpuRepository;
    private final GPURepository gpuRepository;
    private final RAMRepository ramRepository;
    private final OSRepository osRepository;
    private final UserRepository userRepository;
    String message;

    @PersistenceContext
    EntityManager em;

    public ResponseEntity<Object> getDevicesByCurrentUserID(String token, HttpServletRequest request){

        long userID = getUserIDFromToken(token);

        User user = new User();

        user.setUserID(userID);

        List<Devices> result = devicesRepository.findDevicesByUser(user);

        if(result.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
        }
        else{
            return new ResponseEntity<>(result,HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> addDevice(UpdatedDevices device, HttpServletRequest request, String token){

        long userID = getUserIDFromToken(token);

        Devices createdDevice = new Devices();

        Optional<CPU> cpu = cpuRepository.findById(device.getCpuID());
        Optional<GPU> gpu = gpuRepository.findById(device.getGpuID());
        Optional<RAM> ram = ramRepository.findById(device.getRamID());
        Optional<OS> os = osRepository.findById(device.getOsID());
        Optional<User> user = userRepository.findById(userID);

        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No CPU of such id found");
        }
        else if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No GPU of such id found");
        }
        else if(ram.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No RAM of such id found");
        }
        else if(os.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No OS of such id found");
        }
        else if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No such user was found");
        }

        createdDevice.setShortName(device.getShortName());

        if(checkIfSameRecordExists("shortName","devices",device.getShortName())){
            return responseFromServer(HttpStatus.CONFLICT, request, "One of your device already has that name");
        }
        createdDevice.setCpu(cpu.get());
        createdDevice.setGpu(gpu.get());
        createdDevice.setRam(ram.get());
        createdDevice.setUser(user.get());
        createdDevice.setOs(os.get());
        createdDevice.setSSD(device.isSSD());
        createdDevice.setHDD(device.isHDD());

        try{
            devicesRepository.save(createdDevice);
        }
        catch (DataIntegrityViolationException e){
            message = "Devices hasn't been added";
            return responseFromServer(HttpStatus.CONFLICT,request,message);
        }
        message = "Device has been added";
        return responseFromServer(HttpStatus.OK,request,message);
    }

    @SneakyThrows
    public ResponseEntity<Object> deleteDevice(long id, HttpServletRequest request, String token){
        long userID = getUserIDFromToken(token);

        Optional<Devices> device = devicesRepository.findById(id);

        if(device.isEmpty()){
            message = "No such device was found";
            return responseFromServer(HttpStatus.NOT_FOUND,request,message);
        }

        User user = device.get().getUser();

        if(userID == user.getUserID()){
            devicesRepository.deleteById(id);
            message = "Device deleted";
            return responseFromServer(HttpStatus.OK,request,message);
        }
        else if(isUserAnAdmin(userID)){
            devicesRepository.deleteById(id);
            message = "Device deleted";
            return responseFromServer(HttpStatus.OK,request,message);
        }
        else{
            message = "You don't have access to that";
            return responseFromServer(HttpStatus.FORBIDDEN,request,message);
        }
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<Object> editDevice(UpdatedDevices updatedDevices, HttpServletRequest request, long id, String token){
        long userID = getUserIDFromToken(token);

        if(Objects.isNull(updatedDevices.getShortName()) || updatedDevices.getShortName().isBlank()){
            message = "Bad request";
            return responseFromServer(HttpStatus.BAD_REQUEST,request,message);
        }

        Optional<Devices> device = devicesRepository.findById(id);
        if(device.isEmpty()){
            message = "No such device found";
            return responseFromServer(HttpStatus.NOT_FOUND,request,message);
        }

        User user = device.get().getUser();

        String shortName = updatedDevices.getShortName();
        long cpuID = updatedDevices.getCpuID();
        Optional<CPU> cpu = cpuRepository.findById(cpuID);
        if(cpu.isEmpty()){
            message = "No such CPU was found";
            return responseFromServer(HttpStatus.NOT_FOUND,request,message);
        }

        long gpuID = updatedDevices.getGpuID();
        long ramID = updatedDevices.getRamID();
        long osID = updatedDevices.getOsID();
        boolean isHDD = updatedDevices.isHDD();
        boolean isSSD = updatedDevices.isSSD();

        if(isUserAnAdmin(userID)){
            if(!shortName.isBlank()){
                Query query = em.createNativeQuery("UPDATE devices SET shortName = ? WHERE deviceID = ?")
                        .setParameter(1, shortName)
                        .setParameter(2, id);
                query.executeUpdate();
                message = "Device has been modified";
                return responseFromServer(HttpStatus.OK,request,message);
            }
        }
        else if(userID == user.getUserID()){
            if(!shortName.isBlank()){
                if(!checkIfSameRecordExists("shortName","devices", shortName))
                {
                    if(updateField("devices","shortName",shortName,"deviceID",String.valueOf(id)) == 0){
                        message = "Updating was not successful";
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,message);
                    }
                }
                else{
                    message = "One of your devices with such name exists";
                    return responseFromServer(HttpStatus.CONFLICT,request,message);
                }
            }
            if(updateField("devices","cpuID",String.valueOf(cpuID),"deviceID",String.valueOf(id)) == 0){
                message = "Updating was not successful";
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,message);
            }
            message = "Device has been modified";
            return responseFromServer(HttpStatus.OK,request,message);
        }
        else{
            message = "You don't have access to that resource";
            return responseFromServer(HttpStatus.FORBIDDEN,request,message);
        }
        message = "Method has not been applied";
        return responseFromServer(HttpStatus.NOT_ACCEPTABLE,request,message);
    }
}
