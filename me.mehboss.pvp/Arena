package me.mehboss.pvp;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Arena {
	
    private ArenaState state;
    private String name;
    private Location location1;
    private Location location2;
    private ArrayList<Player> queue = new ArrayList<>();
    
    public Arena(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
	public ArenaState getState() {
        return state;
    }
    
    public Location getLocation1() {
    	return location1;
    }
    
    public Location getLocation2() {
    	return location2;
    }
    
    public ArrayList<Player> getQueue() {
    	return queue;
    }
    
    public void addQueue(Player p) {
    	queue.add(p);
    }
    
    public void removeQueue(Player p) {
    	queue.remove(p);
    }
    
    public void clearQueue() {
    	queue.clear();
    }
    
    public void setState(ArenaState state) {
        this.state = state;
    }
    
    public void setLocation1(Location location) {
    	this.location1 = location;
    }
    
    public void setLocation2(Location location) {
    	this.location2 = location;
    }
    
    
}
