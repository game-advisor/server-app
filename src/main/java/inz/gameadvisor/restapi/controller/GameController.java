package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.service.AdminService;
import inz.gameadvisor.restapi.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/api/game/{name}")
    public ResponseEntity<Object> getGamesWithName(@PathVariable("name") String name,
                                                   HttpServletRequest request){
        return gameService.getGamesByName(name, request);
    }
}
