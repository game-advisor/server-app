package inz.gameadvisor.restapi.model.reviewOriented;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditAddReview {
    private String content;

    private float avgFPS;

    private int musicRating;

    private int graphicsRating;

    private int gameplayRating;
}
