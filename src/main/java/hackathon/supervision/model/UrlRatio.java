package hackathon.supervision.model;

import lombok.Getter;
public enum UrlRatio {
    VERY_LOW(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VERY_HIGH(5);

    @Getter
    private final int ratio;

    UrlRatio(int ratio) {
        this.ratio = ratio;
    }
}