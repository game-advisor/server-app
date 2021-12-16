package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.model.userOriented.UserShow;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DevicesService {

    private final DevicesRepository devicesRepository;
    private final CPURepository cpuRepository;
    private final GPURepository gpuRepository;
    private final RAMRepository ramRepository;
    private final OSRepository osRepository;
    private final UserRepository userRepository;

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

    public void addDevice(UpdatedDevices device, String token){

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
            throw new CustomRepsonses.MyDataConflict("Couldn't create device");
        }
    }

    @SneakyThrows
    public void deleteDevice(long id, String token){
        long userID = getUserIDFromToken(token);

        Devices device = devicesRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("Device of ID: " + id + " not found."));

        User user = device.getUser();

        if(userID == user.getUserID()){
            devicesRepository.deleteById(id);
        }
        else if(isUserAnAdmin(userID)){
            devicesRepository.deleteById(id);
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("User of ID " + userID + " tried to delete device of (ID): " + user.getUserID());
        }
    }

    @SneakyThrows
    @Transactional
    public ResponseEntity<Object> editDevice(UpdatedDevices updatedDevices, long id, String token){
        long userID = getUserIDFromToken(token);

        Devices device = devicesRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("Device of ID: " + id + " not found."));

        User user = device.getUser();

        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<String, String>();
        JSONObject response = new JSONObject(jsonOrderedMap);

        if(userID == user.getUserID()){
            String shortName = updatedDevices.getShortName();
            long cpuID = updatedDevices.getCpuID();
            long gpuID = updatedDevices.getGpuID();
            long ramID = updatedDevices.getRamID();
            long osID = updatedDevices.getOsID();
            boolean isHDD = updatedDevices.isHDD();
            boolean isSSD = updatedDevices.isSSD();

            if(!shortName.isBlank()){
                if(!checkIfRecordWithSameNameExists("shortName","devices", shortName))
                {
                    Query query = em.createNativeQuery("UPDATE devices SET shortName = ? WHERE deviceID = ?")
                            .setParameter(1, shortName)
                            .setParameter(2, id);
                    query.executeUpdate();
                    System.out.println("Update executed");
                }
                else{
                    Date date = new Date(System.currentTimeMillis());
                    response.put("message","Device with such name exists");
                    response.put("code", HttpStatus.CONFLICT.value());
                    response.put("timestamp",date);
                    response.put("path","/api/device/"+id+"/edit");
                    return new ResponseEntity<>(response.toMap(), HttpStatus.CONFLICT);
                }
            }
        }
        else{
            return new ResponseEntity<>("You don't have access to that resource", new HttpHeaders(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Nothing changed", new HttpHeaders(), HttpStatus.NOT_MODIFIED);
    }

    //Token value for testing
    //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMkB1c2VyLmNvbSIsInJvbGVzIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjM5NTk3OTg1LCJ1c2VySUQiOjJ9.V07n9xBSrGCLYn849ryVsZnAkmHQDjEAxNjKxpafjOw


    public JSONObject getBodyFromToken(String token){
        String[] splitString = token.split("\\.");
        String base64EncodedBody = splitString[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));

        return new JSONObject(body);
    }

    public boolean checkIfRecordWithSameNameExists(String columnName,String tableName, String name){

        Query query = em.createNativeQuery("SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName+ " = ?")
                .setParameter(1, name);
        try{
            query.getSingleResult();
        }
        catch (NoResultException e){
            e.getLocalizedMessage();
            return false;
        }
        return true;
    }

    public long getUserIDFromToken(String token){
        JSONObject tokenBody = getBodyFromToken(token);

        return Long.parseLong(tokenBody.get("userID").toString());
    }

    public String getUserRoleFromToken(String token){
        JSONObject tokenBody = getBodyFromToken(token);

        return tokenBody.get("roles").toString();
    }

    public boolean isUserAnAdmin(long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals("ROLE_ADMIN");
    }
}
