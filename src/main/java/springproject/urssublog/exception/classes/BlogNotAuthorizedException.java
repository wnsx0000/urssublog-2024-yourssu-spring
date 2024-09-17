package springproject.urssublog.exception.classes;

public class BlogNotAuthorizedException extends BlogException {
    public BlogNotAuthorizedException(String message) {
        super(message);
    }
}
