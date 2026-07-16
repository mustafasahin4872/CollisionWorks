package mapobjects.traits.schemas;

import mapobjects.traits.receivers.HealthEffectReceiver;

import java.util.Set;

public interface Damaging {

    void setTargets(Set<HealthEffectReceiver> targets);

}
