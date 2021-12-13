package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DevicesRepository devicesRepository;
    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;
    private final CPURepository cpuRepository;
    private final CompaniesRepository companiesRepository;
    private final GPURepository gpuRepository;

    @PersistenceContext
    EntityManager em;

    //User part of AdMiN panel
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
    public List<User> getAllUsersList(Integer pageNumber, Integer pageSize, String sortBy, String token){
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
            Pageable paging = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));

            Page<User> pagedResult = userRepository.findAll(paging);

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

    //Devices part of AdMiN panel
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

    @SneakyThrows
    public void addCPU(EditAddCPU cpuAdded, String token) {
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
            CPU addCPU = new CPU();

            Companies company = companiesRepository.findById(cpuAdded.getManufID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such company"));

            addCPU.setName(cpuAdded.getName());
            addCPU.setManufID(cpuAdded.getManufID());
            addCPU.setSeries(cpuAdded.getSeries());
            addCPU.setScore(cpuAdded.getScore());

            try{
                cpuRepository.save(addCPU);
            }
            catch (DataIntegrityViolationException e){
                throw new CustomRepsonses.MyDataConflict("Duplicated data");
            }
        }
        else {
            throw new CustomRepsonses.MyForbiddenAccess("User of id " + userID + " tried to access resource while not being an admin!");
        }
    }

    @SneakyThrows
    @Transactional
    public void editCPU(long id, EditAddCPU editCPU, String token){
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID))
        {
            Companies company = companiesRepository.findById(editCPU.getManufID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such company"));
            CPU cpu = cpuRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such CPU"));

            long cpuID = id;
            String name = editCPU.getName();
            String series = editCPU.getSeries();
            long manufID = editCPU.getManufID();
            int score = editCPU.getScore();

            if(!name.isBlank()) {
                if(!checkIfRecordWithSameNameExists("cpu",name)) {
                    Query query = em.createNativeQuery("UPDATE cpu SET name = ? WHERE cpuID = ?")
                            .setParameter(1, name)
                            .setParameter(2, cpuID);
                        query.executeUpdate();
                }
                else {
                    throw new CustomRepsonses.MyDataConflict("CPU of the same name exists!");
                }
            }
            if(!series.isBlank()) {
                Query query = em.createNativeQuery("UPDATE cpu SET series = ? WHERE cpuID = ?")
                        .setParameter(1, series)
                        .setParameter(2, cpuID);
                query.executeUpdate();
            }
            if(!checkIfManufacturerExistsWithSuchId(manufID)){
                Query query = em.createNativeQuery("UPDATE cpu SET manufID = ? WHERE cpuID = ?")
                        .setParameter(1, manufID)
                        .setParameter(2, cpuID);
                query.executeUpdate();
            }
            if(score > 0 && score < Integer.MAX_VALUE){
                Query query = em.createNativeQuery("UPDATE cpu SET score = ? WHERE cpuID = ?")
                        .setParameter(1, score)
                        .setParameter(2, cpuID);
                query.executeUpdate();
            }
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("User of id " + userID + " tried to access resource while not being an admin!");
        }
    }

    @SneakyThrows
    public void addGPU(EditAddGPU gpuAdded, String token) {
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID)){
            GPU addGpu = new GPU();

            Companies company = companiesRepository.findById(gpuAdded.getManufID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such company"));

            addGpu.setName(gpuAdded.getName());
            addGpu.setManufID(gpuAdded.getManufID());
            addGpu.setSeries(gpuAdded.getSeries());
            addGpu.setScore(gpuAdded.getScore());

            try{
                gpuRepository.save(addGpu);
            }
            catch (DataIntegrityViolationException e){
                throw new CustomRepsonses.MyDataConflict("Duplicated data");
            }
        }
        else {
            throw new CustomRepsonses.MyForbiddenAccess("User of id " + userID + " tried to access resource while not being an admin!");
        }
    }

    @SneakyThrows
    @Transactional
    public void editGPU(long id, EditAddGPU editGPU, String token){
        long userID = getUserIDFromToken(token);

        if(isUserAnAdmin(userID))
        {
            Companies company = companiesRepository.findById(editGPU.getManufID()).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such company"));
            GPU gpu = gpuRepository.findById(id).orElseThrow(() -> new CustomRepsonses.MyNotFoundException("No such CPU"));

            long gpuID = id;
            String name = editGPU.getName();
            String series = editGPU.getSeries();
            long manufID = editGPU.getManufID();
            int score = editGPU.getScore();

            if(!name.isBlank()) {
                if(!checkIfRecordWithSameNameExists("gpu",name)) {
                    Query query = em.createNativeQuery("UPDATE gpu SET name = ? WHERE gpuID = ?")
                            .setParameter(1, name)
                            .setParameter(2, gpuID);
                    query.executeUpdate();
                }
                else {
                    throw new CustomRepsonses.MyDataConflict("CPU of the same name exists!");
                }
            }
            if(!series.isBlank()) {
                Query query = em.createNativeQuery("UPDATE gpu SET series = ? WHERE gpuID = ?")
                        .setParameter(1, series)
                        .setParameter(2, gpuID);
                query.executeUpdate();
            }
            if(!checkIfManufacturerExistsWithSuchId(manufID)){
                Query query = em.createNativeQuery("UPDATE gpu SET manufID = ? WHERE gpuID = ?")
                        .setParameter(1, manufID)
                        .setParameter(2, gpuID);
                query.executeUpdate();
            }
            if(score > 0 && score < Integer.MAX_VALUE){
                Query query = em.createNativeQuery("UPDATE gpu SET score = ? WHERE gpuID = ?")
                        .setParameter(1, score)
                        .setParameter(2, gpuID);
                query.executeUpdate();
            }
        }
        else{
            throw new CustomRepsonses.MyForbiddenAccess("User of id " + userID + " tried to access resource while not being an admin!");
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

    public boolean checkIfRecordWithSameNameExists(String tableName, String name){

        Query query = em.createNativeQuery("SELECT name FROM " + tableName + " WHERE name = ?")
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

    public boolean checkIfManufacturerExistsWithSuchId(long id){
        Query query = em.createNativeQuery("SELECT name FROM companies WHERE companyID = ?")
                .setParameter(1, id);

        return query.getSingleResult().toString().isBlank();
    }

    public boolean isUserAnAdmin(long userID) {
        Query query = em.createNativeQuery("SELECT roles FROM users WHERE userID = ?;")
                .setParameter(1, userID);

        String queryUserRole = query.getSingleResult().toString();

        return queryUserRole.equals("ROLE_ADMIN");
    }
}
