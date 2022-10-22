package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VirustotalSummary {
    private int harmless;
    private int malicious;
    private int suspicious;
    private int undetected;
    private int timeout;
}
