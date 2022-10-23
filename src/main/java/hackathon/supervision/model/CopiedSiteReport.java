package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class CopiedSiteReport {
    private String domain;
    private SimilarityScale result;
}
