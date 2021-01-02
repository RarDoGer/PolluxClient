package rardoger.polluxclient.modules.render.hud.modules;

import rardoger.polluxclient.modules.ModuleManager;
import rardoger.polluxclient.modules.player.InfinityMiner;
import rardoger.polluxclient.modules.render.hud.HUD;

public class InfiniteMineHud extends DoubleTextHudModule {
    public InfiniteMineHud(HUD hud) {
        super(hud, "infmine", "Displays details regarding Infinity Mine.", "Infinity Mine: ");
    }

    @Override
    protected String getRight() {
        InfinityMiner infinityMiner = ModuleManager.INSTANCE.get(InfinityMiner.class);
        if (!infinityMiner.isActive()) return "Disabled";

        switch (infinityMiner.getMode()) {
            case Home:
                int[] coords = infinityMiner.getHomeCoords();
                return "Heading Home: " + coords[0] + " " + coords[1] + " " + coords[2];
            case Target:
                return "Mining: " + infinityMiner.getCurrentTarget().getName().getString();
            case Repair:
                return "Repair-Mining: " + infinityMiner.getCurrentTarget().getName().getString();
            case Still:
                return "Resting";
            default:
                return "";
        }
    }
}
