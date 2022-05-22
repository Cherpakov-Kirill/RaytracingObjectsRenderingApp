package nsu.graphics.fifthlab.render;

public enum Quality {
    ROUGH,
    NORMAL,
    FINE;

    public static Quality getQuality(String quality) {
        switch (quality) {
            case "rough" -> {
                return ROUGH;
            }
            case "normal" -> {
                return NORMAL;
            }
            case "fine" -> {
                return FINE;
            }
        }
        return null;
    }
}
