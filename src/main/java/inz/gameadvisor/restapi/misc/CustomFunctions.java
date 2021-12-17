package inz.gameadvisor.restapi.misc;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class CustomFunctions {

    @PersistenceContext
    EntityManager em;

    public ResponseEntity<Object> responseFromServer(HttpStatus returnCode, String path, String message){
        LinkedHashMap<String, String> jsonOrderedMap = new LinkedHashMap<>();
        JSONObject response = new JSONObject(jsonOrderedMap);
        Date date = new Date(System.currentTimeMillis());
        response.put("message",message);
        response.put("code", returnCode.value());
        response.put("timestamp",date);
        response.put("path", path);
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

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals("ROLE_ADMIN");
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
            for (Object item:
                    resultList) {
                if(item.equals("name"))
                    return false;
            }
        }
        catch (NoResultException e){
            e.getLocalizedMessage();
            return false;
        }
        return true;
    }
}
