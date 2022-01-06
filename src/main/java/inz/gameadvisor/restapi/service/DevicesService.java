package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.*;
import inz.gameadvisor.restapi.model.userOriented.User;
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
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DevicesService extends CustomFunctions {

    private final DevicesRepository devicesRepository;
    private final CPURepository cpuRepository;
    private final GPURepository gpuRepository;
    private final RAMRepository ramRepository;
    private final OSRepository osRepository;
    private final UserRepository userRepository;
    private final CompaniesRepository companiesRepository;

    @PersistenceContext
    EntityManager em;

    public ResponseEntity<Object> getDevicesOfLoggedInUser(String token, HttpServletRequest request){

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
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No such user found");
        }
        Page<Devices> pagedResult = devicesRepository.findAllByUser(user.get(),paging);

        if(!pagedResult.hasContent())
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
        else{

            if(pagedResult.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"No devices found");
            }
            return new ResponseEntity<>(pagedResult,HttpStatus.OK);
        }

    }

    public ResponseEntity<Object> getDeviceByID(long deviceID, String token, HttpServletRequest request){
        Optional<Devices> device = devicesRepository.findById(deviceID);
        if(device.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No such device found");
        }
        if(device.get().getUser().getUserID() != getUserIDFromToken(token)){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

        return new ResponseEntity<>(device.get(),HttpStatus.OK);
    }

    public ResponseEntity<Object> addDevice(UpdatedDevices device, HttpServletRequest request, String token){

        long userID = getUserIDFromToken(token);

        Devices createdDevice = new Devices();

        Optional<CPU> cpu = cpuRepository.findById(device.getCpuID());
        Optional<GPU> gpu = gpuRepository.findById(device.getGpuID());
        Optional<OS> os = osRepository.findById(device.getOsID());
        Optional<User> user = userRepository.findById(userID);

        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCPUFoundMessage);
        }
        else if(gpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoGPUFoundMessage);
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
        RAM ram = new RAM();
        ram.setSize(device.getSize());
        ram.setFreq(device.getFreq());
        ram.setLatency(device.getLatency());
        ram.setAmountOfSticks(device.getAmountOfSticks());
        ramRepository.save(ram);
        createdDevice.setRam(ram);
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
        if(Objects.isNull(updatedDevices) || Objects.isNull(updatedDevices.getShortName())){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }

        Optional<Devices> device = devicesRepository.findById(id);
        if(device.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoDeviceFoundMessage);
        }

        Optional<CPU> cpu = cpuRepository.findById(updatedDevices.getCpuID());
        Optional<GPU> gpu = gpuRepository.findById(updatedDevices.getGpuID());
        Optional<RAM> ram = ramRepository.findById(device.get().getRam().getRamID());
        Optional<OS> os = osRepository.findById(updatedDevices.getOsID());
        Optional<User> user = userRepository.findById(getUserIDFromToken(token));

        if(user.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoUserFoundMessage);
        }

        if(device.get().getUser().getUserID() != user.get().getUserID()){
            return responseFromServer(HttpStatus.FORBIDDEN,request,ForbiddenAccessMessage);
        }

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

        //IDs of the components
        long cpuID = cpu.get().getCpuID();
        long gpuID = gpu.get().getGpuID();
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
        if(ram.get().getSize() != updatedDevices.getSize()){
            if(updateField("ram","size",String.valueOf(updatedDevices.getSize()),"ramID",String.valueOf(ram.get().getRamID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (size)");
            }
        }
        if(ram.get().getFreq() != updatedDevices.getFreq()){
            if(updateField("ram","freq",String.valueOf(updatedDevices.getFreq()),"ramID",String.valueOf(ram.get().getRamID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (freq)");
            }
        }
        if(ram.get().getLatency() != updatedDevices.getLatency()){
            if(updateField("ram","latency",String.valueOf(updatedDevices.getLatency()),"ramID",String.valueOf(ram.get().getRamID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (latency)");
            }
        }
        if(ram.get().getAmountOfSticks() != updatedDevices.getAmountOfSticks()){
            if(updateField("ram","amountOfSticks",String.valueOf(updatedDevices.getAmountOfSticks()),"ramID",String.valueOf(ram.get().getRamID())) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (amountOfSticks)");
            }
        }
        if(osID != updatedDevices.getOsID()){
            if(updateField("devices","osID",String.valueOf(updatedDevices.getOsID()),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (osID)");
            }
        }
        if(device.get().isHDD() != updatedDevices.isHDD()){
            if(updateField("devices","isHDD",String.valueOf(boolToInt(updatedDevices.isHDD())),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (hdd)");
            }
        }
        if(device.get().isSSD() != updatedDevices.isSSD()){
            if(updateField("devices","isSSD",String.valueOf(boolToInt(updatedDevices.isSSD())),"deviceID",String.valueOf(id)) == 0){
                return responseFromServer(HttpStatus.INTERNAL_SERVER_ERROR,request,"Internal server error on record update (ssd)");
            }
        }
        return responseFromServer(HttpStatus.OK,request,DeviceUpdatedMessage);
    }

    public ResponseEntity<Object> getAllCPUListBySeries(String series, HttpServletRequest request){
        List<CPU> cpuList = cpuRepository.findAllBySeries(series);
        if(cpuList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No CPU of such series found");
        }
        List<CPUGPUName> cpuNames = new ArrayList<>();
        for (CPU cpu:
             cpuList) {
            CPUGPUName cpuName = new CPUGPUName();
            cpuName.setName(cpu.getName());
            cpuNames.add(cpuName);
        }
        cpuNames.sort(Comparator.comparing(CPUGPUName::getName));
        return new ResponseEntity<>(cpuNames,HttpStatus.OK);
    }

    public ResponseEntity<Object> getAllGPUListBySeries(String series, HttpServletRequest request){
        List<GPU> gpuList = gpuRepository.findAllBySeries(series);
        if(gpuList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No GPU of such series found");
        }
        List<CPUGPUName> gpuNames = new ArrayList<>();
        for (GPU gpu:
                gpuList) {
            CPUGPUName gpuName = new CPUGPUName();
            gpuName.setName(gpu.getName());
            gpuNames.add(gpuName);
        }
        gpuNames.sort(Comparator.comparing(CPUGPUName::getName));
        return new ResponseEntity<>(gpuNames,HttpStatus.OK);
    }

    public ResponseEntity<Object> getCPUSeriesByCompany(String companyName, HttpServletRequest request){
        Optional<Companies> companies = companiesRepository.findByName(companyName);
        if(companies.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Company of given name was not found");
        }
        Query query = em.createNativeQuery("SELECT series FROM cpu WHERE manufID = ? GROUP BY series")
                .setParameter(1,companies.get().getCompanyID());

        List<?> seriesList = query.getResultList();
        List<CPUSeries> cpuSeriesList = new ArrayList<>();
        for (Object object:
             seriesList) {
            CPUSeries cpuSeries = new CPUSeries();
            cpuSeries.setSeries(String.valueOf(seriesList.get(seriesList.indexOf(object))));
            cpuSeriesList.add(cpuSeries);
        }
        if(seriesList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No CPU found for given company name");
        }
        return new ResponseEntity<>(cpuSeriesList,HttpStatus.OK);
    }

    public ResponseEntity<Object> getGPUSeriesByCompany(String companyName, HttpServletRequest request){
        Optional<Companies> companies = companiesRepository.findByName(companyName);
        if(companies.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Company of given name was not found");
        }
        Query query = em.createNativeQuery("SELECT series FROM gpu WHERE manufID = ? GROUP BY series")
                .setParameter(1,companies.get().getCompanyID());

        List<?> seriesList = query.getResultList();
        List<GPUSeries> gpuSeriesList = new ArrayList<>();
        for (Object object:
                seriesList) {
            GPUSeries gpuSeries = new GPUSeries();
            gpuSeries.setSeries(String.valueOf(seriesList.get(seriesList.indexOf(object))));
            gpuSeriesList.add(gpuSeries);
        }
        if(seriesList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No GPU found for given company name");
        }
        return new ResponseEntity<>(gpuSeriesList,HttpStatus.OK);
    }

    public ResponseEntity<Object> getCPUInfoByModelName(String model, HttpServletRequest request) {
        Optional<CPU> cpu = cpuRepository.findByName(model);
        if(cpu.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No CPU by such name found");
        }
        return new ResponseEntity<>(cpu.get(),HttpStatus.OK);
    }

    public ResponseEntity<Object> getOSByCompanyName(String companyName, HttpServletRequest request) {
        Optional<Companies> companies = companiesRepository.findByName(companyName);
        if(companies.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No company with such name found");
        }
        List<Optional<OS>> osList = osRepository.findAllByCompany(companies.get());
        if(osList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No OS found under this company");
        }
        return new ResponseEntity<>(osList,HttpStatus.OK);
    }


}
