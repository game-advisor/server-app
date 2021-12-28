package inz.gameadvisor.restapi.model.gameOriented;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class EditAddGame {
    private String name;
    private long companyID;
    private String imagePath;
    private Date publishDate;
}
