package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.roguelogix.biggerreactors.classic.reactor.ReactorPowerPort.ConnectionState.*;


@RegisterTileEntity(name = "reactor_power_port")
public class ReactorPowerPortTile extends ReactorBaseTile implements IEnergyStorage {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public ReactorPowerPortTile() {
        super(TYPE);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }

    private boolean connected = false;
    Direction powerOutputDirection = null;

    private void setConnected(boolean newState){
        if(newState != connected){
            connected = newState;
            world.setBlockState(pos, getBlockState().with(CONNECTION_STATE_ENUM_PROPERTY, connected ? CONNECTED : DISCONNECTED));
        }
    }

    public long distributePower(long toDistribute) {
        if (powerOutputDirection == null) {
            setConnected(false);
            return 0;
        }
        assert world != null;
        TileEntity te = world.getTileEntity(pos.offset(powerOutputDirection));
        if (te == null) {
            setConnected(false);
            return 0;
        }
        IEnergyStorage e = te.getCapability(CapabilityEnergy.ENERGY, powerOutputDirection.getOpposite()).orElse(new EnergyStorage(0));
        if (e.canReceive()) {
            setConnected(true);
            return e.receiveEnergy((int) toDistribute, false);
        }
        setConnected(false);
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
