package nl.infinityastro.asteroreceives.menusystem;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerMenuUtility {

    private Player owner;
    private UUID target;

    public PlayerMenuUtility(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getTarget() {
        return target;
    }
}

