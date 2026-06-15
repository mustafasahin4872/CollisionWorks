package helpers;

public enum PlayerDefaults {

    Bob(false, "jpg", 50, MovingTypes.DEFAULT, HealthTypes.DEFAULT, GunTypes.DEFAULT),
    Mike(true, "png", 50, MovingTypes.FAST, HealthTypes.FRAGILE, GunTypes.UZI),
    Sakura(true, "png", 40, MovingTypes.DEFAULT, HealthTypes.RECURRING, GunTypes.PACIFIST);

    private enum MovingTypes {

        DEFAULT(15, 0.8, 0.8),
        FAST(20, 0.8, 0.8),
        OLD(10, 0.5, 0.5),
        SLIPPY(18, 0.8, 0.1),
        RACE_CAR(30, 2, 2)

        ;
        private final double maxSpeed;
        private final double acceleration;
        private final double deceleration;

        MovingTypes(double maxSpeed, double accelaration, double decelaration) {
            this.maxSpeed = maxSpeed;
            this.acceleration = accelaration;
            this.deceleration = decelaration;
        }

        public double getMaxSpeed() {
            return maxSpeed;
        }

        public double getAcceleration() {
            return acceleration;
        }

        public double getDeceleration() {
            return deceleration;
        }

    }

    private enum HealthTypes {

        DEFAULT(3, 200, 5),
        STURDY(3, 300, 20),
        RECURRING(5, 150, 0),
        FRAGILE(3, 100, 0),
        UNDIEABLE(3, 1000, 100),
        CAT(9, 100, 5),
        HARDCORE(1, 200, 5),
        CHALLANGER(1, 1, 0)

        ;

        private final int maxLives;
        private final double maxHP;
        private final double defence;

        HealthTypes(int maxLives, double maxHP, double defence) {
            this.maxLives = maxLives;
            this.maxHP = maxHP;
            this.defence = defence;
        }

        public int getMaxLives() {
            return maxLives;
        }

        public double getMaxHP() {
            return maxHP;
        }

        public double getDefence() {
            return defence;
        }

    }

    private enum GunTypes {

        DEFAULT(5, 1000, 300, 20), // reload once per second
        UZI(10, 400, 100, 10),
        SHOTGUN(3, 2000, 600, 60),
        PACIFIST(0, 1000, 1000, 0) // does not have ammo

        ;

        private final int maxAmmo;
        private final int reloadTime; // in milliseconds
        private final int cooldown;
        private final double damagePerAmmo;

        GunTypes(int maxAmmo, int reloadTime, int cooldown, double damagePerAmmo) {
            this.maxAmmo = maxAmmo;
            this.reloadTime = reloadTime;
            this.cooldown = cooldown;
            this.damagePerAmmo = damagePerAmmo;
        }

    }

    private final boolean animated;
    private final String imageType;
    private final double side;
    private final double maxSpeed;
    private final double acceleration;
    private final double deceleration;
    private final int maxLives;
    private final double maxHP;
    private final double defence;
    private final int maxAmmo;
    private final int reloadTime;
    private final int shootCooldown;
    private final double damagePerAmmo;

    PlayerDefaults(boolean animated, String imageType, double side, MovingTypes movingTypes, HealthTypes healthTypes, GunTypes gunTypes) {
        this.animated = animated;
        this.imageType = imageType;
        this.side = side;
        this.maxSpeed = movingTypes.maxSpeed;
        this.acceleration = movingTypes.acceleration;
        this.deceleration = movingTypes.deceleration;
        this.maxLives = healthTypes.maxLives;
        this.maxHP = healthTypes.maxHP;
        this.defence = healthTypes.defence;
        this.maxAmmo = gunTypes.maxAmmo;
        this.reloadTime = gunTypes.reloadTime;
        this.damagePerAmmo = gunTypes.damagePerAmmo;
        this.shootCooldown = gunTypes.cooldown;
    }

    public boolean isAnimated() {
        return animated;
    }

    public String getImageType() {
        return imageType;
    }

    public double getSide() {
        return side;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public double getDefence() {
        return defence;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public int getReloadTime() {
        return reloadTime;
    }

    public int getShootCooldown() {
        return shootCooldown;
    }

    public double getDamagePerAmmo() {
        return damagePerAmmo;
    }

}
