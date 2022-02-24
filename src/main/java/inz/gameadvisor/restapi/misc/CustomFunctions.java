package inz.gameadvisor.restapi.misc;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CustomFunctions {

    public final String ForbiddenAccessMessage = "You don't have access to that resource";
    public final String BadRequestMessage = "Bad request";
    public final String NoDeviceFoundMessage = "Device of given ID was not found";
    public final String NoCPUFoundMessage = "CPU of given ID was not found";
    public final String NoGPUFoundMessage = "GPU of given ID was not found";
    public final String NoRAMFoundMessage = "RAM of given ID was not found";
    public final String NoOSFoundMessage = "OS of given ID was not found";
    public final String NoUserFoundMessage = "User of given ID was not found";
    public final String NoCompanyFoundMessage = "Company of given ID was not found";
    public final String DeviceAddedMessage = "Device added successfully";
    public final String DeviceNotAddedMessage = "Device not added";
    public final String DeviceUpdatedMessage = "Device update successful";
    public final String DeviceNotUpdatedMessage = "Device update not successful";
    public final String DeviceDeleteMessage = "Device deleted successfully";
    public final String DeviceDuplicateNameMessage = "Device with same name already exists";
    public final String MethodNotApplied = "Method has not been applied";


    @PersistenceContext
    EntityManager em;

    public ResponseEntity<Object> responseFromServer(HttpStatus returnCode, HttpServletRequest request, String message){
        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
        JSONObject response = new JSONObject(jsonOrderedMap);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dateFormatted = simpleDateFormat.format(date);
        response.put("message",message);
        response.put("code", returnCode.value());
        response.put("timestamp",dateFormatted);
        response.put("path", request.getRequestURI());
        return new ResponseEntity<>(response.toMap(), returnCode);
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

    public boolean isUserAnAdmin(long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        return query.getSingleResult().toString().equals("ROLE_ADMIN");
    }

    @Transactional
    public int updateField(String tableName, String columnName,String columnUpdateValue, String conditionName, String conditionValue){
        String updateQuery = "UPDATE " + tableName + " SET " + columnName + " = ?" + " WHERE " + conditionName + " = ? ";
        Query query = em.createNativeQuery(updateQuery)
                .setParameter(1, columnUpdateValue)
                .setParameter(2, conditionValue);
        try{
            return query.executeUpdate();
        }
        catch(IllegalStateException | PersistenceException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public boolean checkIfSameRecordExists(String columnName,String tableName, String name){

        Query query = em.createNativeQuery("SELECT " + columnName + " FROM " + tableName + " WHERE " + columnName+ " = ?")
                .setParameter(1, name);
        try{
            List<?> resultList = query.getResultList();
            if(resultList.isEmpty())
                return false;
            for (Object item: resultList) {
                if(item.toString().equals(name))
                    return true;
            }
        }
        catch (NoResultException e){
            e.getLocalizedMessage();
            return false;
        }
        return false;
    }

    public static boolean checkEmailValidity(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    public int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
}
