package game.ui;

import data.ShopData;
import game.ui.components.ShopEntry;
import game.core.GameState;
import game.io.InputHandler;
import game.core.GameState.STATE;

import java.util.List;

public class Shop {

    private final List<ShopEntry>[][] buyablesPerPage = (new ShopData()).getBuyablesPerPage();

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

}
