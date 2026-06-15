package mapobjects.mapobject;

import helpers.MapObjectGenerator;
import mapobjects.category.MapObject;
import mapobjects.category.Spawnable;
import mapobjects.component.Spawner;
import mapobjects.component.Timer;
import java.util.HashSet;
import java.util.Set;
import static mapobjects.mapobject.Projectile.RegularProjectile;
import static mapobjects.mapobject.Projectile.HomingProjectile;

public class Gun extends MapObject implements Spawnable {

    public enum GunType {

        DEFAULT(RegularProjectile.class, 5, 1000, 300, 20), // reload once per second
        UZI(RegularProjectile.class, 10, 400, 100, 10),
        SHOTGUN(RegularProjectile.class, 3, 2000, 600, 60),
        STAFF(HomingProjectile.class, 4, 1000, 1000, 60),
        PACIFIST(RegularProjectile.class, 0, 1000, 1000, 0) // does not have ammo

        ;

        private final Class<? extends Projectile> projectileClass;
        private final int maxAmmo;
        private final int reloadTime; // in milliseconds
        private final int cooldown;
        private final double damagePerAmmo;

        GunType(Class<? extends Projectile> projectileClass, int maxAmmo, int reloadTime, int cooldown, double damagePerAmmo) {
            this.projectileClass = projectileClass;
            this.maxAmmo = maxAmmo;
            this.reloadTime = reloadTime;
            this.cooldown = cooldown;
            this.damagePerAmmo = damagePerAmmo;
        }

        public Class<? extends Projectile> getProjectileClass() {
            return projectileClass;
        }
    }

    private final GunType gunType;
    private final Spawner spawner;

    private int ammo;
    private final int maxAmmo;
    private final double damagePerAmmo;

    private final Timer reloadTimer;
    private final Timer cooldownTimer;

    private double speed;
    private int direction;
    private double[] centerCoordinates;
    private double[] nextCenterCoordinates;

    protected final Set<Projectile> projectiles = new HashSet<>();

    Gun(GunType gunType) {
        this.gunType = gunType;
        this.maxAmmo = gunType.maxAmmo;
        this.damagePerAmmo = gunType.damagePerAmmo;
        ammo = maxAmmo;

        reloadTimer = new Timer(0, gunType.reloadTime);
        cooldownTimer = new Timer(0, gunType.cooldown);

        spawner = new Spawner(worldIndex);
    }


    @Override
    public void call(Player player) {
        double xVelocity = player.getXVelocity();
        double yVelocity = player.getYVelocity();
        centerCoordinates = player.getCenterCoordinates();
        nextCenterCoordinates = player.getNextCenterCoordinates();
        direction = ((int) Math.toDegrees(Math.atan2(yVelocity, xVelocity)) + 360) % 360;
        speed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);

        if (!reloadTimer.isCompleted()) {reloadTimer.activate();}
        if (reloadTimer.isActive()) {reload();}
        reloadTimer.tick();
        cooldownTimer.tick();
    }


    @Override
    public void spawn() {
        MapObjectGenerator mapObjectGenerator = spawner.directionSpawn(centerCoordinates, nextCenterCoordinates, 30);

        Projectile projectile = createProjectile(mapObjectGenerator, direction, speed);
        projectiles.add(projectile);
    }


    private Projectile createProjectile(MapObjectGenerator mapObjectGenerator, int direction, double speed) {
        if (gunType.projectileClass == HomingProjectile.class) {
            return mapObjectGenerator.mutateToHomingProjectile(direction, 0.75, damagePerAmmo);
        } else {
            return mapObjectGenerator.mutateToRegularProjectile(20, 10, direction, speed + Projectile.DEFAULT_INITIAL_SPEED, damagePerAmmo);
        }
    }


    void shoot() {
        if (ammo > 0 && !cooldownTimer.isCompleted()) {
            cooldownTimer.activate();
            reloadTimer.startCooldown();
            spawn();
            ammo--;
        }
    }


    private void reload() {
        if (ammo+1<=maxAmmo) ammo++;
    }

    public int getAmmo() {
        return ammo;
    }

    public Set<Projectile> getProjectiles() {return projectiles;}

    // unused, use getProjectiles instead
    @Override
    public MapObject[] getSpawnObjects() {
        return projectiles.toArray(new Projectile[0]);
    }



}
