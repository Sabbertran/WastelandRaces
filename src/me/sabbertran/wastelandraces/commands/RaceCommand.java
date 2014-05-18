package me.sabbertran.wastelandraces.commands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import me.sabbertran.wastelandraces.Race;
import me.sabbertran.wastelandraces.WastelandRaces;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class RaceCommand implements CommandExecutor
{

    private WastelandRaces main;
    private int changeTime;

    public RaceCommand(WastelandRaces wr)
    {
        this.main = wr;
        this.changeTime = main.getConfig().getInt("WastelandRaces.changeTime");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("join"))
            {
                if (sender instanceof Player)
                {
                    Player p = (Player) sender;
                    if (args.length == 2)
                    {
                        String perm = "wastelandraces.join." + args[1];
                        if (p.hasPermission(perm))
                        {
                            if (this.main.getPlayerRace(p.getName()) == null || !this.main.getPlayerRace(p.getName()).getName().equals(args[1]))
                            {
                                boolean skip;
                                Calendar c = new GregorianCalendar();
                                if (main.getLastChange(p.getName()) != null)
                                {
                                    c.setTime(this.main.getLastChange(p.getName()));
                                    skip = false;
                                } else
                                {
                                    skip = true;
                                }
                                if (this.main.getSkipCheck().contains(p.getName()))
                                {
                                    skip = true;
                                }
                                c.add(Calendar.DATE, changeTime);
                                Date valid = c.getTime();
                                Date now = new Date();

                                if (!skip)
                                {
                                    if (now.after(valid))
                                    {
                                        try
                                        {
                                            this.main.addPlayer(p.getName(), args[1]);
                                            for (PotionEffect pe : p.getActivePotionEffects())
                                            {
                                                p.removePotionEffect(pe.getType());
                                            }
                                            ArrayList<PotionEffect> effects = this.main.getPlayerRace(p.getName()).getEffects();
                                            p.addPotionEffects(effects);
                                            p.sendMessage("You joined the race " + args[1]);
                                            this.main.setLastChange(p.getName(), now);
                                        } catch (IllegalArgumentException e)
                                        {
                                            p.sendMessage("This race does not exist");
                                        }
                                    } else
                                    {
                                        p.sendMessage("You are not allowed to change your race yet.");
                                    }
                                } else
                                {
                                    try
                                    {
                                        this.main.addPlayer(p.getName(), args[1]);
                                        for (PotionEffect pe : p.getActivePotionEffects())
                                        {
                                            p.removePotionEffect(pe.getType());
                                        }
                                        ArrayList<PotionEffect> effects = this.main.getPlayerRace(p.getName()).getEffects();
                                        p.addPotionEffects(effects);
                                        p.sendMessage("You joined the race " + args[1]);
                                        this.main.setLastChange(p.getName(), now);
                                    } catch (IllegalArgumentException e)
                                    {
                                        p.sendMessage("This race does not exist");
                                    }
                                }
                            } else
                            {
                                p.sendMessage("You cannot join the race you are already in");
                            }
                        } else
                        {
                            p.sendMessage("§cYou don't have permission to use this command");
                        }
                    } else
                    {
                        p.sendMessage("Use §7/race join 'name' §fto join a race");
                    }
                } else
                {
                    sender.sendMessage("You have to be a player to use this command");
                }
            } else if (args[0].equalsIgnoreCase("list"))
            {
                if (sender.hasPermission("wastelandraces.list"))
                {
                    String msg = "";
                    for (String r : this.main.getRaces().keySet())
                    {
                        msg = msg + r + ", ";
                    }
                    StringBuilder sb = new StringBuilder(msg);
                    sb.deleteCharAt(msg.length() - 1);
                    sb.deleteCharAt(msg.length() - 2);
                    sender.sendMessage(sb.toString());
                } else
                {
                    sender.sendMessage("You don't have permission");
                }
            }
        } else
        {
            if (sender instanceof Player)
            {
                Player p = (Player) sender;
                Race race = this.main.getPlayerRace(p.getName());
                if (race != null)
                {
                    sender.sendMessage("You are in race §8" + race.getName());
                } else
                {
                    sender.sendMessage("You are currently in no race");
                }
            }
            sender.sendMessage("Use §7/race join 'name' §for §7/race list");
        }

        return true;
    }
}