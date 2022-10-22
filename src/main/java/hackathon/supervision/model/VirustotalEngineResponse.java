package hackathon.supervision.model;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VirustotalEngineResponse implements Serializable {
    private String category;
    private String result;
    private String method;
    private String engine_name;

}
