package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.model.Authorities;
import inz.gameadvisor.restapi.repository.AuthoritiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthoritiesController {

    private final AuthoritiesRepository authoritiesRepository;

    @GetMapping("/authorities")
    private List<Authorities> getAllAuthorities()
    {
        return authoritiesRepository.findAll();
    }
}
