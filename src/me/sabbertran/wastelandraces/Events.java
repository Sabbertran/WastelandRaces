package me.sabbertran.wastelandraces;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class Events implements Listener
{

    private WastelandRaces main;

    public Events(WastelandRaces wr)
    {
        this.main = wr;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        Race r = this.main.getPlayerRace(p.getName());
        if (r != null)
        {
            ArrayList<PotionEffect> eff = r.getEffects();
            for (PotionEffect ef : eff)
            {
                p.addPotionEffect(ef);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Player p = e.getPlayer();
                Race r = main.getPlayerRace(p.getName());
                if (r != null)
                {
                    ArrayList<PotionEffect> eff = r.getEffects();
                    for (PotionEffect ef : eff)
                    {
                        p.addPotionEffect(ef);
                    }
                }
            }
        }.runTaskLater(main, 1L);
    }
}
