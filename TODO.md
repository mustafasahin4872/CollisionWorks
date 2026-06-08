# CollisionWorks V2 - TODO

---

## ✅ DONE / COMPLETED

- Added GameState for easier player access and improved game loop structure
- Separated entangled logic:
    - Input handling vs frame management
    - GameState vs player stats
    - UI rendering vs map rendering
- Added helper classes for better encapsulation and scoping

---

## 🚧 IMMEDIATE PRIORITIES

### Documentation & Project Management
- [v] Add proper documentation
- [v] Start structured Git tracking (commits, feature branches, etc.)

### Core Refactoring
- [ ] Tidy up Player class
- [v] Tidy up Selection system
- [ ] Create a separate SelectionScreen similar to GameScreen
- [ ] Tidy up GameMap class
- [ ] Fill shop logic

### Rendering Architecture
- [ ] Fix Player drawing responsibility (currently draws too much)
- [ ] Move projectile rendering into always-called objects system (planned)
- [ ] Ensure HP bars and effects remain in game loop only

---

## ⚠️ CRITICAL ISSUES

### Restart / Exit System
- [ ] Restart does not reset full map state
    - Player HP resets correctly
    - Map data does NOT reset
    - Enemies remain active after restart
- [ ] Fix persistent player stats (e.g. coin amount)
- [ ] Fix leftover world state after restart

### Win State System
- [ ] Define and document win point logic clearly
    - Which win point applies to which game state?
    - Remove ambiguity in state transitions

---

## 🧩 OLD SYSTEM ISSUES

### Collision / Entity System
- [ ] Always-called objects should also be checked in projectile collision logic
- [ ] Fix empty grid object collision trigger causing unwanted projectile behavior
- [ ] Fix moving entity check inconsistencies
- [ ] Consider redesigning or removing "always called objects" system

### Entity Mechanics
- [ ] Mines should not be gridObjects
- [ ] Buttons should support "hold to stay active" (also triggered by monsters)
- [ ] Add movable boxes:
    - Block projectiles
    - Hold buttons down
- [ ] Fix player shooting mechanism

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
- [ ] Improve collision system design
- [ ] Possibly redesign movement/collision system entirely

### Architecture Cleanup
- [ ] Consider introducing a generic `Movable` class
- [ ] Generalize collision logic for all movable entities
- [ ] Fix or redesign always-called object system

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

- [ ] Shop system design:
    - Start with "Crystal Palace" concept

- [ ] Fix selection world index system
    - Remove confusing 13–14 level indexing
    - Make level progression consistent

- [ ] Add signal handling system:
    - Next level
    - Shop
    - Selection
    - Death

- [ ] Add random reward system between levels

- [ ] Generalize mud + special tiles across all 4 worlds

---

## 🎮 GAME CONTENT

### Skins
- [ ] Add test skin
- [ ] Add big/small size skin variants

### Worlds
- [ ] Add World 4
- [ ] Expand world system structure

---

## 💡 FUTURE IDEAS

### Gameplay Systems
- Moving monsters
- Fake walls
- In-game currency system
- Boss fights

### Entity System
- Create `Monster` (opponent) class

---

## 🏗 OBJECT INITIALIZATION

- [ ] Add stage system
- [ ] Improve world initialization flow

---

## 🎨 ASSET CREATION

- Create new skins:
    - Sakura
- Expand World 4 assets and design
