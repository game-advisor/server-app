package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.deviceOriented.UpdatedDevices;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.DevicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DevicesService {

    private final DevicesRepository devicesRepository;

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

    public void createDevice(UpdatedDevices device, String token){

        long userID = getUserIDFromToken(token);


//        try {
//            devicesRepository.save(createdDevice);
//        }
//        catch (Exception e){
//            throw new CustomRepsonses.MyDataConflict("Constraint failed");
//        }

    }

    @SneakyThrows
    public void deleteDevice(long id, String token){
        long userID = getUserIDFromToken(token);

        Devices device = devicesRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("Device of ID: " + id + " not found."));

        User user = device.getUser();

        if(userID == user.getUserID()){
            devicesRepository.deleteById(id);
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("User of ID " + userID + " tried to delete device of (ID): " + user.getUserID());
        }

    }

    public JSONObject getBodyFromToken(String token){
        String[] splitString = token.split("\\.");
        String base64EncodedBody = splitString[1];
        Base64 base64Url = new Base64(true);
        String body = new String(base64Url.decode(base64EncodedBody));

        return new JSONObject(body);
    }

    public long getUserIDFromToken(String token){
        JSONObject tokenBody = getBodyFromToken(token);

        return Long.parseLong(tokenBody.get("userID").toString());
    }

    public String getUserRoleFromToken(String token){
        JSONObject tokenBody = getBodyFromToken(token);

        return tokenBody.get("roles").toString();
    }

    public boolean isUserAnAdmin(String roles, long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals(roles);
    }
}
