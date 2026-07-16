package data;

import game.ui.components.ShopEntry;
import mapobjects.entities.Accessory;
import mapobjects.entities.Buff;
import mapobjects.entities.Gun;
import mapobjects.entities.Player;
import mapobjects.traits.schemas.Equippable;

import java.util.List;

public class ShopData {

    private final List<ShopEntry> buyableSkins = List.of(
        new ShopEntry(new Player("Bob"), 0, 0),
        new ShopEntry(new Player("Mike"), 0, 0),
        new ShopEntry(new Player("Sakura"), 0, 0)
    );

    private final List<ShopEntry> buyableAccessories = List.of(
        new ShopEntry(new Accessory.Hat("fedora", Equippable.RARITY.RARE), 0, 0),
        new ShopEntry(new Accessory.Tie("tie", Equippable.RARITY.EPIC), 100, 0),
        new ShopEntry(new Accessory.Headpiece("coquette", Equippable.RARITY.MYTHIC), 0, 0),
        new ShopEntry(new Accessory.Necklace("dollar", Equippable.RARITY.RARE), 200, 0),
        new ShopEntry(new Accessory.Necklace("sorcerer", Equippable.RARITY.MYTHIC), 250, 0),
        new ShopEntry(new Accessory.Pin("star", Equippable.RARITY.LEGENDARY), 50000, 0),
        new ShopEntry(new Accessory.Pin("sheriff", Equippable.RARITY.RARE), 1075, 0)
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

    public List<ShopEntry>[][] getBuyablesPerPage() {
        return buyablesPerPage;
    }

}
