package mapobjects.data;

import helpers.utils.Drawer.PictureDrawer.FILE_TYPE;
import mapobjects.traits.Equippable.RARITY;

public enum PlayerDefaults {

    Bob(false, FILE_TYPE.jpg, 50, MovingTypes.DEFAULT, HealthTypes.DEFAULT, RARITY.RARE),
    Mike(true, FILE_TYPE.png, 50, MovingTypes.FAST, HealthTypes.FRAGILE, RARITY.LEGENDARY),
    Sakura(true, FILE_TYPE.png, 40, MovingTypes.DEFAULT, HealthTypes.RECURRING, RARITY.EPIC);

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

    private final boolean animated;
    private final FILE_TYPE imageType;
    private final double side;
    private final double maxSpeed;
    private final double acceleration;
    private final double deceleration;
    private final int maxLives;
    private final double maxHP;
    private final double defence;
    private final RARITY rarity;

    PlayerDefaults(boolean animated, FILE_TYPE imageType, double side, MovingTypes movingTypes, HealthTypes healthTypes, RARITY rarity) {
        this.animated = animated;
        this.imageType = imageType;
        this.side = side;
        this.maxSpeed = movingTypes.maxSpeed;
        this.acceleration = movingTypes.acceleration;
        this.deceleration = movingTypes.deceleration;
        this.maxLives = healthTypes.maxLives;
        this.maxHP = healthTypes.maxHP;
        this.defence = healthTypes.defence;
        this.rarity = rarity;
    }

    public boolean isAnimated() {
        return animated;
    }

    public FILE_TYPE getImageType() {
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

    public RARITY getRarity() {
        return rarity;
    }
}
