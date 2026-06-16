package game;

import helpers.InputHandler;
import game.GameState.STATE;
import helpers.ShopEntry;
import mapobjects.mapobject.Accessory;
import mapobjects.mapobject.Buff;
import mapobjects.mapobject.Gun;
import mapobjects.mapobject.Player;
import java.util.List;

public class Shop {

    private final List<ShopEntry> buyableSkins = List.of(
        new ShopEntry(new Player("Mike"), 0, true),
        new ShopEntry(new Player("Sakura"), 0, true)
    );

    private final List<ShopEntry> buyableAccessories = List.of(
        new ShopEntry(new Accessory.Hat("fedora"), 0, true),
        new ShopEntry(new Accessory.Tie("tie"), 100, true),
        new ShopEntry(new Accessory.Headpiece("coquette"), 0, true),
        new ShopEntry(new Accessory.Necklace("dollar"), 200, true),
        new ShopEntry(new Accessory.Necklace("sorcerer"), 250, true),
        new ShopEntry(new Accessory.Pin("star"), 50000, true),
        new ShopEntry(new Accessory.Pin("sheriff"), 1075, true)
    );

    private final List<ShopEntry> buyableBuffs = List.of(
        new ShopEntry(new Buff.SpeedBuff(0, 0), 1, false),
        new ShopEntry(new Buff.ShieldBuff(0, 0), 0, false),
        new ShopEntry(new Buff.ShrinkBuff(0, 0), 0, false),
        new ShopEntry(new Buff.MagnetBuff(0, 0), 0, false),
        new ShopEntry(new Buff.VisionBuff(0, 0), 0, false)
    );

    private final List<ShopEntry> buyableGuns = List.of(
        new ShopEntry(new Gun(Gun.GunType.UZI), 0, false),
        new ShopEntry(new Gun(Gun.GunType.SHOTGUN), 0, false),
        new ShopEntry(new Gun(Gun.GunType.STAFF), 0, false)
    );



    private static final STATE[] SHOP_STATES = new STATE[]{
        STATE.SELECTION, STATE.SHOP, STATE.ALTERNATE1, STATE.ALTERNATE2, null
    };

    private final List<ShopEntry>[][] buyablesPerPage = new List[][]{
        new List[]{buyableSkins, buyableAccessories},
        new List[]{buyableBuffs},
        new List[]{}
    };

    private static final int TOTAL_PAGE_NUM = SHOP_STATES.length - 2;

    private final GameState gameState;
    private final ShopPage[] pages = new ShopPage[TOTAL_PAGE_NUM];


    public Shop(InputHandler inputHandler, GameState gameState) throws Exception {
        this.gameState = gameState;
        configure(inputHandler, gameState);
    }

    private void configure(InputHandler inputHandler, GameState gameState) throws Exception {
        if (buyablesPerPage.length != TOTAL_PAGE_NUM) throw new Exception("WRONG NUMBER OF STATES OR BUYABLES_PER_PAGE");
        for (int i = 0; i<TOTAL_PAGE_NUM; i++) {
            pages[i] = new ShopPage(inputHandler, gameState);
            pages[i].configure(SHOP_STATES[i], SHOP_STATES[i+1], SHOP_STATES[i+2], buyablesPerPage[i]);
        }
    }

    public void shopLoop() throws Exception {

        STATE state = gameState.getState();
        for (int i = 0; i<TOTAL_PAGE_NUM; i++) {
            if (state == SHOP_STATES[i+1]) {
                pages[i].shopLoop();
            }
        }

    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    // SHOP ENTRIES
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------



}
