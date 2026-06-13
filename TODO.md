# CollisionWorks V2 - TODO

---

## ✅ Major Updates Since v1

- Added GameState for easier player access and improved game loop structure
- Separated entangled logic:
    - Input handling vs frame management
    - GameState vs player stats
    - UI rendering vs map rendering
- Added helper classes for better encapsulation and scoping

---

## 🚧 IMMEDIATE PRIORITIES

### Core Refactoring
- [v] Finalize the Selection classes, separate level selection and skin/accessory selection altogether, also separate shop from them.
- [v] Fill shop logic
- [v] Add currency system
- [ ] Decouple currencies from Player and move to GameState
- [v] Add ShopEntry to tidy shop logic
- [v] Finalize shop logic
- [ ] Add buff system
- [ ] Add buffs to shop
- [ ] Add in game shop
- [ ] Tidy up Player class
- [v] Tidy up Selection system
- [v] Create a separate SelectionScreen similar to GameScreen
- [ ] Tidy up GameMap class

### Rendering Architecture
- [ ] Fix Player drawing responsibility (currently draws too much)
- [ ] Move projectile rendering into always-called objects system (planned)
- [ ] Ensure HP bars and effects remain in game loop only

### Documentation & Project Management
- [v] Add proper documentation
- [v] Start structured Git tracking (commits, feature branches, etc.)

---

## ⚠️ CRITICAL ISSUES

### Restart / Exit System
- [ ] Restart does not reset full map state
    - Player HP resets correctly
    - Map data does NOT reset
    - Enemies remain active after restart
- [ ] Fix persistent player stats (e.g. coin amount)
- [ ] Fix leftover world state after restart
- [ ] In between maps do not create their doors???

### Win State System
- [ ] Define and document win point logic clearly
    - Which win point applies to which game state?
    - Remove ambiguity in state transitions

---

## 🧩 OLD SYSTEM ISSUES STILL EMERGING

### Collision / Entity System
- [ ] Always-called objects should also be checked in projectile collision logic
- [ ] Fix empty grid object collision trigger causing unwanted projectile behavior
- [ ] Fix moving entity check inconsistencies
- [ ] Consider redesigning or removing "always called objects" system

### Spawn / Lifetime Issues
- [ ] When items expire, spawnObjects stop updating correctly
- [ ] Fix multi-projectile collision handling (2 projectiles collision case)
- [ ] Fix collision issue:
    - Two moving collidables
    - One gets squeezed (left/up teleportation bug)

---

## 🧠 SYSTEM IMPROVEMENTS

### MapMaker / Visualization
- [ ] Improve naming in shooter MapMaker
- [ ] Add MapMaker visualization/debug mode

### Game Physics
- [ ] Improve collision system design
- [ ] Possibly redesign movement/collision system entirely

### Architecture Cleanup
- [ ] Consider introducing a generic `Movable` class
- [ ] Generalize collision logic for all movable entities
- [ ] Fix or redesign always-called object system

### Entity Enhancements
- [ ] Mines should not be gridObjects
- [ ] Enhance player shooting mechanism

---

## ✨ NEW GAME MECHANICS

- [ ] Buff system implementation:
    - Fast
    - Small
    - Immune
    - Magnetic
    - Eagle Eye

- [ ] Sound system:
    - World background sounds
    - Player damage sound
    - Shooting sound
    - Coin collection sound
    - Critical health background change

- [v] Shop system design:
    - Start with "Crystal Palace" concept

- [ ] Fix selection world index system
    - Remove confusing level indexing (13 means world 1 level 3 -> ambiguous)
    - Make level progression consistent

- [v] Add signal handling system:
    - Next level
    - Shop
    - Selection
    - Death

- [ ] Add random reward system between levels

- [ ] Buttons should support "hold to stay active" (also triggered by monsters)
- [ ] Add movable boxes:
    - Block projectiles
    - Hold buttons down

- [ ] All assets need major updates, better designs
  - [ ] Generalize mud + special tiles across all 4 worlds

---

## 🎮 GAME CONTENT

### Skins
- [ ] Add big/small size skin variants
- [ ] Add sakura skin (prize for spring festival)

### Worlds
- [v] Add World 4

---

## 💡 FUTURE IDEAS

### Player Records
- The player continues from the last level they were in

### Gameplay Systems
- Moving monsters
- Fake walls
- In-game currency system
- Boss fights

### Entity System
- Create `Monster` (opponent) class
- Create movable box/crate

