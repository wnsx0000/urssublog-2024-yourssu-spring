package springproject.urssublog.exception.classes;

public abstract class BlogException extends RuntimeException {
    public BlogException(String message) {
        super(message);
    }
}
