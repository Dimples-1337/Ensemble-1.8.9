package it.fktcod.ktykshrk.command;


import it.fktcod.ktykshrk.utils.visual.ChatUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Effect extends Command
{
	public Effect()
	{
		super("effect");
	}

	/*
	 * @Override public void runCommand(String s, String[] args) { try {
	 * if(args[0].equalsIgnoreCase("add")) { int id = Integer.parseInt(args[1]); int
	 * duration = Integer.parseInt(args[2]); int amplifier =
	 * Integer.parseInt(args[3]); if(Potion.getDurationString(id)== null) {
	 * ChatUtils.error("Potion is null"); return; } Utils.addEffect(id, duration,
	 * amplifier); } else if(args[0].equalsIgnoreCase("remove")) { int id =
	 * Integer.parseInt(args[1]); if(Potion.getPotionById(id) == null) {
	 * ChatUtils.error("Potion is null"); return; } Utils.removeEffect(id); } else
	 * if(args[0].equalsIgnoreCase("clear")) { Utils.clearEffects(); } }
	 * catch(Exception e) { ChatUtils.error("Usage: " + getSyntax()); } }
	 */
	@Override
	public String getDescription()
	{
		return "Effect manager.";
	}

	@Override
	public String getSyntax()
	{
		return "effect <add/remove/clear> <id> <duration> <amplifier>";
	}

	@Override
	public void runCommand(String s, String[] args) {
		// TODO �Զ����ɵķ������
		
	}
}