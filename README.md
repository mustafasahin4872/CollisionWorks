# CollisionWorks V2

CollisionWorks V2 is a 2D Java-based game built entirely from scratch, except for using **StdDraw** for rendering and input handling. The project focuses on custom game architecture, collision systems, and modular gameplay design across multiple worlds.

The game features a hand-built mapmaker system driven by text files, animated skin maker, special enemies, fighting logic, loot systems, various tile mechanics, and more.

---

## Overview

CollisionWorks V2 is designed around a structured game loop with a centralized `GameState` system. Each run consists of selecting a configuration, generating a map, and entering a gameplay loop where all systems update continuously.

The game supports multiple worlds, each with distinct level designs and mechanics.

---

## Core Architecture

### Main Classes

#### Player
- Stores all player-related stats and state
- Handles movement-related data and gameplay attributes

#### GameState
- Central runtime state container
- Holds:
    - Player instance
    - World index
    - Level index
- Used to propagate game progression between systems

#### Frame
- Handles screen setup, resolution, and camera positioning.

#### InputHandler
- Handles keyboard and mouse input using StdDraw, and provides a centralized input state for the game loop.

#### GameMap
- Stores map data and runtime representation
- Uses MapMaker to generate maps from `.txt` files
- Inputs:
    - World index
    - Level index
    - Player reference
    - Map type or dimensions

#### Game
- Runs the main game loop and coordinates updates, rendering, and state transitions.

#### Selection
- Level selection screen
- Creates a temporary map instance used exclusively for previewing and selection logic.

#### Shop
- Shop screen used to buy skins and stat buffs.

---

## Game Flow

### 1. Initialization
- GameState, InputHandler, Frame, Game, Shop, Selection instances are created.

### 2. Selection Screen
- Multiple maps and player configurations are generated
- Player chooses:
    - World
    - Level
    - Accessories / configuration
- Player object is created
- Initial `GameState` is produced

### 3. Map Creation
- GameMap is generated using MapMaker
- Map is built from text file definitions

### 4. Game Loop
- GameState is continuously updated each frame
- Collision, movement, and logic updates occur
- Loop continues until a state change is triggered

### 5. State Transition
- Game ends or progresses based on GameState
- Transitions to:
    - Next level
    - Shop / selection screen
    - Main menu

---

## Features

- Custom GameState-driven architecture
- Text-file based mapmaker system
- Multi-world structure with distinct level designs
- Animated skins system
- Button-door mechanics
- Modular helper classes for encapsulation
- Separation of:
    - Input handling
    - Frame management
    - UI rendering vs map rendering
- Extensible collision system foundation

---

## World System

- The game world is built on a grid-based tile system to simplify map creation and level design through the MapMaker tool.
- However, gameplay entities are not strictly bound to grid tiles. Many objects (including the player, projectiles, and moving enemies) operate in continuous space.
- Because of this, the system uses a hybrid approach:
    - Grid system → used for map layout, static objects, and level design
    - Entity system → used for all dynamic and moving objects

- All entities inherit from a shared base class, allowing a consistent structure for:
    - Movement
    - Collision handling
    - Rendering
    - State updates

- This design allows both static tile-based gameplay and flexible dynamic interactions to coexist in the same engine.

---

## Design Philosophy

- Manual implementation of core game systems
- Strong separation of concerns
- Expandability for new mechanics
- Debug-friendly architecture via centralized state control

---

## Author

Designed and developed by Mustafa Şahin

---

## Future Work (Planned)

- Improved collision system overhaul
- Boss fights and moving enemies
- In-game shop and currency system
- Advanced map visualization tools
- Expanded buff and skill mechanics
- Sound system integration
- Skin and world expansion
