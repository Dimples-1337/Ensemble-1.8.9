package it.fktcod.ktykshrk.ui.click;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import it.fktcod.ktykshrk.ForgeMod;
import it.fktcod.ktykshrk.command.Command;
import it.fktcod.ktykshrk.managers.CommandManager;
import it.fktcod.ktykshrk.managers.FileManager;
import it.fktcod.ktykshrk.managers.HackManager;
import it.fktcod.ktykshrk.module.Module;
import it.fktcod.ktykshrk.ui.GuiTextField;
import it.fktcod.ktykshrk.ui.Tooltip;
import it.fktcod.ktykshrk.utils.Utils;
import it.fktcod.ktykshrk.utils.visual.ChatUtils;
import it.fktcod.ktykshrk.utils.visual.ColorUtils;
import it.fktcod.ktykshrk.wrappers.Wrapper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGuiScreen extends GuiScreen {

	public static final String AUTHOR_TEXT = "Coded by Gish_Reloaded/Recoded by Zenwix";
    public static ClickGui clickGui;
    public static int[] mouse = new int[2];
    public static Tooltip tooltip = null;
    String title = AUTHOR_TEXT;
	ArrayList cmds = new ArrayList();
	GuiTextField console;
	

	public ClickGuiScreen() {
		this.cmds.clear();
        for(Command c : CommandManager.commands){
        	this.cmds.add(c.getCommand() + " - " + c.getDescription());
        }
	}
   
   @Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		this.console.mouseClicked(x, y, button);
   }
   
   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	   	tooltip = null;
       	clickGui.render();
       	if(tooltip != null) tooltip.render();
       	int mainColor = it.fktcod.ktykshrk.module.mods.ClickGui.isLight ? ColorUtils.color(255, 255, 255, 255) : ColorUtils.color(0, 0, 0, 255);
       	//this.console.drawTextBox(cn.zenwix.ensemble.hack.hacks.ClickGui.getColor(), mainColor);
		//this.console.setTextColor(cn.zenwix.ensemble.hack.hacks.ClickGui.getColor());
		super.drawScreen(mouseX, mouseY, partialTicks);
   }
   
   @Override
   public void initGui() {
	   Keyboard.enableRepeatEvents(true);
       this.console = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 0, 200, 14);
       this.console.setMaxStringLength(500);
       this.console.setText(title);
       this.console.setFocused(!Utils.isMoving(Wrapper.INSTANCE.player()));
	super.initGui();
   }
    
    @Override
	public void updateScreen() {
		this.console.updateCursorCounter();
		clickGui.onUpdate();
		super.updateScreen();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}
	
	void setTitle() {
		if(!console.getText().equals(AUTHOR_TEXT)) title = "";
	}
	
	private boolean handleKeyScroll(int key) {
		if(Utils.isMoving(Wrapper.INSTANCE.player())) return false;
		if (key == Keyboard.KEY_W) return clickGui.onMouseScroll(3); else 
        if (key == Keyboard.KEY_S) return clickGui.onMouseScroll(-3);
		return false;
	}
	
	private void handleKeyboard() {
		if (Keyboard.isCreated()) {
            Keyboard.enableRepeatEvents(true);
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                	if(!this.handleKeyScroll(Keyboard.getEventKey()))
                		console.textboxKeyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
                    if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
                        setTitle();
                        CommandManager.getInstance().runCommands("." + console.getText());
                        mc.displayGuiScreen((GuiScreen)null);
                        FileManager.saveHacks();
                        FileManager.saveClickGui();
                    } else
                    if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                    	setTitle();
                        mc.displayGuiScreen(null);
                        FileManager.saveHacks();
                        FileManager.saveClickGui();
                    } else {
                        clickGui.onkeyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                    }
                } else {
                    clickGui.onKeyRelease(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                }
            }
        }
	}
	
	private void handleMouse() {
		if (Mouse.isCreated()) {
            while (Mouse.next()) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                int mouseX = Mouse.getEventX() * scaledResolution.getScaledWidth() / mc.displayWidth;
                int mouseY = scaledResolution.getScaledHeight() - Mouse.getEventY() * scaledResolution.getScaledHeight() / mc.displayHeight - 1;
                if (Mouse.getEventButton() == -1) {
                    clickGui.onMouseScroll((Mouse.getEventDWheel() / 100) * 3);
                    clickGui.onMouseUpdate(mouseX, mouseY);
                    mouse[0] = mouseX;
                    mouse[1] = mouseY;
                } else if (Mouse.getEventButtonState()) {
                    clickGui.onMouseClick(mouseX, mouseY, Mouse.getEventButton());
                } else {
                    clickGui.onMouseRelease(mouseX, mouseY);
                }
            }
        }
	}
	
    @Override
    public void handleInput() throws IOException {
    	try {
	        int scale = mc.gameSettings.guiScale;
	        mc.gameSettings.guiScale = 2;
	        this.handleKeyboard();
	        this.handleMouse();
	        mc.gameSettings.guiScale = scale;
	        super.handleInput();
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		ChatUtils.error("Exception: handleInput");
    		ChatUtils.error(ex.toString());
    		Utils.copy(ex.toString());
    	}
    }
}
