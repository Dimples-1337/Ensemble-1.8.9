package it.fktcod.ktykshrk.command;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import it.fktcod.ktykshrk.managers.SkinChangerManager;
import it.fktcod.ktykshrk.utils.LoginUtils;
import it.fktcod.ktykshrk.utils.visual.ChatUtils;
import it.fktcod.ktykshrk.wrappers.Wrapper;
import net.minecraft.item.ItemStack;

import net.minecraft.util.ResourceLocation;

public class SkinSteal extends Command
{
	public SkinSteal()
	{
		super("skinsteal");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			SkinChangerManager.addTexture(Type.SKIN, args[0]);
			//if(args[0].equalsIgnoreCase("add")) {
				//SkinChangerManager.setTexture(Type.valueOf(args[1].toUpperCase()), args[2]);
//			} else 
//				if(args[0].equalsIgnoreCase("remove")) {
//					SkinChangerManager.removeTexture(Type.valueOf(args[1].toUpperCase()));
//				}
//				else 
//					if(args[0].equalsIgnoreCase("clear")) {
//						SkinChangerManager.clear();
//					}
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription()
	{
		return "Steal skin for 'SkinChanger'.";
	}

	@Override
	public String getSyntax()
	{
		return "skinsteal <nickname/URL>";
	}
}