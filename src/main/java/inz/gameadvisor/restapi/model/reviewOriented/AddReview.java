package inz.gameadvisor.restapi.model.reviewOriented;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class AddReview {
    private String content;

    private float avgFPS;

    private int musicRating;

    private int graphicsRating;

    private int gameplayRating;
}
