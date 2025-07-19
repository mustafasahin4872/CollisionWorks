package helperobjects;

import game.Player;
import mapobjects.category.GridObject;
import mapobjects.mapobject.*;

import static mapobjects.category.GridObject.TILE_SIDE;

public class Blueprint {

    private final int worldIndex, xNum, yNum;
    private final double centerX, centerY;

    public Blueprint(int worldIndex, int xNum, int yNum) {
        this.worldIndex = worldIndex;
        this.xNum = xNum;
        this.yNum = yNum;
        centerX = (xNum - 0.5) * TILE_SIDE;
        centerY = (yNum - 0.5) * TILE_SIDE;
    }

    public Blueprint(int worldIndex, double x, double y) {
        this.worldIndex = worldIndex;
        xNum = (int) (x/TILE_SIDE) + 1; yNum = (int) (y/TILE_SIDE) + 1;
        centerX = x;
        centerY = y;
    }

    public Projectile mutateToProjectile(int direction) {
        return new Projectile(worldIndex, centerX, centerY, direction);
    }

    public Projectile mutateToProjectile(double width, double height, int direction, double speed) {
        return new Projectile(worldIndex, centerX, centerY, width, height, direction, speed);
    }

    public Projectile mutateToHomingProjectile(int direction, double I) {
        return new Projectile.HomingProjectile(worldIndex, centerX, centerY, direction, I);
    }

    public Coin.SingleCoin mutateToSingleCoin() {
        return new Coin.SingleCoin(worldIndex, xNum, yNum);
    }

    public Coin.TripleCoin mutateToTripleCoin() {
        return new Coin.TripleCoin(worldIndex, xNum, yNum);
    }

    public Coin.CoinBag mutateToCoinBag() {
        return new Coin.CoinBag(worldIndex, xNum, yNum);
    }

    public Mine mutateToMine() {
        return new Mine(worldIndex, xNum, yNum);
    }

    public Mortar mutateToMortar(GridObject[][][] layers) {
        return new Mortar(worldIndex, xNum, yNum, layers);
    }

    public Button.BigButton mutateToBigButton() {
        return new Button.BigButton(worldIndex, xNum, yNum);
    }

    public Button.LittleButton mutateToLittleButton() {
        return new Button.LittleButton(worldIndex, xNum, yNum);
    }

    public Point.SpawnPoint mutateToSpawnPoint(boolean isBlue) {
        return new Point.SpawnPoint(worldIndex, xNum, yNum, isBlue);
    }

    public Point.CheckPoint mutateToCheckPoint(int index, boolean isBlue) {
        return new Point.CheckPoint(worldIndex, xNum, yNum, index, isBlue);
    }

    public Point.WinPoint mutateToWinPoint(int index, boolean isBlue) {
        return new Point.WinPoint(worldIndex, xNum, yNum, index, isBlue);
    }

    public Chest.WoodenChest mutateToWoodenChest(char[] buffs) {
        return new Chest.WoodenChest(worldIndex, xNum, yNum, buffs);
    }

    public Chest.SilverChest mutateToSilverChest(char[] buffs) {
        return new Chest.SilverChest(worldIndex, xNum, yNum, buffs);
    }

    public Chest.GoldenChest mutateToGoldenChest(char[] buffs) {
        return new Chest.GoldenChest(worldIndex, xNum, yNum, buffs);
    }

    public Door mutateToDoor(char alignment) {
        return new Door(worldIndex, xNum, yNum, alignment);
    }

    public Door mutateToDoor(char alignment, int length) {
        return new Door(worldIndex, xNum, yNum, alignment, length);
    }

    public Sign mutateToSign(String[] messages) {
        return new Sign(worldIndex, xNum, yNum, messages);
    }

    public Shooter mutateToRegularShooter(char direction, GridObject[][][] layers) {return new Shooter.RegularShooter(worldIndex, xNum, yNum, direction, layers);}

    public Shooter mutateToDirectionShooter(GridObject[][][] layers, Player player) {return new Shooter.DirectionShooter(worldIndex, xNum, yNum, layers, player);}

    public Shooter mutateToHomingShooter(GridObject[][][] layers, Player player) {return new Shooter.HomingShooter(worldIndex, xNum, yNum, layers, player);}

    public Shooter mutateToMovingShooter(char direction, char alignment, GridObject[][][] layers) {return new Shooter.MovingShooter(worldIndex, xNum, yNum, direction, alignment, layers);}

    public Ghost mutateToGhost(char alignment, GridObject[][][] layers) {
        return new Ghost(worldIndex, xNum, yNum, alignment, layers);
    }

    public Tile mutateToTile(char type) {
        return switch (type) {
            case ' ', '_' -> new Tile.SpaceTile(worldIndex, xNum, yNum);
            case 'w' -> new Tile.SlowTile(worldIndex, xNum, yNum);
            case '!' -> new Tile.SpecialTile(worldIndex, xNum, yNum);
            case '-' -> new Tile.DamageTile(worldIndex, xNum, yNum);
            case '+' -> new Tile.HealTile(worldIndex, xNum, yNum);
            case 'X' -> new Tile.WallTile(worldIndex, xNum, yNum);
            case '#' -> new Tile.RiverTile(worldIndex, xNum, yNum);
            default -> {
                System.out.println("default message for basic tiles, an error occurred");
                yield new Tile.SpaceTile(worldIndex, xNum, yNum);
            }
        };
    }

}