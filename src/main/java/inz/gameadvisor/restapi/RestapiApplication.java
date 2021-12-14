package inz.gameadvisor.restapi;

import inz.gameadvisor.restapi.misc.FileStorageProperties;
import inz.gameadvisor.restapi.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableConfigurationProperties({FileStorageProperties.class})
public class RestapiApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RestapiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

}
