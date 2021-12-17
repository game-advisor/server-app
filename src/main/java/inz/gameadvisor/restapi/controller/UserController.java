package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.CustomRepsonses;
import inz.gameadvisor.restapi.model.userOriented.LoginCredentials;
import inz.gameadvisor.restapi.model.userOriented.RegisterCredentials;
import inz.gameadvisor.restapi.model.userOriented.UpdateUser;
import inz.gameadvisor.restapi.model.userOriented.User;
import inz.gameadvisor.restapi.repository.UserRepository;
import inz.gameadvisor.restapi.service.FileStorageService;
import inz.gameadvisor.restapi.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class UserController extends CustomFunctions {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final CustomFunctions customFunctions = new CustomFunctions();

    @PutMapping("/api/user/edit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Method has not been used")
    })
    public ResponseEntity<Object> updateUserInfo(@RequestBody UpdateUser updateUser,
                               @ApiIgnore @RequestHeader("Authorization") String token) throws CustomRepsonses.MyNotFoundException {
        return userService.editUserInfo(updateUser, token);
    }

    @GetMapping("/api/user/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Object> getUserInfo(@PathVariable("id") long id,
                                      @ApiIgnore @RequestHeader("Authorization") String token){
        return userService.getUserInfo(id, token);
    }

    @PostMapping("/api/user/login")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ResponseStatus(code = HttpStatus.OK)
    public void login(@RequestBody LoginCredentials credentials){
    }

    @PostMapping("/api/user/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Data duplicated"),
            @ApiResponse(responseCode = "422", description = "Body payload is wrong")
    })
    public ResponseEntity<Object> register(@RequestBody RegisterCredentials registerCredentials){
        return userService.register(registerCredentials);
    }

    @GetMapping("/api/user/{id}/avatar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Object> getUserAvatar(@PathVariable("id") long id, HttpServletRequest request){

        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            String fileName = user.get().getAvatarPath();
            Resource resource = fileStorageService.loadFileAsResource(fileName);

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Could not determine file type.");
            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        else{
            String message = "User was not found";
            String path = "/api/user/" + id + "/avatar";
            return customFunctions.responseFromServer(HttpStatus.NOT_FOUND,path,message);
        }
    }

}
