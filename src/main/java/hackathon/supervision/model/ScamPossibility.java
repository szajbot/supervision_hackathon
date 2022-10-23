package hackathon.supervision.model;

import lombok.Getter;
import lombok.Setter;

public enum ScamPossibility {
    VERY_LOW(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VERY_HIGH(5);

    @Getter @Setter
    private int ratio;

    ScamPossibility(int ratio) {
        this.ratio = ratio;
    }
}