# CollisionWorks V2 - TODO

---

## ✅ Major Updates Since v1

- Added GameState for easier player access and improved game loop structure
- Separated entangled logic:
    - Input handling vs frame management
    - Input pauses vs drawing pauses
    - GameState vs player stats
    - UI rendering vs map rendering
    - Selection screens separation
- Added helper classes for better encapsulation and scoping
- Added in game Shop

---

## 🚧 IMMEDIATE PRIORITIES

### New Elements
- [ ] Add buff system
  - [v] Buff shrinking and growing animation is vibrating
  - [v] Buff shrinking and growing animation should stop when bought
  - [v] Buff buying logic - calls playerontop -> change to calling buff.expire() only
  - [v] Add buffs to shop
  - [ ] Add buffs to mapmaker
  - [ ] BUFFS IDEA: (decide whether to implement or not)
      - [ ] player should hold a in-game buffs list (not gamestate)
      - [ ] buffs should have an apply(Player player) and revert(Player player) function that applies/reverts the effects
      - [ ] playerIsOn should only add the buff to the List
- [ ] Enhance currency system
    - [ ] in-game collected currencies add to the gamestate currencies
    - [ ] add a gem counter into in-game ui as well
- [ ] in-between levels content
  - [ ] add shop
  - [ ] add random rewards
- [v] Create a separate SelectionScreen similar to GameScreen

### Core Refactoring
- [ ] Tidy up Player class
- [ ] Decouple currencies from Player and move to GameState
- [ ] Tidy up GameMap class
- [v] Add a cooldown to the input taking logic, separate the drawing logic pauses from it
- [v] Tidy up Selection system
  - [v] separate level selection and skin/accessory selection altogether
  - [v] separate shop
  - [v] finalize the Selection classes
- [v] Shop logic
  - [v] Fill shop logic
  - [v] Add ShopEntry to tidy shop logic
  - [v] Finalize shop logic


### Rendering Architecture
- [ ] Fix Player drawing responsibility (currently draws too much)
- [ ] Move projectile rendering into always-called objects system (planned)
- [v] Ensure HP bars and effects remain in game loop only

### Documentation & Project Management
- [ ] Get rid of unnecessary warnings
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

### File System - Indexing
- [ ] Fix selection world index system
    - Remove confusing level indexing (13 means world 1 level 3 -> ambiguous)
    - Make level progression consistent

---

## ✨ NEW GAME MECHANICS

- [v] Buff system implementation:
    - Fast
    - Small
    - Immune
    - Magnetic
    - Eagle Eye

- [ ] in-between levels content
    - [ ] add shop
    - [ ] add random rewards

- [ ] Sound system:
    - World background sounds
    - Player damage sound
    - Shooting sound
    - Coin collection sound
    - Critical health background change

- [v] Shop system design:
    - Start with "Crystal Palace" concept

- [v] Add signal handling system:
    - Next level
    - Shop
    - Selection
    - Death

---

## 🎮 NEW GAME CONTENT

### Assets
- [ ] All assets need major updates, better designs
    - [ ] Generalize mud + special tiles across all 4 worlds

### Skins
- [ ] Add big/small size skin variants
- [v] Add sakura skin
- [ ] add a ghost as a skin - twist! it can pass through walls

### Entities
- [ ] Fake walls
- [ ] Moving monsters

### Worlds
- [v] Add World 4

### Levels
- [ ] start designing levels

---

## 💡 FUTURE IDEAS

### Player Records
- [ ] Players' gamestates are remembered with each login

### Gameplay Systems
- [ ] Boss fights

### Mapmaker
- [ ] Add MapMaker visualization/debug mode

### Entity System
- [ ] Create `Monster` (opponent) class
- [ ] Create movable box/crate
  - [ ] Buttons should support "hold to stay active" (also triggered by monsters)
  - [ ] Add movable boxes

