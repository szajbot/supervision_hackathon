package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class IcannReport {
    private String domain;
    private UrlRatio urlRatio;
    private long lifeSpan;
    private String registrationDate;
    private String expirationDate;

}