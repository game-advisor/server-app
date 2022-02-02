package inz.gameadvisor.restapi.service;

import inz.gameadvisor.restapi.misc.CustomFunctions;
import inz.gameadvisor.restapi.misc.PublishDates;
import inz.gameadvisor.restapi.model.Companies;
import inz.gameadvisor.restapi.model.gameOriented.Game;
import inz.gameadvisor.restapi.model.gameOriented.GameAndTags;
import inz.gameadvisor.restapi.model.gameOriented.Tag;
import inz.gameadvisor.restapi.repository.CompaniesRepository;
import inz.gameadvisor.restapi.repository.GameRepository;
import inz.gameadvisor.restapi.repository.ReviewRepository;
import inz.gameadvisor.restapi.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService extends CustomFunctions {

    private final GameRepository gameRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;
    private final ReviewRepository reviewRepository;
    private final CompaniesRepository companiesRepository;

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

    public ResponseEntity<Object> getGamesByDatePublished(PublishDates publishDates, HttpServletRequest request){
        if(Objects.isNull(publishDates.getDateBegin()) || publishDates.getDateBegin().toString().isBlank()) {
            return responseFromServer(HttpStatus.BAD_REQUEST, request, BadRequestMessage);
        }
        if(Objects.isNull(publishDates.getDateEnd())){
            publishDates.setDateEnd(new Date(System.currentTimeMillis()));
        }
        List<Game> gameList = gameRepository.findByPublishDateBetween(publishDates.getDateBegin(), publishDates.getDateEnd());

        if(gameList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Games between specified dates not found");
        }
        return new ResponseEntity<>(gameList.toArray(),HttpStatus.OK);
    }

    public ResponseEntity<Object> getGamesByCompanyName(String companyName, HttpServletRequest request){
        if(Objects.isNull(companyName) || companyName.isBlank()){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,BadRequestMessage);
        }
        Optional<Companies> company = companiesRepository.findByNameContaining(companyName);
        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Company of given name not found");
        }
        List<Game> gameList = gameRepository.findByCompany(company.get());
        if(gameList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No games found for given company");
        }
        return new ResponseEntity<>(gameList.toArray(),HttpStatus.OK);
    }

    public ResponseEntity<Object> getGameTags(long gameID, HttpServletRequest request) {
        List<Tag> gameTags = tagRepository.findByGameHasTags_gameID(gameID);
        if (gameTags.isEmpty()) {
            return responseFromServer(HttpStatus.NOT_FOUND, request, "No tags found for this game");
        }
        return new ResponseEntity<>(gameTags.toArray(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Object> getGamesByTagsAndCompany(String tags, long companyID, HttpServletRequest request){
        String[] tagsSplit = tags.split(",");
        List<Tag> listOfTagsFromRequest = new ArrayList<>();
        for (String tag:
             tagsSplit) {
            Optional<Tag> tagOptional = tagRepository.findByName(tag);
            if(tagOptional.isEmpty()){
                return responseFromServer(HttpStatus.NOT_FOUND,request,"No tag called: " + tag +" found");
            }
            else{
                listOfTagsFromRequest.add(tagOptional.get());
            }
        }
        Optional<Companies> company = companiesRepository.findById(companyID);
        if(company.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"Company of given ID was not found");
        }
        if(company.get().getIsGameDev() == 0){
            return responseFromServer(HttpStatus.BAD_REQUEST,request,"Company is not a game developer");
        }
        List<Game> gameList = gameRepository.findByCompany(company.get());
        if(gameList.isEmpty()){
            return responseFromServer(HttpStatus.NOT_FOUND,request,"No games found for that company");
        }
        List<GameAndTags> gameAndTagsList = new ArrayList<>();
        for (Game game:
             gameList) {
            GameAndTags gameAndTags = new GameAndTags();
            List<Tag> currentGameTags = new ArrayList<>(game.getGameTags());
            currentGameTags.sort(Comparator.comparing(Tag::getTagID));
            if(currentGameTags.stream().anyMatch(listOfTagsFromRequest::contains)){
                gameAndTags.setTags(currentGameTags);
            }
            else
                return responseFromServer(HttpStatus.NOT_FOUND,request,"No game found for given tags");
            gameAndTags.setGame(game);
            gameAndTagsList.add(gameAndTags);
        }
        return new ResponseEntity<>(gameAndTagsList.toArray(),HttpStatus.OK);
    }

    public ResponseEntity<Object> gameRecommend(String token, HttpServletRequest request) {
        long userID = getUserIDFromToken(token);
        return null;
    }
}
