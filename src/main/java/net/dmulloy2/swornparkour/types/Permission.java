package net.dmulloy2.swornparkour.types;

/**
 * @author dmulloy2
 */

public enum Permission
{
	CMD_RELOAD("cmd.reload"),
	CMD_SETPOINT("cmd.setpoint"),
	CMD_CREATE("cmd.create"),
	CMD_ABANDON("cmd.abandon"),
	CMD_KICK("cmd.kick"),
	CMD_DELETE("cmd.delete"),
	CMD_SPAWN("cmd.spawn"),
	BUILD("build");
	
	public final String node;
	Permission(final String node) 
	{
		this.node = node;
	}
}