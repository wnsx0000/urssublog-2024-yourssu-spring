package springproject.urssublog.exception;

import org.apache.logging.log4j.message.Message;

public class BlogUserNotFoundException extends BlogException {
    public BlogUserNotFoundException(String message) {
        super(message);
    }
}
