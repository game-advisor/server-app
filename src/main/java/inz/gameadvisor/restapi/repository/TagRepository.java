package inz.gameadvisor.restapi.repository;

import inz.gameadvisor.restapi.model.gameOriented.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findByLikeTags_userID(long id);
    List<Tag> findByGameHasTags_gameID(long id);
    Optional<Tag> findByName(String name);
}
