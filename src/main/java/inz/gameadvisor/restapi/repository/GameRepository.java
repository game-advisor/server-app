package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface GameRepository extends JpaRepository<Game,Long> {
    List<Game> findByNameContaining(String name);
    List<Game> findByLike_userID(long id);
    List<Game> findByPublishDateBetween(Date dateBegin, Date dateEnd);
    List<Game> findByCompany(Companies company);
    List<Game> findByGameTags_tagIDInAndCompany(List<Long> id,Companies company);
}
