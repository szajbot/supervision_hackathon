package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class RaportResult {
    private String domain;
    private Integer value;
}
