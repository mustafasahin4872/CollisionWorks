package mapobjects.effects;

public interface Effect {}

record MovementEffect(double maxSpeedMult, double accelerationMult, double decelerationMult) implements Effect {}

record DamageEffect(double damage) implements Effect {}

record CurrencyEffect(int coinAmount, int gemAmount) implements Effect {}
