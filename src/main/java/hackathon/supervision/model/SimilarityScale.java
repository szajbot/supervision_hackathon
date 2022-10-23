package hackathon.supervision.model;

import lombok.Getter;

public enum SimilarityScale {
    CONFIDENT(100),
    HIGH(75),
    MEDIUM(50),
    LOW(0);

    @Getter
    private final int grade;

    SimilarityScale(int grade) {
        this.grade = grade;
    }
}
