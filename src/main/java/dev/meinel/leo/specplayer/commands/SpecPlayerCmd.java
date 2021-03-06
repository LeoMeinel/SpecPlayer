/*
 * SpecPlayer is a Spigot Plugin that gives players the ability to spectate others.
 * Copyright © 2022 Leopold Meinel & contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://github.com/LeoMeinel/SpecPlayer/blob/main/LICENSE
 */

package dev.meinel.leo.specplayer.commands;

import dev.meinel.leo.specplayer.utils.commands.Cmd;
import dev.meinel.leo.specplayer.utils.commands.CmdSpec;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpecPlayerCmd
		implements CommandExecutor {

	private static final Map<UUID, Location> lastLocation = new HashMap<>();

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
	                         @NotNull String[] args) {
		if (Cmd.isArgsLengthGreaterThan(sender, args, 1)) {
			return false;
		}
		if (args.length == 0) {
			doBack(sender);
			return true;
		}
		doSpec(sender, args);
		return true;
	}

	private void doBack(@NotNull CommandSender sender) {
		if (CmdSpec.isInvalidCmd(sender, "specplayer.spectate", lastLocation)) {
			return;
		}
		Player senderPlayer = (Player) sender;
		UUID senderUUID = senderPlayer.getUniqueId();
		senderPlayer.setGameMode(GameMode.SURVIVAL);
		if (lastLocation.containsKey(senderUUID)) {
			senderPlayer.teleport(lastLocation.get(senderUUID));
			senderPlayer.setAllowFlight(true);
			senderPlayer.setFlying(true);
			lastLocation.remove(senderUUID);
		}
	}

	private void doSpec(@NotNull CommandSender sender, @NotNull String[] args) {
		Player player = Bukkit.getPlayer(args[0]);
		if (CmdSpec.isInvalidCmd(sender, player, "specplayer.spectate")) {
			return;
		}
		Player senderPlayer = (Player) sender;
		UUID senderUUID = senderPlayer.getUniqueId();
		assert player != null;
		lastLocation.computeIfAbsent(senderUUID, key -> senderPlayer.getLocation());
		senderPlayer.setGameMode(GameMode.SPECTATOR);
		senderPlayer.teleport(player.getLocation());
	}
}
