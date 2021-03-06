package it.fktcod.ktykshrk.module.mods;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import it.fktcod.ktykshrk.Core;
import it.fktcod.ktykshrk.module.HackCategory;
import it.fktcod.ktykshrk.module.Module;
import it.fktcod.ktykshrk.utils.BlockUtils;
import it.fktcod.ktykshrk.utils.PlayerControllerUtils;
import it.fktcod.ktykshrk.utils.Utils;
import it.fktcod.ktykshrk.utils.visual.RenderUtils;
import it.fktcod.ktykshrk.value.Mode;
import it.fktcod.ktykshrk.value.ModeValue;
import it.fktcod.ktykshrk.value.NumberValue;
import it.fktcod.ktykshrk.wrappers.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Nuker extends Module{
	
	public ModeValue mode;
    public NumberValue distance;
    
    public final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<Set<BlockPos>>();
    public BlockPos currentBlock;
    public float progress;
    public float prevProgress;
    public int id;
	
	public Nuker() {
		super("Nuker", HackCategory.PLAYER);
		
		this.mode = new ModeValue("Mode", new Mode("ID", true), new Mode("All", false));
		distance = new NumberValue("Distance", 6.0D, 0.1D, 6.0D);
		
		this.addValue(mode, distance);
		this.setChinese(Core.Translate_CN[69]);
	}
	
	@Override
	public String getDescription() {
		return "Automatically breaks blocks around you.";
	}
	
	@Override
	public void onDisable() {
		if(currentBlock != null) {
			PlayerControllerUtils.setIsHittingBlock(true);
			Wrapper.INSTANCE.controller().resetBlockRemoving();
			currentBlock = null;
		}
		prevBlocks.clear();
		id = 0;
		super.onDisable();
	}
	
	@Override
	public void onClientTick(ClientTickEvent event) {
		currentBlock = null;
		
		Vec3 eyesPos = Utils.getEyesPos().subtract(0.5, 0.5, 0.5);
		BlockPos eyesBlock = new BlockPos(Utils.getEyesPos());
		
		double rangeSq = Math.pow(distance.getValue().doubleValue(), 2);
		int blockRange = (int)Math.ceil(distance.getValue().doubleValue());
		
		Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(
						eyesBlock.add(blockRange, blockRange, blockRange),
						eyesBlock.add(-blockRange, -blockRange, -blockRange)).spliterator(), true);
		
		stream = stream.filter(pos -> eyesPos.squareDistanceTo(new Vec3(pos)) <= rangeSq)
				.filter(pos -> BlockUtils.canBeClicked(pos))
				.sorted(Comparator.comparingDouble(pos -> eyesPos.squareDistanceTo(new Vec3(pos))));
		
		if(mode.getMode("ID").isToggled()) {
			stream = stream.filter(pos -> Block.getIdFromBlock(BlockUtils.getBlock(pos)) == id);
		} 
		else if(mode.getMode("All").isToggled()) {
			//stream = stream.filter(pos -> BlockUtils.getHardness(pos) >= 1);
		}
		
		List<BlockPos> blocks = stream.collect(Collectors.toList());
		
		if(Wrapper.INSTANCE.player().capabilities.isCreativeMode){
			Stream<BlockPos> stream2 = blocks.parallelStream();
			
			for(Set<BlockPos> set : prevBlocks) {
				stream2 = stream2.filter(pos -> !set.contains(pos));
			}
			
			List<BlockPos> blocks2 = stream2.collect(Collectors.toList());
			prevBlocks.addLast(new HashSet<>(blocks2));
			
			while(prevBlocks.size() > 5) {
				prevBlocks.removeFirst();
			}
			
			if(!blocks2.isEmpty()) {
				currentBlock = blocks2.get(0);
			}
			
			Wrapper.INSTANCE.controller().resetBlockRemoving();
			progress = 1;
			prevProgress = 1;
			BlockUtils.breakBlocksPacketSpam(blocks2);
			return;
		}
		
		for(BlockPos pos : blocks)
			if(BlockUtils.placeBlockSimple(pos)){
				currentBlock = pos;
				break;
			}
		
		if(currentBlock == null) {
			Wrapper.INSTANCE.controller().resetBlockRemoving();
		}
		
		if(currentBlock != null && BlockUtils.getHardness(currentBlock) < 1) {
			prevProgress = progress;
		}
		
		progress = PlayerControllerUtils.getCurBlockDamageMP();	
			
		if(progress < prevProgress) {
			prevProgress = progress;
		} else {
			progress = 1;
			prevProgress = 1;
		}
		super.onClientTick(event);
	}
	
	@Override
	public void onLeftClickBlock(BlockEvent event) {
	
		if(mode.getMode("ID").isToggled() && Wrapper.INSTANCE.world().isRemote) {
			IBlockState blockState = BlockUtils.getState(event.pos);
			id = Block.getIdFromBlock(blockState.getBlock());
		}
		super.onLeftClickBlock(event);
	}
	
	@Override
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		if(currentBlock == null) {
			return;
		}
		if(mode.getMode("All").isToggled()) {
			RenderUtils.drawBlockESP(currentBlock, 1.0f, 0.0f, 0.0f);
		} 
		else if(mode.getMode("ID").isToggled()) {
			RenderUtils.drawBlockESP(currentBlock, 0.0f, 0.0f, 1.0f);
		}
		super.onRenderWorldLast(event);
	}
	
	
}
