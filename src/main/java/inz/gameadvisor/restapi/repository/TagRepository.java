package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.gameOriented.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findByLikeTags_userID(long id);
}
