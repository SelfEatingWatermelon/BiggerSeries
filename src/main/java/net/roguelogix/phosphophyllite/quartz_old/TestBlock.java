package net.roguelogix.phosphophyllite.quartz_old;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

@RegisterBlock(name = "testblock")
public class TestBlock extends Block {
    public TestBlock() {
        super(Properties.create(Material.IRON));
    }
}
