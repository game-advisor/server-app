package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.misc.FileStorageProperties;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileStorageService extends CustomFunctions {

    private final Path fileStorageLocation;

    @SneakyThrows
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.fileStorageLocation);
        }
        catch (Exception ex){
            throw new FileUploadException("Could not");
        }
    }

    @SneakyThrows
    public String storeFile(MultipartFile file, String token){
        String fileName;
        if(!isUserAnAdmin(getUserIDFromToken(token))){
            Date currentDate = new Date(System.currentTimeMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_SSS");
            fileName = "img_" + simpleDateFormat.format(currentDate) + ".png";
        }
        else{
            fileName = file.getName();
        }



        try{
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }
        catch (IOException | NullPointerException e)
        {
            throw new CustomRepsonses.MyDataConflict(fileName);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new CustomRepsonses.MyNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new CustomRepsonses.MyNotFoundException("File not found " + fileName);
        }
    }
}
