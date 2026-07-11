# Implementation Plan: Decoupling `Player` via Effect Wrappers

*Note: I am acting as your advisor for this refactor. The following plan is a blueprint for you to execute at your own pace.*

The goal is to completely eliminate the tight coupling between `Player.java` and the rest of the game world by replacing direct mutation with a queued "Effect" system and generalizing our trait interfaces. 

---

## Phase 1: Trait Generalization & The "Check vs. Reaction" Pattern
We must distinguish between the logic that *detects* an event (which needs an entity as an input) and the *reaction* to that event (which should NOT take an input, but instead **return** an `Effect` wrapper).

### `mapobjects/traits/OnEffector.java`
- **Checks:** `checkPlayerIsOn(Player)` becomes `boolean checkIsOn(Moving entity)`.
- **Reactions:** `playerIsOn(Player)` becomes `Effect reactionIsOn()`. 
  *(Instead of doing `player.setSpeed(5)`, it returns `new SpeedEffect()`, which the checking loop then applies to the entity).*

### `mapobjects/traits/Ranged.java`
- **Checks:** `checkPlayerInRange(Player)` becomes `boolean checkInRange(Moving entity)`.
- **Reactions:** `playerInRange(Player)` becomes `Effect reactionInRange()`.

### `mapobjects/traits/Timed.java`
- **Reactions:** Change `timeIsUp(Player player)` to `Effect timeIsUp()`. A timer triggering shouldn't require the player's state—it just returns whatever effect the timer was counting down for.

---

## Phase 2: The Generic Effect System
The Effect wrappers will be entirely generic so they can be applied to future enemies and NPCs.

### `game/core/effects/Effect.java`
An interface representing a modification to an entity.
```java
public interface Effect {
    void apply(Moving entity); 
    void revert(Moving entity);
}
```

### `game/core/effects/SpeedEffect.java` (Example)
```java
public class SpeedEffect implements Effect {
    private final double multiplier;
    // apply() and revert() logic mapping to the Moving interface
}
```

### Entity Inbox Implementation
- Any entity that can receive effects (Player, Monsters) will hold a `List<Effect> activeEffects`.
- Add a method `public void receiveEffect(Effect effect)`.
- In the entity's `update()` loop, stats are recalculated based on the `activeEffects` list.

---

## Phase 3: The `Aiming` Interface & Target Parsing
To remove `Player` from the generic update loops without losing the ability for enemies/turrets to aim, we will introduce a new interface.

### `mapobjects/traits/Aiming.java`
```java
public interface Aiming {
    void setTargets(Set<Moving> targets);
    Set<Moving> getTargets();
    
    // Default method to parse the Set and find the nearest target
    default Moving getNearestTarget(double myX, double myY) {
        // parsing logic...
    }
}
```
*Objects like `Shooter` will implement this. The `GameMap` will pass the set of valid targets (Player, friendly NPCs) into `setTargets()` when the map loads or updates.*

---

## Phase 4: The Core Game Loop Rewrite
Once the traits are generic and the Effect system is built, we can safely pull the `Player` out of the core loop.

### `mapobjects/traits/MapObject.java`
- Change `call(Player player)` to `update()`.

### All MapObject Subclasses (`Shooter`, `Mine`, `Sign`, `Door`, etc.)
- Update their method signatures to match the new `update()` method.
- Remove all direct references to `Player`. They will rely on `Aiming` to find targets, and `reaction()` to output `Effect` wrappers.
