package springproject.urssublog.dto.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseDto {
    private String time;
    private String status;
    private String message;
    private String requestURI;

    @Override
    public String toString() {
        return "-----------------------------------------------\n" +
                "time : " + time + "\nstatus : " + status
                + "\nmessage : " + message + "\nrequestURI : " + requestURI +
                "\n-----------------------------------------------";
    }
}
