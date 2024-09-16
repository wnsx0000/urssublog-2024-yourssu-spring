package springproject.urssublog.exception;

public class BlogNotAuthorizedException extends BlogException {
    public BlogNotAuthorizedException(String message) {
        super(message);
    }
}
