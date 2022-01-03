package inz.gameadvisor.restapi;

import inz.gameadvisor.restapi.misc.CPUList;
import inz.gameadvisor.restapi.misc.FileStorageProperties;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.deviceOriented.CPU;
import inz.gameadvisor.restapi.repository.CPURepository;
import inz.gameadvisor.restapi.repository.CompaniesRepository;
import inz.gameadvisor.restapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties({FileStorageProperties.class})
@RequiredArgsConstructor
public class RestapiApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RestapiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

}
