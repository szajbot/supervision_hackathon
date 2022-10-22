package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class GeneralReport {
    private ValidatorReport validatorReport;
    private IcannReport icannReport;
    private VirustotalReport virustotalReport;
}
