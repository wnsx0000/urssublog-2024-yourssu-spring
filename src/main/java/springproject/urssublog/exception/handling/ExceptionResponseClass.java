package springproject.urssublog.exception.handling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseClass {
    private LocalDateTime time;
    private String status;
    private String message;
    private String requestURI;

    @Override
    public String toString() {
        return "-----------------------------------------------\n" +
                "time : " + time.toString() + "\nstatus : " + status
                + "\nmessage : " + message + "\nrequestURI : " + requestURI +
                "\n-----------------------------------------------";
    }
}
