package com.stebars.rainbreaks;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;


public class OptionsHolder {
	public static class Common {	

		public ConfigValue<List<? extends String>> breakableBlocks;
		public ConfigValue<Integer> checkEveryNTicks;
		public ConfigValue<Integer> tryBreakNBlocksPerChunk;

		public Common(ForgeConfigSpec.Builder builder) {
			breakableBlocks = builder.comment("IDs for blocks to break.")
					.defineList("breakableBlocks", Arrays.asList(
							"minecraft:bookshelf",
							"minecraft:enchanting_table",
							"minecraft:smithing_table",
							"minecraft:furnace",
							"minecraft:blast_furnace",
							"minecraft:smoker",
							"minecraft:torch",
							"minecraft:brewing_stand",
							"minecraft:lectern",
							"minecraft:redstone_wire",

							"minecraft:white_bed",
							"minecraft:orange_bed",
							"minecraft:magenta_bed",
							"minecraft:light_blue_bed",
							"minecraft:yellow_bed",
							"minecraft:lime_bed",
							"minecraft:pink_bed",
							"minecraft:gray_bed",
							"minecraft:light_gray_bed",
							"minecraft:cyan_bed",
							"minecraft:purple_bed",
							"minecraft:blue_bed",
							"minecraft:brown_bed",
							"minecraft:green_bed",
							"minecraft:red_bed",
							"minecraft:black_bed"
							),
							o -> o instanceof String);
			checkEveryNTicks = builder.comment("The mod will check every [this number] ticks to see if it's raining, and if so, break blocks. "
					+ "There are 20 ticks in a second.")
					.define("checkEveryNTicks", 20 * 10);
			tryBreakNBlocksPerChunk = builder.comment("When we check and it's raining, we'll look at this number of random blocks inside each chunk, and "
					+ "possibly break them.")
					.define("tryBreakNBlocksPerChunk", 5);
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static { //constructor
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
}