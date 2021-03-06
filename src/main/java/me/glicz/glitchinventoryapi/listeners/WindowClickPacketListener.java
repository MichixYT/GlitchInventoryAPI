package me.glicz.glitchinventoryapi.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.glicz.glitchinventoryapi.GlitchInventoryAPI;
import me.glicz.glitchinventoryapi.types.ClickType;
import me.glicz.glitchinventoryapi.types.GlitchInventory;
import me.glicz.glitchinventoryapi.types.SlotClickEvent;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.function.Consumer;

public class WindowClickPacketListener extends PacketAdapter {

    public WindowClickPacketListener() {
        super(GlitchInventoryAPI.getPlugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.WINDOW_CLICK);
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        if (!GlitchInventory.getCurrentInventories().containsKey(e.getPlayer().getUniqueId())) return;
        GlitchInventory glitchInventory = GlitchInventory.getCurrentInventories().get(e.getPlayer().getUniqueId());
        glitchInventory.update();
        e.getPlayer().setItemOnCursor(null);
        e.getPlayer().updateInventory();
        HashMap<Integer, Consumer<? super SlotClickEvent>> listenerMap = glitchInventory.getSlotClickListeners();
        if (listenerMap.containsKey(e.getPacket().getIntegers().read(2))) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(
                    GlitchInventoryAPI.getPlugin(),
                    () -> listenerMap.get(e.getPacket().getIntegers().read(2)).accept(
                            new SlotClickEvent(e.getPlayer(),
                                    glitchInventory.getItems()[e.getPacket().getIntegers().read(2)],
                                    e.getPacket().getIntegers().read(2),
                                    glitchInventory,
                                    ClickType.get(((Enum<?>) e.getPacket().getModifier().read(4)).ordinal(),
                                            e.getPacket().getIntegers().read(3)))));
        }
    }
}
