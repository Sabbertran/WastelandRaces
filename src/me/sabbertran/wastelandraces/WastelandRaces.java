package me.sabbertran.wastelandraces;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import me.sabbertran.wastelandraces.commands.RaceCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WastelandRaces extends JavaPlugin
{

    static final Logger log = Bukkit.getLogger();
    private File raceFile;
    private File playerRaceFile;
    private File lastChangeFile;
    private File skipCheckFile;
    private Map<String, Race> races = new HashMap<String, Race>(); //Racename, Race
    private Map<String, String> players = new HashMap<String, String>(); //Playername, Race
    private Map<String, Date> lastChange = new HashMap<String, Date>(); //Playername, Date
    private ArrayList<String> skipCheck = new ArrayList<String>();

    @Override
    public void onDisable()
    {
        try
        {
            PrintWriter pw = new PrintWriter(lastChangeFile);
            for (Map.Entry<String, Date> entry : lastChange.entrySet())
            {
                int year = entry.getValue().getYear() + 1900;
                int month = entry.getValue().getMonth() + 1;
                int day = entry.getValue().getDate();
                int hours = entry.getValue().getHours();
                int minutes = entry.getValue().getMinutes();
                int seconds = entry.getValue().getSeconds();
                String date = year + "-" + month + "-" + day + "-" + hours + "-" + minutes + "-" + seconds;
                String print = entry.getKey() + ":" + date;
                pw.println(print);
            }
            pw.close();
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        try
        {
            PrintWriter pw = new PrintWriter(playerRaceFile);
            for (Map.Entry<String, String> entry : players.entrySet())
            {
                String name = entry.getKey();
                String race = entry.getValue();
                String print = name + ":" + race;
                pw.println(print);
            }
            pw.close();
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        log.info("WastelandRaces disabled");
    }

    @Override
    public void onEnable()
    {

        this.getConfig().addDefault("WastelandRaces.changeTime", 7);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        raceFile = new File(getDataFolder(), "races.yml");
        if (!raceFile.exists())
        {
            raceFile.getParentFile().mkdirs();
            copy(getResource("races.yml"), raceFile);
        }
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(raceFile))));
            String text;

            while ((text = br.readLine()) != null)
            {
                if (!text.startsWith("#"))
                {
                    String[] eff = text.split(":");
                    if (eff.length == 3)
                    {
                        String name = eff[0];
                        PotionEffect e = new PotionEffect(PotionEffectType.getByName(eff[1]), Integer.MAX_VALUE, (Integer.valueOf(eff[2]) - 1));
                        if (this.races.containsKey(name))
                        {
                            Race ra = this.races.get(name);
                            ra.getEffects().add(e);
                        } else
                        {
                            ArrayList<PotionEffect> efflist = new ArrayList<PotionEffect>();
                            efflist.add(e);
                            Race ra = new Race(name, efflist);
                            this.races.put(name, ra);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        lastChangeFile = new File(getDataFolder(), "lastchanges.yml");
        if (!lastChangeFile.exists())
        {
            lastChangeFile.getParentFile().mkdirs();
            try
            {
                lastChangeFile.createNewFile();
            } catch (IOException ex)
            {
                System.err.println(ex.getMessage());
            }
        }
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(lastChangeFile))));
            String text;

            while ((text = br.readLine()) != null)
            {
                if (!text.startsWith("#"))
                {
                    String[] line = text.split(":");
                    String name = line[0];
                    String[] date = line[1].split("-");
                    int year = Integer.parseInt(date[0]);
                    int month = Integer.parseInt(date[1]);
                    int day = Integer.parseInt(date[2]);
                    int hours = Integer.parseInt(date[3]);
                    int minutes = Integer.parseInt(date[4]);
                    int seconds = Integer.parseInt(date[5]);

                    Date d = new Date(year - 1900, month - 1, day, hours, minutes, seconds);
                    this.lastChange.put(name, d);
                }
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        playerRaceFile = new File(getDataFolder(), "playerraces.yml");
        if (!playerRaceFile.exists())
        {
            playerRaceFile.getParentFile().mkdirs();
            try
            {
                playerRaceFile.createNewFile();
            } catch (IOException ex)
            {
                System.err.println(ex.getMessage());
            }
        }

        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(playerRaceFile))));
            String text;

            while ((text = br.readLine()) != null)
            {
                if (!text.startsWith("#"))
                {
                    String[] line = text.split(":");
                    String name = line[0];
                    String race = line[1];
                    this.players.put(name, race);
                }
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        skipCheckFile = new File(getDataFolder(), "skipcheck.yml");
        if (!skipCheckFile.exists())
        {
            skipCheckFile.getParentFile().mkdirs();
            try
            {
                skipCheckFile.createNewFile();
            } catch (IOException ex)
            {
                System.err.println(ex.getMessage());
            }
        }
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(skipCheckFile))));
            String name;
            while ((name = in.readLine()) != null)
            {
                this.skipCheck.add(name);
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        this.getServer().getPluginManager().registerEvents(new Events(this), this);
        this.getCommand("race").setExecutor(new RaceCommand(this));

        log.info("WastelandRaces enabled");
    }

    public void addPlayer(String p, String race) throws IllegalArgumentException
    {
        if (this.races.containsKey(race))
        {
            this.players.put(p, race);
        } else
        {
            throw new IllegalArgumentException();
        }
    }

    public ArrayList<PotionEffect> getEffects(String race)
    {
        if (this.races.containsKey(race))
        {
            return this.races.get(race).getEffects();
        }
        return null;
    }

    public Race getPlayerRace(String p)
    {
        if (this.players.containsKey(p))
        {
            String race = this.players.get(p);
            Race r = this.races.get(race);
            return r;
        }
        return null;
    }

    public void setLastChange(String p, Date d)
    {
        this.lastChange.put(p, d);
    }

    public Date getLastChange(String p)
    {
        return this.lastChange.get(p);
    }

    public Map<String, Race> getRaces()
    {
        return races;
    }

    public ArrayList<String> getSkipCheck()
    {
        return this.skipCheck;
    }

    private void copy(InputStream in, File file)
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
