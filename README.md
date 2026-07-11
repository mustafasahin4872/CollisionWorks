# CollisionWorks V2

CollisionWorks V2 is a 2D Java-based game built entirely from scratch, using **StdDraw** for rendering and input handling. The project focuses on a custom game architecture, collision systems, and modular gameplay design across multiple worlds.

The game features a custom hand-built mapmaker system driven by text files, an animated skin maker, special enemies, fighting logic, loot systems, various tile mechanics, a built-in shop, and more.

---

## Overview

CollisionWorks V2 is designed around a structured game loop with a centralized `GameState` system. Each run consists of selecting a skin, accessory, and gun, choosing a world and level, generating a map, and entering a gameplay loop where all systems update continuously.

The game supports multiple worlds (currently 4 worlds), each with distinct level designs and mechanics.

---

## Core Architecture

### Main Classes & Core Loop

- **Main**: Bootstraps the application from the absolute root and manages the transitions between the different UI screens (`Selection`, `Shop`, `Game`) based on the current `GameState`.
- **GameState**: The central runtime state container. Manages the current game state (`SELECTION`, `GAME`, `SHOP`, `DEAD`, `PAUSE`, etc.), tracks the active `Player`, owned/buyable items (skins, accessories, buffs, guns), currencies (coins, gems), and level progression.
- **Game**: Runs the main gameplay loop. It coordinates inputs, player logic updates, map object interactions, frame recentering, and rendering.
- **GameMap**: Stores map data and runtime representations. Uses `MapMaker` to dynamically generate levels from `.txt` files based on the chosen world and level. It maintains the grid layers and dynamic objects (`alwaysCalledObjects`).
- **Frame & Drawer**: A fully decoupled rendering architecture located in `game.io`. The game logic is entirely separated from `StdDraw` using a `Drawable` interface and `Drawer` components.
- **InputHandler**: Centralized handling of keyboard (arrows/space) and mouse input using `StdDraw`, feeding states into the game loop.
- **GameScreen**: Handles all the in-game UI overlays, such as health bars, coin amounts, gem counts, ammo count, critical health effects, and the pause/death screens.

### Pre-Game Menus

- **Selection Screens**: Allows the player to preview and select from their owned skins, accessories, and guns before starting the game. Separated clearly into Level Selection and Skin/Accessory/Gun Selection.
- **LevelSelection**: The world and level picker. It previews the world's aesthetics in the background while allowing the player to select levels.
- **Shop**: An interactive in-game store where players can spend gems and coins to buy new cosmetic skins, accessories, guns, and gameplay buffs.

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
- `Equippable`: Items that can be bought and equipped by the player (Skins, Accessories, Buffs, Guns), featuring a rarity system.

---

## Game Flow

1. **Initialization**: `GameState`, `InputHandler`, `Frame`, `Shop`, and UI/selection instances are created in `Main`.
2. **Customization & Shop**: The player customizes their character's loadout in the Selection screens, or visits the `Shop` to buy new items (skins, accessories, buffs, guns) using the centralized `ShopData`.
3. **Level Selection**: The player chooses a World (1-4) and Level in the `LevelSelection` screen.
4. **Map Creation**: `GameMap` is generated using `MapMaker` from text files.
5. **Game Loop**: The loop runs in `Game.java`. It processes inputs, updates player velocities, calls interactions with surrounding grid objects, and recenters the camera.
6. **State Transition**: The loop exits when the state changes (e.g., `DEAD`, `PASSED`, `PAUSE`). The player then proceeds to the next level, respawns, or returns to menus.

---

## Features

- Custom `GameState`-driven architecture ensuring clean data scoping.
- Text-file based MapMaker system.
- 4 unique worlds with distinct level designs.
- Advanced `Equippable` system with rarities.
- Weapon system with distinct gun types and spawn logics.
- Dynamic camera following the player.
- Decoupled `Drawer` rendering engine.
- Extensible collision and movement system.
- In-game Shop with functional and cosmetic items.
- Buff System: Speed, Shrink, Shield, Magnet, and Vision buffs.

---

## Current Roadmap & Known Issues

Based on the development tracking:

### Priorities
- **Buff System Polish**: Add proper buff logic to `Player`, implement mapmaker buffs, and add a stats page for upgrades.
- **Rendering Polish**: Debug minor rendering quirks, including signs trembling during camera movement and text elements anchoring towards the upper-left instead of centering.
- **Entity Refactoring**: Decouple the `Player` class from MapObject calls and input taking. Continue integrating `CollisionMethods` into distinct box classes (`CollisionBox`, `EffectBox`).
- **Level & Progression Features**: Lock unpassed levels in the selection screen, draw chains on locked boxes, disable coin collection on passed levels, and add in-between level content (random rewards).

### Critical Issues
- **Restart System**: Restarting a level correctly resets player HP and stats, but map data and active enemies are NOT fully resetting. 
- **Collision Quirks**: Occasional bugs when two moving collidables interact (squeezing/teleportation). Generalizing collision mechanics for all movable entities is planned.
- **Always-Called Objects**: Projectiles sometimes miss collisions with off-grid always-called objects.

---

## Author

Designed and developed by **Mustafa Şahin**

## AI Usage
AI assistance is used solely for repetitive text creations, small helper logic implementations, and game design guidance.

---

## Future Work (Planned)

- Boss fights and moving enemies.
- Advanced map visualization tools.
- Expanded buff and skill mechanics.
- Sound system integration (ambient sounds, damage, shooting, collecting).
- Saving player records and states across sessions.
