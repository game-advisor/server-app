package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.repository.CompaniesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompaniesService extends CustomFunctions {

    private final CompaniesRepository companiesRepository;

    public ResponseEntity<Object> getCompanyInfoByID(long id, HttpServletRequest request){
        Optional<Companies> company = companiesRepository.findById(id);

        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }
        return new ResponseEntity<>(company,HttpStatus.OK);
    }

    public ResponseEntity<Object> getCompanyInfoByName(String name, HttpServletRequest request){
        Optional<Companies> company = companiesRepository.findByName(name);

        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,NoCompanyFoundMessage);
        }
        return new ResponseEntity<>(company,HttpStatus.OK);
    }
}
