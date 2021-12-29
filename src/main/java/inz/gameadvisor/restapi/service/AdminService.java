package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.gameOriented.EditAddGame;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.model.userOriented.UserShow;
import inz.gameadvisor.restapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService extends CustomFunctions {

    private final UserRepository userRepository;
    private final DevicesRepository devicesRepository;
    private final ReviewRepository reviewRepository;
    private final ScoreRepository scoreRepository;
    private final CPURepository cpuRepository;
    private final CompaniesRepository companiesRepository;
    private final GPURepository gpuRepository;
    private final OSRepository osRepository;
    private final GameRepository gameRepository;

    @PersistenceContext
    EntityManager em;

    //User part of admin panel
    public ResponseEntity<Object> getUserInfo(long id, HttpServletRequest request ,String token) {
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Optional<User> user = userRepository.findById(id);

        UserShow userShow = new UserShow();

        if(user.isPresent()){
            userShow.setUserID(user.get().getUserID());
            userShow.setUsername(user.get().getUsername());
            userShow.setEmail(user.get().getEmail());
            userShow.setAvatarPath(user.get().getAvatarPath());
            return new ResponseEntity<>(userShow,HttpStatus.OK);
        }
        else{
            return responseFromServer(HttpStatus.NOT_FOUND, request, "User not found");
        }
    }
    public ResponseEntity<Object> getAllUsersInfo(Integer pageNumber, Integer pageSize, String sortBy, String token, HttpServletRequest request){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Pageable paging = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));
        Page<User> pagedResult = userRepository.findAll(paging);
        List<UserShow> userShowList = new ArrayList<>();

        if(!pagedResult.hasContent())
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No users found");
        else{
            for (User user : pagedResult) {
                UserShow userShow = new UserShow();
                userShow.setUserID(user.getUserID());
                userShow.setUsername(user.getUsername());
                userShow.setEmail(user.getEmail());
                userShow.setAvatarPath(user.getAvatarPath());
                userShowList.add(userShow);
            }
            return new ResponseEntity<>(userShowList.toArray(),HttpStatus.OK);
        }

    }

    //Devices part of admin panel
    public ResponseEntity<Object> getAllDevicesList(Integer pageNumber, Integer pageSize, String sortBy, String token, HttpServletRequest request){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Pageable paging = PageRequest.of(pageNumber,pageSize, Sort.by(sortBy));
        Page<Devices> pagedResult = devicesRepository.findAll(paging);

        if(!pagedResult.hasContent())
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
        else
            return new ResponseEntity<>(pagedResult.getContent(),HttpStatus.OK);
    }

    //CPU
    public ResponseEntity<Object> addCPU(EditAddCPU addCPU, HttpServletRequest request, String token) {
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(addCPU) || Objects.isNull(addCPU.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        CPU cpu = new CPU();

        String name = addCPU.getName();
        String series = addCPU.getSeries();
        long manufID = addCPU.getManufID();
        Optional<Companies> company = companiesRepository.findById(manufID);
        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }
        int score = addCPU.getScore();

        if(!name.isBlank()){
            if(checkIfSameRecordExists("name","cpu",name)){
                return responseFromServer(HttpStatus.CONFLICT,request,"CPU with the same name already exists!");
            }
        }
        else{
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"CPU name cannot be empty");
        }
        if(series.isBlank()){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"Series cannot be empty");
        }

        if(!checkIfManufacturerExistsWithSuchId(manufID)){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }

        if(score <= 0){
            return responseFromServer(HttpStatus.CONFLICT,request,"Your score values are less than or equal 0");
        }

        cpu.setName(name);
        cpu.setSeries(series);
        cpu.setScore(score);
        cpu.setCompany(company.get());
        try{
            cpuRepository.save(cpu);
        }
        catch (Exception e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"CPU has been saved");
    }
    @Transactional
    public ResponseEntity<Object> editCPU(long id, EditAddCPU editCPU, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(editCPU) || Objects.isNull(editCPU.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Optional<CPU> cpu = cpuRepository.findById(id);
        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCPUFoundMessage);
        }

        long manufID = editCPU.getManufID();

        if(manufID >= 0){
            Optional<Companies> company = companiesRepository.findById(manufID);

            if(company.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
            }

            String name = editCPU.getName();
            String series = editCPU.getSeries();
            int score = editCPU.getScore();

            if(!name.isBlank()){
                if(!checkIfSameRecordExists("name","cpu",name)){
                    if(updateField("cpu","name",name,"cpuID",String.valueOf(id)) == 0){
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                    }
                }
                else{
                    return responseFromServer(HttpStatus.CONFLICT,request,"Record with the same name exists");
                }
            }
            if(!series.isBlank()){
                if(!Objects.equals(cpu.get().getSeries(), series)){
                    if(updateField("cpu","series",series,"cpuID",String.valueOf(id)) == 0){
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                    }
                }
            }
            if(cpu.get().getCompany() != company.get()){
                if(updateField("cpu","manufID",String.valueOf(manufID),"cpuID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                }
            }
            if(cpu.get().getScore() != score){
                if(updateField("cpu","score",String.valueOf(score),"cpuID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                }
            }
            return responseFromServer(HttpStatus.OK,request,"CPU has been updated");
        }
        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
    }
    public ResponseEntity<Object> deleteCPU(long id, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Optional<CPU> cpu = cpuRepository.findById(id);

        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCPUFoundMessage);
        }

        try{
            cpuRepository.deleteById(id);
        }
        catch (IllegalArgumentException e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"CPU has been deleted");
    }

    //GPU
    public ResponseEntity<Object> addGPU(EditAddGPU addGPU, HttpServletRequest request, String token) {
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(addGPU) || Objects.isNull(addGPU.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        GPU gpu = new GPU();

        String name = addGPU.getName();
        String series = addGPU.getSeries();
        long manufID = addGPU.getManufID();
        Optional<Companies> company = companiesRepository.findById(manufID);

        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }
        int score = addGPU.getScore();

        if(!name.isBlank()){
            if(checkIfSameRecordExists("name","gpu",name)){
                return responseFromServer(HttpStatus.CONFLICT,request,"GPU with the same name already exists!");
            }
        }
        else{
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"GPU name cannot be empty");
        }
        if(series.isBlank()){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"Series cannot be empty");
        }

        if(!checkIfManufacturerExistsWithSuchId(manufID)){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }

        if(score <= 0){
            return responseFromServer(HttpStatus.CONFLICT,request,"Your score values are less than or equal 0");
        }

        gpu.setName(name);
        gpu.setSeries(series);
        gpu.setScore(score);
        gpu.setCompany(company.get());
        try{
            gpuRepository.save(gpu);
        }
        catch (Exception e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"GPU has been saved");
    }
    @Transactional
    public ResponseEntity<Object> editGPU(long id, EditAddGPU editGPU, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(editGPU) || Objects.isNull(editGPU.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Optional<GPU> gpu = gpuRepository.findById(id);
        if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoGPUFoundMessage);
        }

        long manufID = editGPU.getManufID();

        if(manufID >= 0){
            Optional<Companies> company = companiesRepository.findById(manufID);

            if(company.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
            }

            String name = editGPU.getName();
            String series = editGPU.getSeries();
            int score = editGPU.getScore();

            if(!name.isBlank()){
                if(!checkIfSameRecordExists("name","gpu",name)){
                    if(updateField("gpu","name",name,"gpuID",String.valueOf(id)) == 0){
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                    }
                }
                else{
                    return responseFromServer(HttpStatus.CONFLICT,request,"Record with the same name exists");
                }
            }
            if(!series.isBlank()){
                if(!Objects.equals(gpu.get().getSeries(), series)){
                    if(updateField("gpu","series",String.valueOf(series),"gpuID",String.valueOf(id)) == 0){
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                    }
                }
            }
            if(gpu.get().getCompany() != company.get()){
                if(updateField("gpu","manufID",String.valueOf(manufID),"gpuID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                }
            }
            if(gpu.get().getScore() != score){
                if(updateField("gpu","score",String.valueOf(score),"gpuID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                }
            }
            return responseFromServer(HttpStatus.OK,request,"GPU has been updated");
        }
        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
    }
    public ResponseEntity<Object> deleteGPU(long id, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Optional<GPU> gpu = gpuRepository.findById(id);

        if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoGPUFoundMessage);
        }

        try{
            gpuRepository.deleteById(id);
        }
        catch (IllegalArgumentException e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"GPU has been deleted");
    }

    //OS
    public ResponseEntity<Object> addOS(EditAddOS addOS, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN, request, ForbiddenAccessMessage);
        }

        if(Objects.isNull(addOS) || Objects.isNull(addOS.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        OS os = new OS();

        String name = addOS.getName();
        long manufID = addOS.getManufID();

        Optional<Companies> company = companiesRepository.findById(manufID);

        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }

        if(!name.isBlank()){
            if(checkIfSameRecordExists("name","ram",name)){
                return responseFromServer(HttpStatus.CONFLICT,request,"OS with the same name already exists!");
            }
        }
        else{
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"OS name cannot be empty");
        }

        os.setName(name);
        os.setCompany(company.get());

        try{
            osRepository.save(os);
        }
        catch (Exception e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"OS has been saved");

    }
    @Transactional
    public ResponseEntity<Object> editOS(long id, EditAddOS editOS, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(editOS) || Objects.isNull(editOS.getName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Optional<OS> os = osRepository.findById(id);
        if(os.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoOSFoundMessage);
        }

        long manufID = editOS.getManufID();

        if(manufID >= 0){
            Optional<Companies> company = companiesRepository.findById(manufID);

            if(company.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
            }

            String name = editOS.getName();

            if(!name.isBlank()){
                if(!checkIfSameRecordExists("name","os",name)){
                    if(updateField("os","name",name,"osID",String.valueOf(id)) == 0){
                        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                    }
                }
                else{
                    return responseFromServer(HttpStatus.CONFLICT,request,"Record with the same name exists");
                }
            }
            if(os.get().getCompany() != company.get()){
                if(updateField("os","manufID",String.valueOf(manufID),"osID",String.valueOf(id)) == 0){
                    return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
                }
            }
            return responseFromServer(HttpStatus.OK,request,"OS has been updated");
        }
        return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Record has not been updated");
    }
    public ResponseEntity<Object> deleteOS(long id, HttpServletRequest request, String token){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        Optional<OS> os = osRepository.findById(id);

        if(os.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoOSFoundMessage);
        }

        try{
            osRepository.deleteById(id);
        }
        catch (IllegalArgumentException e){
            return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,e.getMessage());
        }
        return responseFromServer(HttpStatus.OK,request,"OS has been deleted");
    }

    //Game
    public ResponseEntity<Object> addGame(EditAddGame addGame, HttpServletRequest request, String token) {
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        if(Objects.isNull(addGame)){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Game game = new Game();

        game.setName(addGame.getName());

        Optional<Companies> company = companiesRepository.findById(addGame.getCompanyID());
        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }
        game.setCompany(company.get());
        game.setImagePath(addGame.getImagePath());
        game.setPublishDate(addGame.getPublishDate());

        gameRepository.save(game);

        return responseFromServer(HttpStatus.OK,request,"Game has been saved");
    }
}
