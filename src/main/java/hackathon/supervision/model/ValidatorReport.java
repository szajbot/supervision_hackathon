package hackathon.supervision.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class ValidatorReport {
    private String domain;
    private boolean isHttps;
    private boolean isUntypicalNumsInDomain;
    private boolean isCommonTopDomain;
    private int numberOfSpecialChar;
    private int numberOfDigits;
    private int domainLength;
}
