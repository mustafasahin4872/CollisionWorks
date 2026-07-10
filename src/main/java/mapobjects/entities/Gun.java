package mapobjects.entities;

import mapobjects.factories.Blueprint;
import mapobjects.traits.Equippable;
import mapobjects.traits.Generator;
import mapobjects.traits.HealthBearer;
import mapobjects.traits.MapObject;
import mapobjects.components.Spawner;
import mapobjects.components.Timer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static mapobjects.traits.GridObject.TILE_SIDE;

import mapobjects.factories.ProjectileBlueprint;
import static mapobjects.entities.Projectile.ProjectileType;

public abstract class Gun extends Equippable implements Generator {

    private static final int MAX_LEVEL = 5;
    private static final int UPGRADABLE_NUM = 3;

    protected final ProjectileType projectileType;
    protected ProjectileBlueprint projectileBlueprint;

    private final int defaultMaxAmmo;
    private final int defaultReloadTime;
    private final int defaultUnloadTime;

    private int maxAmmo;
    private Timer reloadTimer;
    private Timer unloadTimer;

    private final int[] levels = new int[3];
    private int ammo;
    protected double direction;

    protected double[] centerCoordinates;
    protected double[] nextCenterCoordinates;

    protected final Spawner spawner;
    protected final Set<Projectile> projectiles = new HashSet<>();
    protected Set<HealthBearer> targets;

    protected Gun(ProjectileType projectileType, int maxAmmo, int reloadTime, int unloadTime, RARITY rarity) {
        super(0, 0, 0, TILE_SIDE, TILE_SIDE, rarity);
        setName(getClass().getName().split("\\$")[1].toLowerCase(Locale.ROOT));
        this.projectileType = projectileType;
        this.projectileBlueprint = new ProjectileBlueprint(projectileType);

        this.defaultMaxAmmo = maxAmmo;
        this.defaultReloadTime = reloadTime;
        this.defaultUnloadTime = unloadTime;

        this.maxAmmo = maxAmmo;
        ammo = maxAmmo;
        reloadTimer = new Timer(0, reloadTime);
        unloadTimer = new Timer(0, unloadTime);

        spawner = new Spawner(worldIndex);
    }

    public void setTargets(Set<HealthBearer> targets) {
        this.targets = targets;
    }

    @Override
    public void call(Player player) {
        double xVelocity = player.getXVelocity();
        double yVelocity = player.getYVelocity();
        centerCoordinates = player.getCenterCoordinates();
        nextCenterCoordinates = player.getNextCenterCoordinates();
        direction = (Math.toDegrees(Math.atan2(yVelocity, xVelocity)) + 360) % 360;

        if (!reloadTimer.isCompleted()) {
            reloadTimer.activate();
        }

        if (reloadTimer.isActive()) {
            reload();
        }

        reloadTimer.tick();
        unloadTimer.tick();

        projectiles.removeIf(Projectile::isExpired);
    }

    void shoot() {
        if (ammo > 0 && !unloadTimer.isCompleted()) {
            unloadTimer.activate();
            reloadTimer.startCooldown();
            spawn();
            ammo--;
        }
    }

    private void reload() {
        if (ammo + 1 <= maxAmmo) {
            ammo++;
        }
    }

    public int getAmmo() {
        return ammo;
    }

    public Set<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public abstract void spawn();


    public boolean levelUp(int index) {
        boolean upgraded = upgrade(index);
        if (upgraded) {
            levels[index]++;
        }
        return upgraded;
    }

    public boolean upgrade(int index) {
        if (index<0 || index>=UPGRADABLE_NUM) {
            System.out.println("LEVEL INDEX INVALID: " + index + "\nMUST BE IN BETWEEN 0 AND " + UPGRADABLE_NUM);
            return false;
        }
        if (levels[index] == MAX_LEVEL) {
            // silently fall back
            return false;
        }
        if (index == 0) {
            upgrade1(levels[0]);
        } else if (index == 1) {
            upgrade2(levels[1]);
        } else {
            upgrade3(levels[2]);
        }
        return true;
    }

    /// in the case of debuffs, we need to restore the previous state
    /// reset builder and re-apply all upgrades
    public void restoreUpgrades() {
        projectileBlueprint = new ProjectileBlueprint(projectileType);
        for (int i = 0; i<UPGRADABLE_NUM; i++) {
            for (int j = 0; j<levels[i]; j++) {
                upgrade(i);
            }
        }
    }


    public abstract void upgrade1(int level);
    public abstract void upgrade2(int level);
    public abstract void upgrade3(int level);


    protected void reloadBuff(double buff) {
        int rt = (int) (defaultReloadTime * (1 - buff));
        this.reloadTimer = new Timer(0, rt);
    }

    protected void unloadBuff(double buff) {
        int ut = (int) (defaultUnloadTime * (1 - buff));
        this.unloadTimer = new Timer(0, ut);
    }

    protected void ammoBuff(int buff) {
        this.maxAmmo = defaultMaxAmmo + buff;
    }

    protected void damageBuff(double buff) {
        projectileBlueprint.setDamage(projectileType.getDamage() * (1+ buff));
    }

    protected void rangeBuff(double buff) {
        projectileBlueprint.setRange(projectileType.getRange() * (1 + buff));
    }

    protected void speedBuff(double buff) {
        projectileBlueprint.setSpeed(projectileType.getSpeed() * (1 + buff));
    }


    /// The default weapon
    /// Upgrades Types: reload speed, damage, ammo
    public static class Handgun extends Gun {

        private static final int MAX_AMMO = 5;
        private static final int RELOAD_TIME = 1000;
        private static final int UNLOAD_TIME = 300;

        private static final double RELOAD_BUFF = 0.1; // 10 percent each upgrade
        private static final double DAMAGE_BUFF = 0.1;
        private static final int AMMO_BUFF = MAX_AMMO / 5; // 20 percent each upgrade

        public Handgun() {
            super(ProjectileType.REGULAR, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.RARE);
        }

        @Override
        public void spawn() {
            Blueprint blueprint = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30);

            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, direction);

            projectile.setTargets(targets);
            projectiles.add(projectile);
        }

        @Override
        public void upgrade1(int level) {
            reloadBuff(level * RELOAD_BUFF);
        }

        @Override
        public void upgrade2(int level) {
            damageBuff(level * DAMAGE_BUFF);
        }

        @Override
        public void upgrade3(int level) {
            ammoBuff(level * AMMO_BUFF);
        }

    }

    /// High reload-unload-ammo, low damage, small bullets and small spread
    /// Upgrade Types: reload-unload speed, ammo, spread increase
    public static class Uzi extends Gun {

        private static final double SPREAD = 2.5;

        private static final int MAX_AMMO = 10;
        private static final int RELOAD_TIME = 400;
        private static final int UNLOAD_TIME = 100;

        private static final double RELOAD_BUFF = 0.08; // 8 percent
        private static final double UNLOAD_BUFF = 0.02; // 2 percent
        private static final int AMMO_BUFF = MAX_AMMO / 5; // 20 percent each upgrade

        private static final int[] SHIFTS_LEVEL_1 = new int[]{0};
        private static final int[] SHIFTS_LEVEL_2 = new int[]{0, 1, -1};
        private static final int[] SHIFTS_LEVEL_3 = new int[]{0, 2, -1, 1, -2};
        private static final int[] SHIFTS_LEVEL_4 = new int[]{0, 3, -1, 2, -2, 1, -3};
        private static final int[] SHIFTS_LEVEL_5 = new int[]{0, 4, -1, 3, -2, 2, -3, 1, -4};
        private static final int[][] SHIFT_LEVELS = {SHIFTS_LEVEL_1, SHIFTS_LEVEL_2, SHIFTS_LEVEL_3, SHIFTS_LEVEL_4, SHIFTS_LEVEL_5};

        private int[] shifts = SHIFTS_LEVEL_1;
        private int shiftIndex = 0;

        public Uzi() {
            super(ProjectileType.SMALL, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.RARE);
        }

        @Override
        public void spawn() {
            Blueprint blueprint = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30);

            double diff = shifts[shiftIndex] * SPREAD;
            shiftIndex = (shiftIndex +1) % shifts.length;

            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, direction + diff);
            projectile.setTargets(targets);
            projectiles.add(projectile);
        }

        @Override
        public void upgrade1(int level) {
            reloadBuff(level * RELOAD_BUFF);
            unloadBuff(level * UNLOAD_BUFF);
        }

        @Override
        public void upgrade2(int level) {
            ammoBuff(level * AMMO_BUFF);
        }

        @Override
        public void upgrade3(int level) {
            shifts = SHIFT_LEVELS[level-1];
        }

    }

    /// Very slow reload-unload speed, high damage, low range
    /// Upgrade Types: ammo or reload, range, projectile count + lower spread
    public static class Shotgun extends Gun {

        private static final int PROJECTILE_COUNT = 3;
        private static final double INITIAL_SPREAD = 20;

        private static final int MAX_AMMO = 3;
        private static final int RELOAD_TIME = 1500;
        private static final int UNLOAD_TIME = 700;

        private static final int AMMO_BUFF = 1;
        private static final double RELOAD_BUFF = 0.1;
        private static final double RANGE_BUFF = 0.1;
        private static final int PROJECTILE_BUFF = 2;
        private static final double SPREAD_BUFF = 0.15;

        private int projectileCount = PROJECTILE_COUNT;
        private double spread = INITIAL_SPREAD;
        private double coneWidth = INITIAL_SPREAD * (projectileCount - 1);

        public Shotgun() {
            super(ProjectileType.SHOTGUN, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.EPIC);
        }

        @Override
        public void spawn() {
            Blueprint[] blueprints = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30, projectileCount, coneWidth);

            double d = direction;

            if (projectileCount % 2 == 0) {
                d += spread / 2;
            }

            for (int i = 0; i < projectileCount; i++) {
                Blueprint blueprint = blueprints[i];
                int mult = i - projectileCount / 2;
                double diff = spread * mult;

                Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, d + diff);

                projectile.setTargets(targets);
                projectiles.add(projectile);
            }
        }

        @Override
        public void upgrade1(int level) {
            if (level % 2 == 0) {
                int buffNum = level / 2;
                ammoBuff(buffNum * AMMO_BUFF);
            } else {
                int buffNum = (level+1) / 2;
                reloadBuff(buffNum * RELOAD_BUFF);
            }
        }

        @Override
        public void upgrade2(int level) {
            rangeBuff(level * RANGE_BUFF);
        }

        @Override
        public void upgrade3(int level) {
            projectileCount = PROJECTILE_COUNT + PROJECTILE_BUFF * level;
            spread = INITIAL_SPREAD * (1 - level * SPREAD_BUFF);
            coneWidth = spread * (projectileCount - 1);
        }

    }

    /// High unload speed, spawns projectiles with spread and coneWidth aspects
    /// Upgrade Types: higher reload speed, smaller shifts, projectile count + lower spread
    public static class MachineGun extends Gun {

        private static final int PROJECTILE_COUNT = 3;
        private static final double INITIAL_SPREAD = 20;
        private static final double INITIAL_INNER_WIDTH = INITIAL_SPREAD * (PROJECTILE_COUNT - 1);
        private static final double INITIAL_OUTER_WIDTH = 80;
        private static final double FINAL_INNER_WIDTH = 48;
        private static final double FINAL_OUTER_WIDTH = 55;

        private static final int MAX_AMMO = 10;
        private static final int RELOAD_TIME = 400;
        private static final int UNLOAD_TIME = 100;

        private static final double RELOAD_BUFF = 0.1;
        private static final double OUTER_CONE_BUFF = 0.2 * (1-(INITIAL_OUTER_WIDTH-FINAL_OUTER_WIDTH)/INITIAL_INNER_WIDTH);
        private static final double INNER_CONE_BUFF = 0.2 * (1-(FINAL_INNER_WIDTH-INITIAL_INNER_WIDTH)/INITIAL_INNER_WIDTH);
        private static final int PROJECTILE_BUFF = 2;

        private double spread = INITIAL_SPREAD;
        private int projectileCount = PROJECTILE_COUNT;
        private double outerConeWidth = INITIAL_OUTER_WIDTH;
        private double innerConeWidth = INITIAL_SPREAD * (projectileCount - 1);

        private static final int[] shifts = new int[]{0, 2, -1, 1, -2};
        private int shiftIndex;

        public MachineGun() {
            super(ProjectileType.SMALL, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.EPIC);
        }

        @Override
        public void spawn() {
            Blueprint[] blueprints = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30, projectileCount, innerConeWidth);

            double opening = (outerConeWidth - innerConeWidth) / 2;
            double shift = shifts[shiftIndex] * opening;
            shiftIndex = (shiftIndex +1) % shifts.length;

            double d = direction;

            if (projectileCount % 2 == 0) {
                d += spread / 2;
            }

            for (int i = 0; i < projectileCount; i++) {
                Blueprint blueprint = blueprints[i];
                int mult = i - projectileCount / 2;
                double diff = spread * mult;

                Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, d + diff + shift);

                projectile.setTargets(targets);
                projectiles.add(projectile);
            }
        }

        @Override
        public void upgrade1(int level) {
            reloadBuff(level * RELOAD_BUFF);
        }

        @Override
        public void upgrade2(int level) {
            outerConeWidth = INITIAL_OUTER_WIDTH * (1 - level * OUTER_CONE_BUFF);
        }

        @Override
        public void upgrade3(int level) {
            projectileCount = PROJECTILE_COUNT + level * PROJECTILE_BUFF;
            spread = INITIAL_SPREAD * (1 - level * INNER_CONE_BUFF);
            innerConeWidth = spread * (projectileCount - 1);
        }

    }

    /// High damage, slow, homing projectiles
    /// Upgrade Types: faster speed + range, faster reload or projectile number, better tracking
    public static class Staff extends Gun {

        private static final int PROJECTILE_COUNT = 1;
        private static final double SPREAD = 30;

        private static final int MAX_AMMO = 4;
        private static final int RELOAD_TIME = 1000;
        private static final int UNLOAD_TIME = 1000;

        private static final double SPEED_BUFF = 0.05;
        private static final double RANGE_BUFF = 0.05;
        private static final int PROJECTILE_BUFF = 1;
        private static final double RELOAD_BUFF = 0.1;
        private static final double INERTIA_BUFF = 0.1;


        private int projectileCount = PROJECTILE_COUNT;
        private double coneWidth;

        public Staff() {
            super(ProjectileType.HOMING, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.MYTHIC);
        }

        @Override
        public void spawn() {
            Blueprint[] blueprints = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30, projectileCount, coneWidth);

            double d = direction;

            if (projectileCount % 2 == 0) {
                d += SPREAD / 2;
            }

            for (int i = 0; i < projectileCount; i++) {
                Blueprint blueprint = blueprints[i];
                int mult = i - projectileCount / 2;
                double diff = SPREAD * mult;

                Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, d + diff);

                projectile.setTargets(targets);
                projectiles.add(projectile);
            }
        }

        @Override
        public void upgrade1(int level) {
            speedBuff(level * SPEED_BUFF);
            rangeBuff(level * RANGE_BUFF);
        }

        @Override
        public void upgrade2(int level) {
            if (level % 2 == 0) {
                int buffNum = level / 2;
                projectileCount = PROJECTILE_COUNT + buffNum * PROJECTILE_BUFF;
                coneWidth = (projectileCount - 1) * SPREAD;
            } else {
                int buffNum = (level + 1) / 2;
                reloadBuff(buffNum * RELOAD_BUFF);
            }
        }

        @Override
        public void upgrade3(int level) {
            projectileBlueprint.setInertia(projectileType.getInertia() * (1 + level * INERTIA_BUFF));
        }

    }

    /// Spawns a giant rocket - slow
    /// Upgrade types: damage, reload, bullet speed
    public static class Launcher extends Gun {

        private static final int MAX_AMMO = 1;
        private static final int RELOAD_TIME = 2000;
        private static final int UNLOAD_TIME = 0;

        private static final double DAMAGE_BUFF = 0.1;
        private static final double RELOAD_BUFF = 0.1;
        private static final double SPEED_BUFF = 0.1;

        public Launcher() {
            super(ProjectileType.LAUNCHER, MAX_AMMO, RELOAD_TIME, UNLOAD_TIME, RARITY.LEGENDARY);
        }

        @Override
        public void spawn() {
            Blueprint blueprint = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30);

            Projectile projectile = blueprint.mutateToProjectile(projectileBlueprint, direction);

            projectile.setTargets(targets);
            projectiles.add(projectile);
        }

        @Override
        public void upgrade1(int level) {
            damageBuff(level * DAMAGE_BUFF);
        }

        @Override
        public void upgrade2(int level) {
            reloadBuff(level * RELOAD_BUFF);
        }

        @Override
        public void upgrade3(int level) {
            speedBuff(level * SPEED_BUFF);
        }

    }

    /// CHALLENGE: NO AMMO!
    public static class Pacifist extends Gun {

        public Pacifist() {
            super(ProjectileType.REGULAR, 0, 1000, 1000, RARITY.RARE);
        }

        @Override
        public void spawn() {

        }

        @Override
        public void upgrade1(int level) {

        }

        @Override
        public void upgrade2(int level) {

        }

        @Override
        public void upgrade3(int level) {

        }

    }


    @Override
    public String[] getStats() {

        String reloadCategory;
        if (defaultReloadTime < 300) {
            reloadCategory = "very fast";
        } else if (defaultReloadTime < 600) {
            reloadCategory = "fast";
        } else if (defaultReloadTime < 1000) {
            reloadCategory = "normal";
        } else if (defaultReloadTime < 1500) {
            reloadCategory = "slow";
        } else {
            reloadCategory = "very slow";
        }


        String unloadCategory;
        if (defaultUnloadTime <= 100) {
            unloadCategory = "very fast";
        } else if (defaultUnloadTime <= 300) {
            unloadCategory = "fast";
        } else if (defaultUnloadTime <= 600) {
            unloadCategory = "normal";
        } else if (defaultUnloadTime <= 1000) {
            unloadCategory = "slow";
        } else {
            unloadCategory = "very slow";
        }


        double speed = projectileType.getSpeed();
        String speedCategory;
        if (speed < 5) {
            speedCategory = "very slow";
        } else if (speed < 10) {
            speedCategory = "slow";
        } else if (speed < 15) {
            speedCategory = "normal";
        } else if (speed < 25) {
            speedCategory = "fast";
        } else {
            speedCategory = "very fast";
        }

        double range = projectileType.getRange() / TILE_SIDE;
        String rangeCategory;
        if (range < 3) {
            rangeCategory = "very short";
        } else if (range < 5) {
            rangeCategory = "short";
        } else if (range < 8) {
            rangeCategory = "medium";
        } else if (range < 10) {
            rangeCategory = "long";
        } else {
            rangeCategory = "very long";
        }


        String sizeCategory;
        double area = projectileType.getWidth() * projectileType.getHeight();
        double normalArea = Projectile.DEFAULT_WIDTH * Projectile.DEFAULT_HEIGHT;

        if (area < normalArea / 2) {
            sizeCategory = "very small";
        } else  if (area < normalArea * 3 / 4) {
            sizeCategory = "small";
        } else if (area < normalArea * 5 / 4) {
            sizeCategory = "normal";
        } else if (area < normalArea * 3 / 2) {
            sizeCategory = "large";
        } else {
            sizeCategory = "very large";
        }


        return new String[] {
            "Max Ammo: " + defaultMaxAmmo,
            "Reload: " + reloadCategory,
            "Fire Rate: " + unloadCategory,
            "Projectile:",
            "\t\tType: " + projectileType.name().toLowerCase(Locale.ROOT),
            "\t\tDamage: " + projectileType.getDamage(),
            "\t\tSpeed: " + speedCategory,
            "\t\tRange: " + rangeCategory,
            "\t\tSize: " + sizeCategory,
        };
    }

}

