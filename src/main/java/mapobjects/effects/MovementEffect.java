package mapobjects.effects;

public record MovementEffect(double maxSpeedMult, double accelerationMult, double decelerationMult) implements Effect {}

