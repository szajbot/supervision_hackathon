package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class SimilarityModuleReport {
    private String domain;
    private ScamPossibility scamPossibility;
    private int numOfSimilarities;
    private SimilarityScale verifiedDatabaseGrade;
    private SimilarityScale reportedDatabaseGrade;
}
