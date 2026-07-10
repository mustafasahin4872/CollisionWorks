package game.ui;

import game.core.GameState;
import game.io.InputHandler;
import game.core.GameState.STATE;
import game.data.ShopEntry;
import mapobjects.entities.Accessory;
import mapobjects.traits.Equippable.RARITY;
import mapobjects.entities.Buff;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;
import java.util.List;

public class Shop {

    private static final STATE[] SHOP_STATES = new STATE[] {
            STATE.SELECTION, STATE.SHOP, STATE.ALTERNATE1, STATE.ALTERNATE2, null
    };

    private static final int TOTAL_PAGE_NUM = SHOP_STATES.length - 2;

    private final GameState gameState;
    private final ShopPage[] pages = new ShopPage[TOTAL_PAGE_NUM];

    public Shop(InputHandler inputHandler, GameState gameState) throws Exception {
        this.gameState = gameState;
        configure(inputHandler, gameState);
    }

    private void configure(InputHandler inputHandler, GameState gameState) throws Exception {
        if (buyablesPerPage.length != TOTAL_PAGE_NUM)
            throw new Exception("WRONG NUMBER OF STATES OR BUYABLES_PER_PAGE");
        for (int i = 0; i < TOTAL_PAGE_NUM; i++) {
            pages[i] = new ShopPage(inputHandler, gameState);
            pages[i].configure(SHOP_STATES[i], SHOP_STATES[i + 1], SHOP_STATES[i + 2], buyablesPerPage[i]);
        }
    }

    public void shopLoop() throws Exception {

        STATE state = gameState.getState();
        for (int i = 0; i < TOTAL_PAGE_NUM; i++) {
            if (state == SHOP_STATES[i + 1]) {
                pages[i].shopLoop();
            }
        }

    }

    // ------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------
    // SHOP ENTRIES
    // ------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------------------

    private final List<ShopEntry> buyableSkins = List.of(
        new ShopEntry(new Player("Bob"), 0, 0),
        new ShopEntry(new Player("Mike"), 0, 0),
        new ShopEntry(new Player("Sakura"), 0, 0)
    );

    private final List<ShopEntry> buyableAccessories = List.of(
        new ShopEntry(new Accessory.Hat("fedora", RARITY.RARE), 0, 0),
        new ShopEntry(new Accessory.Tie("tie", RARITY.EPIC), 100, 0),
        new ShopEntry(new Accessory.Headpiece("coquette", RARITY.MYTHIC), 0, 0),
        new ShopEntry(new Accessory.Necklace("dollar", RARITY.RARE), 200, 0),
        new ShopEntry(new Accessory.Necklace("sorcerer", RARITY.MYTHIC), 250, 0),
        new ShopEntry(new Accessory.Pin("star", RARITY.LEGENDARY), 50000, 0),
        new ShopEntry(new Accessory.Pin("sheriff", RARITY.RARE), 1075, 0)
    );

    private final List<ShopEntry> buyableBuffs = List.of(
        new ShopEntry(new Buff.SpeedBuff(0, 0), 1, 0),
        new ShopEntry(new Buff.ShieldBuff(0, 0), 0, 0),
        new ShopEntry(new Buff.ShrinkBuff(0, 0), 0, 0),
        new ShopEntry(new Buff.MagnetBuff(0, 0), 0, 0),
        new ShopEntry(new Buff.VisionBuff(0, 0), 0, 0)
    );

    private final List<ShopEntry> buyableGuns = List.of(
        new ShopEntry(new Gun.Handgun(), 0, 0),
        new ShopEntry(new Gun.Uzi(), 0, 0),
        new ShopEntry(new Gun.Shotgun(), 0, 0),
        new ShopEntry(new Gun.MachineGun(), 0, 0),
        new ShopEntry(new Gun.Staff(), 0, 0),
        new ShopEntry(new Gun.Launcher(), 0, 0),
        new ShopEntry(new Gun.Pacifist(), 0, 0)
    );


    private final List<ShopEntry>[][] buyablesPerPage = new List[][] {
        new List[] { buyableSkins, buyableAccessories },
        new List[] { buyableBuffs },
        new List[] { buyableGuns }
    };

}
