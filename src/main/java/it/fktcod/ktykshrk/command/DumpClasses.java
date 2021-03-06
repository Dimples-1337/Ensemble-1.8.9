package it.fktcod.ktykshrk.command;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Vector;

import it.fktcod.ktykshrk.utils.Utils;
import it.fktcod.ktykshrk.utils.visual.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.objectweb.asm.ClassReader;

public class DumpClasses extends Command
{
	public DumpClasses()
	{
		super("dumpclasses");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			ArrayList<String> list = new ArrayList<String>();
			
			Field f = ClassLoader.class.getDeclaredField("classes");
			f.setAccessible(true);

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Vector<Class> classes =  (Vector<Class>) f.get(classLoader);
			
	        for (Class<?> clazz: classes) {
	        	String className = clazz.getName();
	        	if(args.length > 0) {
	        		if(className.contains(args[0])){
	        			list.add("\n" + className);
					}
	        		//写到文件(获取ClassByte(className),目录)

	        	} else { list.add("\n" + className); }
	        }
	        
	        if(list.isEmpty()) {
				ChatUtils.error("List is empty.");
			}
			else
			{
				Utils.copy(list.toString());
				ChatUtils.message("List copied to clipboard.");
			}
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription()
	{
		return "Get classes from ClassLoader by regex.";
	}

	@Override
	public String getSyntax()
	{
		return "dumpclasses <regex>";
	}
}