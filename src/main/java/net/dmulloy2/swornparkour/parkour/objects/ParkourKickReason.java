package net.dmulloy2.swornparkour.parkour.objects;

/**
 * @author dmulloy2
 */

public enum ParkourKickReason 
{
	TOO_MANY_DEATHS("deaths"),
	FORCE_KICK("kick"),
	SHUTDOWN("disable"),
	QUIT("quit"),
	LEAVE("leave");
	
	public String name;
	ParkourKickReason(String name)
	{
		this.name = name;
	}
}
