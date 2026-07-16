package mapobjects.traits.receivers;

import mapobjects.effects.Effect;
import mapobjects.effects.MovementEffect;

import java.util.Set;

public interface MovementEffectReceiver extends Receiver {

    double getBaseMaxSpeed();
    double getBaseAcceleration();
    double getBaseDeceleration();

    void setMaxSpeed(double maxSpeed);
    void setAcceleration(double acceleration);
    void setDeceleration(double deceleration);

    default void processMovementEffect(Set<Effect> effects) {

        double totalMaxSpeedMult = 1;
        double totalAccelerationMult = 1;
        double totalDecelerationMult = 1;

        for (Effect effect : effects) {
            if (effect instanceof MovementEffect(double maxSpeedMult, double accelerationMult, double decelerationMult)) {
                totalMaxSpeedMult *= maxSpeedMult;
                totalAccelerationMult *= accelerationMult;
                totalDecelerationMult *= decelerationMult;
            }
        }

        setMaxSpeed(getBaseMaxSpeed() * totalMaxSpeedMult);
        setAcceleration(getBaseAcceleration() * totalAccelerationMult);
        setDeceleration(getBaseDeceleration() * totalDecelerationMult);

    }

}
