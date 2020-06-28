package net.roguelogix.biggerreactors;

import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.roguelogix.biggerreactors.classic.blocks.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.blocks.CyaniteReprocessorScreen;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(BiggerReactors.modid)
public class BiggerReactors {

    public static final String modid = "biggerreactors";

    private static final Logger LOGGER = LogManager.getLogger();
    public BiggerReactors() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().register(RegistryEvents.class);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SuppressWarnings("unused")
    public static class RegistryEvents {
        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            Registry.registerBlocks(blockRegistryEvent);
            Config.onLoad();
        }

        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            Registry.registerItems(itemRegistryEvent);
        }

        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> tileEntityTypeRegisteryEvent) {
            Registry.registerTileEntities(tileEntityTypeRegisteryEvent);
        }

        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerTypeRegistryEvent) {
            Registry.registerContainers(containerTypeRegistryEvent);
        }

    }

    public void onClientSetup(final FMLClientSetupEvent e) {

        Registry.onClientSetup(e);
        ScreenManager.registerFactory(CyaniteReprocessorContainer.INSTANCE,
            CyaniteReprocessorScreen::new);
    }

    public void onLoadComplete(final FMLLoadCompleteEvent e) { Registry.registerWorldGen(); }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre event) {
        Registry.onTextureStitch(event);
    }

    public void onModelBake(final ModelBakeEvent event) {
        Registry.onModelBake(event);
    }
}
