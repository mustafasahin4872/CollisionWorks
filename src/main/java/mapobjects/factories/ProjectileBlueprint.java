package mapobjects.factories;

import mapobjects.entities.Projectile;

import java.util.Locale;

public class ProjectileBlueprint {
    private final String name;
    private double width;
    private double height;
    private double speed;
    private double damage;
    private double range;
    private boolean piercing;
    private double inertia;
    private double bounceFactor;

    public ProjectileBlueprint(Projectile.ProjectileType projectileType) {
        this.name = projectileType.name().toLowerCase(Locale.ROOT);
        this.width = projectileType.getWidth();
        this.height = projectileType.getHeight();
        this.speed = projectileType.getSpeed();
        this.damage = projectileType.getDamage();
        this.range = projectileType.getRange();
        this.piercing = projectileType.isPiercing();
        this.inertia = projectileType.getInertia();
        this.bounceFactor = projectileType.getBounceFactor();
    }

    public Projectile createProjectile(int worldIndex, double x, double y, double direction) {
        return new Projectile(worldIndex, x, y, width, height, name, direction, range, damage, speed, inertia, bounceFactor, piercing);
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setInertia(double inertia) {
        this.inertia = inertia;
    }

    public void setBounceFactor(double bounceFactor) {
        this.bounceFactor = bounceFactor;
    }

    public void addPiercing() {
        piercing = true;
    }

}
