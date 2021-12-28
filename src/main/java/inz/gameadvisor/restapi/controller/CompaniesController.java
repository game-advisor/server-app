package inz.gameadvisor.restapi.controller;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.service.CompaniesService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CompaniesController extends CustomFunctions {

    private final CompaniesService companiesService;

    @GetMapping("/api/company/findByID/{company_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<Object> getCompanyInfoByID(@PathVariable("company_id") long id,
                                                 HttpServletRequest request){
        return companiesService.getCompanyInfoByID(id,request);
    }

    @GetMapping("/api/company/findByName/{company_name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    public ResponseEntity<Object> getCompanyInfoByName(@PathVariable("company_name") String name,
                                                 HttpServletRequest request){
        return companiesService.getCompanyInfoByName(name,request);
    }
}
