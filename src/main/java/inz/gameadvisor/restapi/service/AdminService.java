package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.Score;
import inz.gameadvisor.restapi.model.deviceOriented.AddCPU;
import inz.gameadvisor.restapi.model.deviceOriented.CPU;
import inz.gameadvisor.restapi.model.deviceOriented.Devices;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import springfox.documentation.annotations.ApiIgnore;

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
    private final CPURepository cpuRepository;

    @PersistenceContext
    EntityManager em;

    @SneakyThrows
    public User getUserInfo(long id, String token) throws CustomRepsonses.MyNotFoundException {
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
            return userRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such user"));
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("You are not an admin");
        }
    }

    @SneakyThrows
    public List<Devices> getAllDevicesList(Integer pageNumber, Integer pageSize, String sortBy, String token){
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
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

    @SneakyThrows
    public void addCPU(AddCPU cpuAdded, String token) {
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
            CPU addCPU = new CPU();

            addCPU.setName(cpuAdded.getName());
            addCPU.setManufID(cpuAdded.getManufID());
            addCPU.setSeries(cpuAdded.getSeries());
            addCPU.setScore(cpuAdded.getScore());

            cpuRepository.save(addCPU);
        }
        else
        {
            throw new CustomRepsonses.MyForbiddenAccess("User of id " + userID + " tried to access resource while not being an admin!");
        }
    }

    public CPU editCPU(long id, CPU editCPU, String token){

        long cpuID = id;


        return null;
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

    public boolean isUserAnAdmin(long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals("ROLE_ADMIN");
    }
}
