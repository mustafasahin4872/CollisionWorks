package helpers;

public enum PlayerData {
    Bob(false, 50, "jpg"),
    Mike(true, 50, "png"),
    Sakura(true, 40, "png");
    
    private final boolean animated;
    private final double defaultSide;
    private final String imageType;

    PlayerData(boolean animated, double defaultSide, String imageType) {
        this.animated = animated;
        this.defaultSide = defaultSide;
        this.imageType = imageType;
    }

    public boolean isAnimated() {
        return animated;
    }

    public double getDefaultSide() {
        return defaultSide;
    }

    public String getImageType() {
        return imageType;
    }

}
