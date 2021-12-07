package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.Score;
import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.DevicesRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.ScoreRepository;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DevicesRepository devicesRepository;
    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;

    @PersistenceContext
    EntityManager em;

    @SneakyThrows
    public User getUserInfo(long id, String token) throws CustomRepsonses.MyNotFoundException {
        long userID = getUserIDFromToken(token);

        boolean userRole = isUserAnAdmin("ROLE_ADMIN", userID);
        if(userRole){
            return userRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such user"));
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("You are not an admin");
        }
    }

    @SneakyThrows
    public List<Devices> getAllDevicesList(Integer pageNumber, Integer pageSize, String sortBy, String token){
        String roleFromToken = getUserRoleFromToken(token);
        long userID = getUserIDFromToken(token);

        boolean userRole = isUserAnAdmin("ROLE_ADMIN",userID);

        if(userRole){
            System.out.println("User is an admin");
            Pageable paging = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));

            Page<Devices> pagedResult = devicesRepository.findAll(paging);

            if(!pagedResult.hasContent()){
                throw new CustomRepsonses.MyNotFoundException("Not found");
            }
            else{
                return pagedResult.getContent();
            }
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("You are not an admin");
        }
    }

    public List<Score> getAllScores(){
        if(scoreRepository.findAll().isEmpty())
            throw new CustomRepsonses.MyNotFoundException("No elements found");
        else
            return scoreRepository.findAll();
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
