package me.sabbertran.wastelandraces;

import java.io.Serializable;
import java.util.ArrayList;
import org.bukkit.potion.PotionEffect;

public class Race implements Serializable {

    private String name;
    private ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();

    public Race(String name, ArrayList<PotionEffect> eff) {
        this.name = name;
        this.effects = eff;
    }

    public ArrayList<PotionEffect> getEffects() {
        return this.effects;
    }
    
    public String getName(){
        return this.name;
    }
}
