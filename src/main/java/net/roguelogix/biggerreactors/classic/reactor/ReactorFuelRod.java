package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_fuel_rod", tileEntityClass = ReactorFuelRodTile.class)
public class ReactorFuelRod extends ReactorBaseBlock {

    @RegisterBlock.Instance
    public static ReactorFuelRod INSTANCE;

    public ReactorFuelRod() {
        super(false);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorFuelRodTile();
    }

    @RegisterBlock.RenderLayer
    RenderType renderLayer() {
        return RenderType.getCutout();
    }
}
