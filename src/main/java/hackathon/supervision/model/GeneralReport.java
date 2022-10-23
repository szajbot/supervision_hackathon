package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class GeneralReport {
    private SimilarityModuleReport similarityModuleReport;
    private CopiedSiteReport chanceToBeCopied;
    private int value;
    private ValidatorReport validatorReport;
    private IcannReport icannReport;
    private VirustotalReport virustotalReport;
}
