package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CPUList;
import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.GPUList;
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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
        float score = addCPU.getScore();

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
            float score = editCPU.getScore();

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
        float score = addGPU.getScore();

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
            float score = editGPU.getScore();

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

    //Seeder
    public ResponseEntity<Object> populateCPU(String token, HttpServletRequest request){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }
        String filePath = System.getProperty("user.dir");
        String fullPath = filePath+ "\\" + "cpus.csv";

        File file = new File(fullPath);
        List<CPUList> cpuList = new ArrayList<>();
        try{
            Scanner reader = new Scanner(file);
            reader.nextLine();
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String[] lines = line.split(",");

                String producent = lines[2];
                String model = lines[3];
                String seria = "";

                //Intel
                if (producent.equals("Intel")){
                    if(model.contains("Core2")){
                        if(model.contains("Duo")){
                            seria = model.substring(0,9);
                        }
                        else if(model.contains("Quad")){
                            seria = model.substring(0,10);
                        }
                        else if(model.contains("Extreme")){
                            seria = model.substring(0,13);
                        }
                        else{
                            seria = model.substring(0,5);
                        }
                    }
                    else if(model.contains("Core")) {
                        if(model.contains("Duo")){
                            seria = model.substring(0,8);
                        }
                        else {
                            seria = model.substring(0,7);
                        }
                    }
                    else if(model.contains("Xeon")){
                        seria = model.substring(0,7);
                    }
                    else if(model.contains("Celeron")){
                        if(model.contains("D")){
                            seria = model.substring(0,9);
                        }
                        else{
                            seria = model.substring(0,7);
                        }
                    }
                    else if (model.contains("Atom")){
                        seria = model.replaceAll(" \\S.*","");
                    }
                    else if (model.contains("Pentium")){
                        if(model.contains("4 ") || model.contains("M")){
                            seria = model.substring(0,9);
                        }
                        else{
                            seria = model.substring(0,7);
                        }
                    }
                }
                //AMD
                else if (producent.equals("AMD")){
                    if(model.contains("Ryzen")){
                        if(model.contains("TR")){
                            seria = model.substring(0,8);
                        }
                        else{
                            seria = model.substring(0,7);
                        }
                    }
                    else{
                        String[] nameExploded = model.split(" ");
                        if (nameExploded[0].contains("-")){
                            String[] secondExpload = nameExploded[0].split("-");
                            seria = secondExpload[0];
                        }
                        else{
                            try{
                                if (nameExploded[1].contains("II") || nameExploded[1].contains("64"))
                                    seria = nameExploded[0] + " " + nameExploded[1];
                                else
                                    seria = nameExploded[0];
                            }
                            catch (ArrayIndexOutOfBoundsException e){
                                seria = "Other";
                            }
                        }
                    }
                }

                float wynik = Float.parseFloat(lines[5]);

                CPUList cpu = new CPUList(producent,seria,model,wynik);

                cpuList.add(cpu);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return responseFromServer(HttpStatus.NOT_FOUND,request,"File not found");
        }

        for (CPUList cpu:
                cpuList) {
            CPU cpu1 = new CPU();
            Optional<Companies> companies = companiesRepository.findByName(cpu.getProducent());
            cpu1.setCompany(companies.get());
            cpu1.setName(cpu.getNazwa());
            cpu1.setSeries(cpu.getSeria());
            cpu1.setScore(cpu.getWynik());
            cpuRepository.save(cpu1);
        }

        return responseFromServer(HttpStatus.OK,request,"Database has been seeded");
    }

    public ResponseEntity<Object> populateGPU(String token, HttpServletRequest request){
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }
        String filePath = System.getProperty("user.dir");
        String fullPath = filePath+ "\\" + "gpus.csv";

        File file = new File(fullPath);
        List<GPUList> gpuList = new ArrayList<>();
        try{
            Scanner reader = new Scanner(file);
            reader.nextLine();
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                String[] lines = line.split(",");

                String producent = lines[2];
                String model = lines[3];
                String seria = "";

                //nVidia
                if(producent.equals("Nvidia")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                        else if(model.contains("Titan"))
                            seria = "Titan RTX";
                        else if(model.contains("Quadro")){
                            seria = "Quadro RTX";
                        }
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("Quadro")){
                        producent = "nVidia";
                        seria = "Quadro";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                }
                //AMD
                else if(producent.equals("AMD")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("Zotac")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("HD 4")){
                            producent = "AMD";
                            seria = "HD 4000";
                        }
                        else if(model.contains("HD 5")){
                            producent = "AMD";
                            seria = "HD 5000";
                        }
                        else if(model.contains("HD 6")){
                            producent = "AMD";
                            seria = "HD 6000";
                        }
                        else if(model.contains("HD 7")){
                            producent = "AMD";
                            seria = "HD 7000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("PNY")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("Asus")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("MSI")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30")){
                            seria = "GeForce RTX 30";
                        }
                        else if(model.contains("RTX 20")){
                            seria = "GeForce RTX 20";
                        }
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("Gigabyte")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30")){
                            seria = "GeForce RTX 30";
                        }
                        else if(model.contains("RTX 20")){
                            seria = "GeForce RTX 20";
                        }
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("EVGA")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("TITAN") || model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("Gainward")){
                    if(model.contains("RTX")){
                        producent = "nVidia";
                        if(model.contains("RTX 30"))
                            seria = "GeForce RTX 30";
                        else if(model.contains("RTX 20"))
                            seria = "GeForce RTX 20";
                    }
                    else if(model.contains("Titan")){
                        producent = "nVidia";
                        seria = "Titan";
                    }
                    else if(model.contains("GTX")){
                        producent = "nVidia";
                        if(model.contains("GTX 10"))
                            seria = "GeForce GTX 10";
                        else if(model.contains("GTX 16")){
                            seria = "GeForce GTX 16";
                        }
                        else if(model.contains("Titan")){
                            producent = "nVidia";
                            seria = "Titan";
                        }
                        else if(model.contains("GTX 9"))
                            seria = "GeForce GTX 9";
                        else if(model.contains("GTX 7"))
                            seria = "GeForce GTX 7";
                        else if(model.contains("GTX 6"))
                            seria = "GeForce GTX 6";
                        else if(model.contains("GTX 5"))
                            seria = "GeForce GTX 5";
                        else if(model.contains("GTX 4"))
                            seria = "GeForce GTX 4";
                    }
                    else if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("Sapphire")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("PowerColor")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("XFX")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("ASRock")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else if(producent.equals("PwrHis")){
                    if(model.contains("RX")){
                        producent = "AMD";
                        if(model.contains("RX 5")){
                            if(model.contains("5300") || model.contains("5500") || model.contains("5600") || model.contains("5700")){
                                seria = "RX 5000";
                            }
                            else if(model.contains("550") || model.contains("560") || model.contains("570") || model.contains("580") || model.contains("590")){
                                seria = "RX 500";
                            }
                        }
                        else if(model.contains("RX 4")){
                            if(model.contains("460") || model.contains("470") || model.contains("480") || model.contains("490")){
                                seria = "RX 400";
                            }
                        }
                        else if(model.contains("RX 6")){
                            seria = "RX 6000";
                        }
                        else if(model.contains("Vega")){
                            if(model.contains("56")){
                                seria = "RX Vega 56";
                            }
                            else if(model.contains("64")){
                                seria = "RX Vega 64";
                            }
                        }
                    }
                    else if(model.contains("HD 4")){
                        producent = "AMD";
                        seria = "HD 4000";
                    }
                    else if(model.contains("HD 5")){
                        producent = "AMD";
                        seria = "HD 5000";
                    }
                    else if(model.contains("HD 6")){
                        producent = "AMD";
                        seria = "HD 6000";
                    }
                    else if(model.contains("HD 7")){
                        producent = "AMD";
                        seria = "HD 7000";
                    }
                    else if(model.contains("R9")){
                        producent = "AMD";
                        seria = "R9";
                    }
                    else if(model.contains("R7")){
                        producent = "AMD";
                        seria = "R7";
                    }
                    else if(model.contains("R5")){
                        producent = "AMD";
                        seria = "R5";
                    }
                    else
                        producent = "Other";
                }
                else {
                    producent = "Other";
                    seria = "";
                }
                float wynik = Float.parseFloat(lines[5]);

                GPUList gpu = new GPUList(producent,seria,model,wynik);

                gpuList.add(gpu);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return responseFromServer(HttpStatus.NOT_FOUND,request,"File not found");
        }

        for (GPUList gpu:
                gpuList) {
            GPU gpu1 = new GPU();
            Optional<Companies> companies = companiesRepository.findByName(gpu.getProducent());
            if(companies.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"Company of name " + gpu.getProducent() + " not found");
            }
            gpu1.setCompany(companies.get());
            gpu1.setName(gpu.getNazwa());
            gpu1.setSeries(gpu.getSeria());
            gpu1.setScore(gpu.getWynik());
            gpuRepository.save(gpu1);
        }
        return responseFromServer(HttpStatus.OK,request,"GPU table has been seeded");
    }
}
