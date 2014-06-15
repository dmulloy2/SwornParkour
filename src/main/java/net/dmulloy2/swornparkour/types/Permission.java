package net.dmulloy2.swornparkour.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.types.IPermission;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public enum Permission implements IPermission
{
	CMD_RELOAD("cmd.reload"),
	CMD_SETPOINT("cmd.setpoint"),
	CMD_CREATE("cmd.create"),
	CMD_ABANDON("cmd.abandon"),
	CMD_KICK("cmd.kick"),
	CMD_DELETE("cmd.delete"),
	CMD_SPAWN("cmd.spawn"),
	BUILD("build");
	
	private final String node;
}