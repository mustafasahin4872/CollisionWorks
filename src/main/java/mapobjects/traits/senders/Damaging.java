package mapobjects.traits.senders;

import mapobjects.traits.receivers.HealthEffectReceiver;

public interface Damaging extends Sender {

    Class<? extends HealthEffectReceiver> getTargetClass();

}
