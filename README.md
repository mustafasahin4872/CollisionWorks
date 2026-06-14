# CollisionWorks V2

CollisionWorks V2 is a 2D Java-based game built entirely from scratch, using **StdDraw** for rendering and input handling. The project focuses on a custom game architecture, collision systems, and modular gameplay design across multiple worlds.

The game features a custom hand-built mapmaker system driven by text files, an animated skin maker, special enemies, fighting logic, loot systems, various tile mechanics, a built-in shop, and more.

---

## Overview

CollisionWorks V2 is designed around a structured game loop with a centralized `GameState` system. Each run consists of selecting a skin and accessory, choosing a world and level, generating a map, and entering a gameplay loop where all systems update continuously.

The game supports multiple worlds (currently 4 worlds), each with distinct level designs and mechanics.

---

## Core Architecture

### Main Classes & Core Loop

- **Main**: Manages the transitions between the different UI screens (`SkinSelection`, `LevelSelection`, `Shop`, `Game`) based on the current `GameState`.
- **GameState**: The central runtime state container. Manages the current game state (`SELECTION`, `GAME`, `SHOP`, `DEAD`, `PAUSE`, etc.), tracks the active `Player`, owned/buyable items (skins, accessories, buffs), currencies (coins, gems), and level progression.
- **Game**: Runs the main gameplay loop. It coordinates inputs, player logic updates, map object interactions, frame recentering, and rendering.
- **GameMap**: Stores map data and runtime representations. Uses `MapMaker` to dynamically generate levels from `.txt` files based on the chosen world and level. It maintains the grid layers and dynamic objects (`alwaysCalledObjects`).
- **Frame**: Handles screen setup, resolution scaling (`X_SCALE`, `Y_SCALE`), and camera positioning to keep the player in focus.
- **InputHandler**: Centralized handling of keyboard (arrows/space) and mouse input using `StdDraw`, feeding states into the game loop.
- **GameScreen**: Handles all the in-game UI overlays, such as health bars, coin amounts, ammo count, critical health effects, and the pause/death screens.

### Pre-Game Menus

- **SkinSelection**: Allows the player to preview and select from their owned skins and accessories before starting the game.
- **LevelSelection**: The world and level picker. It previews the world's aesthetics in the background while allowing the player to select levels.
- **Shop**: An interactive in-game store where players can spend gems and coins to buy new cosmetic skins, accessories, or gameplay buffs.

---

## Entity & Map System (MapObjects)

The game world uses a hybrid system for mapping and collision:
- **GridObject System**: Used for map layout, static objects, and level design. Entities that snap to tiles extend `GridObject`, yet any grid object can move in between tiles if necessary.
- **MapObject System**: Used for dynamic and moving objects (player, projectiles, moving enemies). They operate in continuous space. 

All map and game entities inherit from `MapObject`, allowing a consistent structure for position tracking, dimensions, rendering (including animated resizing), and lifecycle management (`expire()`).

### Entity Categories

Entities implement various interfaces and abstract classes to mix and match behaviors:
- `Moving` / `MovingCollidable`: Objects that have velocities and collision boxes.
- `Collidable`: Objects that restrict movement of `MovingCollidable` entities.
- `OnEffector`: Objects that trigger effects when a player steps on them (e.g., Buffs, Buttons, Coins).
- `Timed`: Objects that have lifetimes or cooldowns using `Timer`.
- `HealthBearer`: Entities with an `HPBar` that can take damage and die.
- `Damaging` / `Damager`: Entities that inflict damage on contact.

---

## Game Flow

1. **Initialization**: `GameState`, `InputHandler`, `Frame`, `Shop`, and UI/selection instances are created in `Main`.
2. **Customization & Shop**: The player customizes their character skins and accessories in the `SkinSelection` screen, or visits the `Shop` to buy new items (skins, accessories, buffs).
3. **Level Selection**: The player chooses a World (1-4) and Level in the `LevelSelection` screen.
4. **Map Creation**: `GameMap` is generated using `MapMaker` from text files.
5. **Game Loop**: The loop runs in `Game.java`. It processes inputs, updates player velocities, calls interactions with surrounding grid objects, and recenters the camera.
6. **State Transition**: The loop exits when the state changes (e.g., `DEAD`, `PASSED`, `PAUSE`). The player then proceeds to the next level, respawns, or returns to menus.

---

## Features

- Custom `GameState`-driven architecture ensuring clean data scoping.
- Text-file based MapMaker system.
- 4 unique worlds with distinct level designs.
- Animated skins and cosmetic accessories.
- Dynamic camera following the player.
- UI separation and pause/death menus.
- Extensible collision and movement system.
- In-game Shop with functional and cosmetic items.
- Buff System: Speed, Shrink, Shield, Magnet, and Vision buffs.

---

## Current Roadmap & Known Issues

Based on the development tracking:

### Priorities
- **Buff System Polish**: Buffs currently visually shrink/grow and stop when bought. Need to fully integrate their active gameplay effects into the `Player` class logic.
- **Currency Enhancements**: Need to properly integrate collected coins/gems into persistent save states.
- **Entity Refactoring**: Tidy up the `Player` and `GameMap` classes, decoupling any remaining entangled logic.

### Critical Issues
- **Restart System**: Restarting a level resets player HP but doesn't fully reset the map data or enemies to their initial state. Persistent stats need fixed tracking upon death.
- **Collision Quirks**: Occasional bugs when two moving collidables interact (squeezing/teleportation). Improving the `MovingCollidable` and continuous movement integration is planned.
- **Always-Called Objects**: Projectiles sometimes miss collisions with off-grid always-called objects, requiring a more unified collision check approach.

---

## Author

Designed and developed by **Mustafa Şahin**

---

## Future Work (Planned)

- Boss fights and moving enemies.
- Advanced map visualization tools.
- Expanded buff and skill mechanics.
- Sound system integration (ambient sounds, damage, shooting, collecting).
- Saving player records and states across sessions.
