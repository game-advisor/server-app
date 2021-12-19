package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class GameController {

    private final AdminService adminService;

    @GetMapping("/api/game/{name}")
    public ResponseEntity<Object> getGamesWithName(@PathVariable("name") String name){
        return adminService.getGamesWithName(name);
    }
}
