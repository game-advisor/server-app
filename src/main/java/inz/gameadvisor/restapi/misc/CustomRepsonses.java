package inz.gameadvisor.restapi.misc;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

public class CustomRepsonses {
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public static class MyDataConflict extends DataIntegrityViolationException {
        public MyDataConflict(String message){
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public static class MyForbiddenAccess extends IllegalAccessException{
        public MyForbiddenAccess(String message){
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class MyNotFoundException extends NoSuchElementException {
        public MyNotFoundException(String message)
        {
            super(message);
        }
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public static class MyBadRequestException extends NullPointerException{
        public MyBadRequestException(String message){super(message);}
    }
}
