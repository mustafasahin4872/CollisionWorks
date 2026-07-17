## v1: first version of the game code
- game mechanics:
  - map system
  - player mechanics
  - mapmaker system
  - grid system
- new entities:
  - tiles
  - doors and buttons
  - chests and coins
  - enemies: mortar, mine, shooter, ghost

the code is extremely entangled and written poorly. this is fixed in v2

## v2: mainly rewriting the entire code for scalability and encapsulation
- refactoring:
  - create a solid state system instead of jumping from one loop to another
  - decouple, encapsulate and centralize the input and drawing logics from code
  - decouple the player from entity calls, develop an effect system
  - centralize collision mechanics and tie it to the effect system
- new mechanics:
  - selection screens
  - guns
  - buffs
  - trinkets(accessories)
- new assets:
  - addition of all the main characters: sakura, sheriff, shopkeeper
  - addition of lore-important trinkets: sheriff set, sakura set

very few in-game mechanics are added in this version, most of the new features are ui related.

## future(v3+): 
- moving to a better drawing engine from StdDraw
- adding mapmaker ui
- creating a way to store game data to not lose progression
- addition of a dialogue tool for the end of game dialogues(lore)
- boss fights
- addition of sound effects and in-game music
