package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.gameOriented.Tag;
import inz.gameadvisor.restapi.repository.GameRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService extends CustomFunctions {

    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;
    private final ReviewRepository reviewRepository;

    public ResponseEntity<Object> getGamesByName(String name, HttpServletRequest request){
        if(name.isBlank()){
            return responseFromServer(HttpStatus.UNPROCESSABLE_ENTITY,request,"Bad data payload");
        }
        List<Game> gameList = gameRepository.findByNameContaining(name);

        if(gameList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No games found");
        }
        else{
            return new ResponseEntity<>(gameList.stream().toArray(),HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> getReviewCountByGameID(long id, HttpServletRequest request) {
        Optional<Game> game = gameRepository.findById(id);
        if(game.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game not found");
        }
        if(reviewRepository.countAllBygame(game.get()) == 0)
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Not found");
        return new ResponseEntity<>(reviewRepository.countAllBygame(game.get()), HttpStatus.OK);
    }

    public ResponseEntity<Object> getGameThumbnail(long id, HttpServletRequest request){
        Optional<Game> game = gameRepository.findById(id);

        if(game.isPresent()){
            String fileName = game.get().getImagePath();
            Resource resource = fileStorageService.loadFileAsResource(fileName);

            if(resource == null){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"File not found");
            }

            // Try to determine file's content type
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Could not determine file type.");
            }

            // Fallback to the default content type if type could not be determined
            if(contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }
        else{
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Game was not found");
        }
    }

    public ResponseEntity<Object> listAllTags(HttpServletRequest request){
        List<Tag> tagList = tagRepository.findAll();
        if(tagList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No tags found");
        }
        return new ResponseEntity<>(tagList,HttpStatus.OK);
    }


}
