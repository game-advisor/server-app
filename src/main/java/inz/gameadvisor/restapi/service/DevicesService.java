package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.*;
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

    //private final CustomFunctions customFunctions = new CustomFunctions();
    String path;
    String message;

    @PersistenceContext
    EntityManager em;

    public List<Devices> getDevicesByCurrentUserID(String token){

        long userID = getUserIDFromToken(token);

        User user = new User();

        user.setUserID(userID);

        List<Devices> result = devicesRepository.findDevicesByUser(user);

        if(result.isEmpty()){
            throw new CustomRepsonses.MyNotFoundException("Not found");
        }
        else{
            return result;
        }
    }

    public ResponseEntity<Object> addDevice(UpdatedDevices device, String token){

        long userID = getUserIDFromToken(token);

        Devices createdDevice = new Devices();

        CPU cpu = cpuRepository.findById(device.getCpuID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("CPU of id: " + device.getCpuID() + " not found"));
        GPU gpu = gpuRepository.findById(device.getGpuID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("GPU of id: " + device.getGpuID() + " not found"));
        RAM ram = ramRepository.findById(device.getRamID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("RAM of id: " + device.getRamID() + " not found"));
        OS os = osRepository.findById(device.getOsID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("OS of id: " + device.getOsID() + " not found"));
        User user = userRepository.findById(userID).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("User of id: " + userID + " not found"));

        createdDevice.setShortName(device.getShortName());
        createdDevice.setCpu(cpu);
        createdDevice.setGpu(gpu);
        createdDevice.setRam(ram);
        createdDevice.setUser(user);
        createdDevice.setOs(os);
        createdDevice.setSSD(device.isSSD());
        createdDevice.setHDD(device.isHDD());

        try{
            devicesRepository.save(createdDevice);
        }
        catch (DataIntegrityViolationException e){
            return responseFromServer(HttpStatus.CONFLICT,path,message);
        }
        return null;
    }

    @SneakyThrows
    public ResponseEntity<Object> deleteDevice(long id, String token){
        long userID = getUserIDFromToken(token);
        path = "/api/device/" + id+ "/delete";

        Optional<Devices> device = devicesRepository.findById(id);

        if(device.isEmpty()){
            message = "No such device was found";
            return responseFromServer(HttpStatus.NOT_FOUND,path,message);
        }

        User user = device.get().getUser();

        if(userID == user.getUserID()){
            devicesRepository.deleteById(id);
            message = "Device deleted";
            return responseFromServer(HttpStatus.OK,path,message);
        }
        else if(isUserAnAdmin(userID)){
            devicesRepository.deleteById(id);
            message = "Device deleted";
            return responseFromServer(HttpStatus.OK,path,message);
        }
        else{
            message = "You don't have access to that";
            return responseFromServer(HttpStatus.FORBIDDEN,path,message);
        }
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<Object> editDevice(UpdatedDevices updatedDevices, long id, String token){
        long userID = getUserIDFromToken(token);
        path = "/api/device/" + id + "/edit";

        if(Objects.isNull(updatedDevices.getShortName()) || updatedDevices.getShortName().isBlank()){
            message = "Bad request";
            return responseFromServer(HttpStatus.BAD_REQUEST,path,message);
        }

        Optional<Devices> device = devicesRepository.findById(id);
        if(device.isEmpty()){
            message = "No such device found";
            return responseFromServer(HttpStatus.NOT_FOUND,path,message);
        }

        User user = device.get().getUser();

        String shortName = updatedDevices.getShortName();
        long cpuID = updatedDevices.getCpuID();
        Optional<CPU> cpu = cpuRepository.findById(cpuID);
        if(cpu.isEmpty()){
            message = "No such CPU was found";
            return responseFromServer(HttpStatus.NOT_FOUND,path,message);
        }

        long gpuID = updatedDevices.getGpuID();
        long ramID = updatedDevices.getRamID();
        long osID = updatedDevices.getOsID();
        boolean isHDD = updatedDevices.isHDD();
        boolean isSSD = updatedDevices.isSSD();

        if(userID == user.getUserID()){
            if(!shortName.isBlank()){
                if(!checkIfSameRecordExists("shortName","devices", shortName))
                {
                    if(updateField("devices","shortName",shortName,"deviceID",String.valueOf(id)) == 0){
                        message = "Updating was not successful";
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,path,message);
                    }
                }
                else{
                    message = "One of your devices with such name exists";
                    return responseFromServer(HttpStatus.CONFLICT,path,message);
                }
            }
            if(updateField("devices","cpuID",String.valueOf(cpuID),"deviceID",String.valueOf(id)) == 0){
                message = "Updating was not successful";
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,path,message);
            }
            message = "Device has been modified";
            return responseFromServer(HttpStatus.OK,path,message);
        }
        else if(isUserAnAdmin(userID)){
            if(!shortName.isBlank()){
                Query query = em.createNativeQuery("UPDATE devices SET shortName = ? WHERE deviceID = ?")
                        .setParameter(1, shortName)
                        .setParameter(2, id);
                query.executeUpdate();
                message = "Device has been modified";
                return responseFromServer(HttpStatus.OK,path,message);
            }
        }
        else{
            message = "You don't have access to that resource";
            return responseFromServer(HttpStatus.FORBIDDEN,path,message);
        }
        message = "Method has not been applied";
        return responseFromServer(HttpStatus.NOT_ACCEPTABLE,path,message);
    }
}
