package pe.com.practicar.business.model;

import lombok.Getter;

@Getter
public enum RiskLevel {
    LOW(1, 3),
    MEDIUM(4, 6),
    HIGH(7, 10);

    private final int minValue;
    private final int maxValue;

    RiskLevel(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}
