package com.stebars.rainbreaks.mixin;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stebars.rainbreaks.OptionsHolder;

import net.minecraft.block.BlockState;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.ISpawnWorldInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {

	private static Set<String> breakableBlocks = null;
	private static Integer checkEveryNTicks = null;
	private static Integer tryBreakNBlocksPerChunk = null;

	protected ServerWorldMixin(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_,
			DimensionType p_i241925_3_, Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_,
			long p_i241925_7_) {
		super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
	}

	@Inject(method = "tickChunk",
			at = @At("HEAD"))
	protected void tickChunk(Chunk chunk, int randomTickRate,
			CallbackInfo ci) {
		World world = chunk.getLevel();
		if (!world.isRaining())
			return;

		// world.getGameTime() is tick counter, persisted between save and load
		if (checkEveryNTicks == null)
			checkEveryNTicks = OptionsHolder.COMMON.checkEveryNTicks.get();
		if (world.getGameTime() % checkEveryNTicks != 0)
			return;

		if (tryBreakNBlocksPerChunk == null)
			tryBreakNBlocksPerChunk = OptionsHolder.COMMON.tryBreakNBlocksPerChunk.get();
		if (breakableBlocks == null)
			breakableBlocks = OptionsHolder.COMMON.breakableBlocks.get().stream()
			.map(x -> x.toString()).collect(Collectors.toSet());
		Random random = world.getRandom();
		BlockPos basePos = chunk.getPos().getWorldPosition();
		for (int i = 0; i < tryBreakNBlocksPerChunk; i++) {
			int x = random.nextInt(16) + basePos.getX();
			int z = random.nextInt(16) + basePos.getZ();
			
			// Check motion-blocking blocks; this excludes eg redstone wire and torches
			int y1 = chunk.getHeight(Type.MOTION_BLOCKING, x, z);
			BlockPos pos1 = new BlockPos(x, y1, z);
			// Ignore biomes with no rain (savanna, desert, nether, etc.)
			if (world.getBiome(pos1).getPrecipitation() == RainType.NONE)
				continue;
			BlockState state1 = chunk.getBlockState(pos1);
			if (shouldBreak(state1)) {
				world.destroyBlock(pos1, true); // The 2nd arg is whether chests etc. should drop contents
				continue;
			}

			// If no motion-blocking blocks broken, also check blocks like torches and redstone wire
			int y2 = chunk.getHeight(Type.WORLD_SURFACE, x, z);
			BlockPos pos2 = new BlockPos(x, y2, z);
			BlockState state2 = chunk.getBlockState(pos2);
			if (shouldBreak(state2)) {
				world.destroyBlock(pos2, true);
			}
		}	
	}

	private boolean shouldBreak(BlockState state) {
		return breakableBlocks.contains(state.getBlock().getRegistryName().toString());
	}
}