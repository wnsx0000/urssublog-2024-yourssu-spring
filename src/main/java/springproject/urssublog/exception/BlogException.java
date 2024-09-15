package springproject.urssublog.exception;

public abstract class BlogException extends RuntimeException {
    public BlogException(String message) {
        super(message);
    }
}
