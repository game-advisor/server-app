package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.PublishDates;
import inz.gameadvisor.restapi.model.gameOriented.EditAddGame;
import inz.gameadvisor.restapi.service.AdminService;
import inz.gameadvisor.restapi.service.DevicesService;
import inz.gameadvisor.restapi.service.GameService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final AdminService adminService;
    private final DevicesService devicesService;

    @GetMapping("/api/game/{game_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Object> getGamesWithName(@PathVariable("game_name") String name,
                                                   HttpServletRequest request){
        return gameService.getGamesByName(name, request);
    }

    @GetMapping("/api/games/datePublished")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Games not found")
    })
    public ResponseEntity<Object> getGamesByDatePublished(@RequestBody PublishDates publishDates,
                                                          HttpServletRequest request){
        return gameService.getGamesByDatePublished(publishDates, request);
    }

    @GetMapping("/api/games/{company_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Games not found")
    })
    public ResponseEntity<Object> getGamesByCompanyName(@PathVariable("company_name")String companyName,
                                                          HttpServletRequest request){
        return gameService.getGamesByCompanyName(companyName, request);
    }

    @GetMapping("/api/tags/{game_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tags not found")
    })
    public ResponseEntity<Object> getGameTags(@PathVariable("game_id")long gameID,
                                                        HttpServletRequest request){
        return gameService.getGameTags(gameID, request);
    }

    @GetMapping("/api/game/getByTagsAndCompany/{company_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400",description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Tags not found")
    })
    public ResponseEntity<Object> getGamesByTagsAndCompany(@RequestParam String tags,
                                                           @PathVariable("company_id") long companyID,
                                                           HttpServletRequest request){
        return gameService.getGamesByTagsAndCompany(tags, companyID, request);
    }

    @GetMapping("/api/game/{game_id}/review/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Object> getReviewCountByGameID(@PathVariable("game_id") long id,
                                                         HttpServletRequest request){
        return gameService.getReviewCountByGameID(id,request);
    }

    @GetMapping("/api/game/{game_id}/thumbnail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Game not found")
    })
    public ResponseEntity<Object> getGameThumbnail(@PathVariable("game_id") long id, HttpServletRequest request){
        return gameService.getGameThumbnail(id,request);
    }

    @PostMapping("/api/admin/game/add")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401",description = "Unauthorized")
    })
    public ResponseEntity<Object> addGame(@RequestBody EditAddGame addGame,
                                          HttpServletRequest request,
                                          @ApiIgnore @RequestHeader("Authorization") String token){
        return adminService.addGame(addGame,request,token);
    }

    @GetMapping("/api/tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No tag found")
    })
    public ResponseEntity<Object> listAllTags(HttpServletRequest request){
        return gameService.listAllTags(request);
    }

    @PostMapping("/api/gameRequirementsCompare/{game_ID}/{user_deviceID}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No tag found")
    })
    public ResponseEntity<Object> compareDeviceWithGameRequirements(@PathVariable("game_ID") long gameID,
                                                                    @PathVariable("user_deviceID") long deviceID,
                                                                    HttpServletRequest request){
        return devicesService.compareDeviceWithGameRequirements(deviceID,gameID,request);
    }
}
