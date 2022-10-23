package hackathon.supervision.model;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class VirustotalReport {
    private String domain;
    private ScamPossibility scamPossibility;
    private VirustotalSummary virustotalSummary;
//    private List<VirustotalEngineResponse> suspiciousResponses;
}
