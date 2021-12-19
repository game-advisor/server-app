package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    public ResponseEntity<Object> getDevicesByUserID(Integer pageNumber, Integer pageSize, String sortBy, long id, HttpServletRequest request){

        Pageable paging = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));
        Page<Devices> pagedResult = devicesRepository.findAll(paging);

        List<Devices> devicesList = new ArrayList<>();

        if(!pagedResult.hasContent())
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
        else{
            for (Devices devices : pagedResult) {
                User user = devices.getUser();
                if(user.getUserID() == id)
                    devicesList.add(devices);
            }
            if(devicesList.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
            }
            return new ResponseEntity<>(devicesList,HttpStatus.OK);
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
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCPUFoundMessage);
        }
        else if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoGPUFoundMessage);
        }
        else if(ram.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoRAMFoundMessage);
        }
        else if(os.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoOSFoundMessage);
        }
        else if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        createdDevice.setShortName(device.getShortName());

        if(checkIfSameRecordExists("shortName","devices",device.getShortName())){
            return responseFromServer(HttpStatus.CONFLICT, request, DeviceDuplicateNameMessage);
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
        catch (IllegalArgumentException e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,DeviceAddedMessage);
    }

    public ResponseEntity<Object> deleteDevice(long id, HttpServletRequest request, String token){
        long userID = getUserIDFromToken(token);

        Optional<Devices> device = devicesRepository.findById(id);

        if(device.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoDeviceFoundMessage);
        }

        User user = device.get().getUser();

        if(isUserAnAdmin(userID)){
            devicesRepository.deleteById(id);
            return responseFromServer(HttpStatus.OK,request,DeviceDeleteMessage);
        }
        else if(userID == user.getUserID()){
            devicesRepository.deleteById(id);
            return responseFromServer(HttpStatus.OK,request,DeviceDeleteMessage);
        }
        else{
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }
    }

    @Transactional
    public ResponseEntity<Object> editDevice(UpdatedDevices updatedDevices, HttpServletRequest request, long id, String token){
        long userID = getUserIDFromToken(token);

        if(Objects.isNull(updatedDevices) || Objects.isNull(updatedDevices.getShortName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Optional<Devices> device = devicesRepository.findById(id);
        if(device.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoDeviceFoundMessage);
        }

        Optional<CPU> cpu = cpuRepository.findById(updatedDevices.getCpuID());
        Optional<GPU> gpu = gpuRepository.findById(updatedDevices.getGpuID());
        Optional<RAM> ram = ramRepository.findById(updatedDevices.getRamID());
        Optional<OS> os = osRepository.findById(updatedDevices.getOsID());
        Optional<User> user = userRepository.findById(userID);

        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCPUFoundMessage);
        }
        else if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoGPUFoundMessage);
        }
        else if(ram.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoRAMFoundMessage);
        }
        else if(os.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoOSFoundMessage);
        }
        else if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        //IDs of the components
        long cpuID = cpu.get().getCpuID();
        long gpuID = gpu.get().getGpuID();
        long ramID = ram.get().getRamID();
        long osID = os.get().getOsID();

        //Name of device
        String shortName = updatedDevices.getShortName();


        if(!shortName.isBlank())
        {
            if(!checkIfSameRecordExists("shortName","devices",shortName)){
                if(updateField("devices","shortName",shortName,"deviceID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (cpuID)");
                }
            }
            else{
                return responseFromServer(HttpStatus.CONFLICT,request,DeviceDuplicateNameMessage);
            }
        }
        if(cpuID != updatedDevices.getCpuID()){
            if(updateField("devices","cpuID",String.valueOf(updatedDevices.getCpuID()),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (cpuID)");
            }
        }
        if(gpuID != updatedDevices.getGpuID()){
            if(updateField("devices","gpuID",String.valueOf(updatedDevices.getGpuID()),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (gpuID)");
            }
        }
        if(ramID != updatedDevices.getRamID()){
            if(updateField("devices","ramID",String.valueOf(updatedDevices.getRamID()),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (ramID)");
            }
        }
        if(osID != updatedDevices.getOsID()){
            if(updateField("devices","osID",String.valueOf(updatedDevices.getOsID()),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (osID)");
            }
        }
        return responseFromServer(HttpStatus.OK,request,DeviceUpdatedMessage);
    }
}
