package net.roguelogix.biggerreactors.classic.reactor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.Validator;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockController;

import java.util.HashSet;
import java.util.Set;


/* TODO

Internal simulation
internal coolant checking
assembly errors

 */
public class ReactorMultiblockController extends RectangularMultiblockController {

    public ReactorMultiblockController(World world) {
        super(world);
        minWidth = minHeight = minLength = 3;
        maxLength = Config.ReactorMaxLength;
        maxWidth = Config.ReactorMaxWidth;
        maxHeight = Config.ReactorMaxHeight;
        tileAttachValidator = tile -> {
            return tile instanceof ReactorBaseTile;
        };
        frameValidator = block -> {
            return block instanceof ReactorCasing;
        };
        cornerValidator = frameValidator;
        exteriorValidator = Validator.or(frameValidator, block -> {
            return block instanceof ReactorTerminal || block instanceof ReactorControlRod || block instanceof ReactorGlass || block instanceof ReactorPowerPort;
        });
        interiorValidator = block -> {
            if(block instanceof ReactorFuelRod){
                return true;
            }
            if(!ReactorCoolantRegistry.isBlockAllowed(block)){
                return false;
            }
            if(exteriorValidator.validate(block)){
                return false;
            }
            return true;
        };
        setAssemblyValidator(genericController -> {
            ReactorMultiblockController reactorController = (ReactorMultiblockController) genericController;
            if (terminals.isEmpty()) {
                return false;
            }
            if (controlRods.isEmpty()) {
                return false;
            }
            for (ReactorControlRodTile controlRod : controlRods) {
                if (controlRod.getPos().getY() != maxY()) {
                    return false;
                }
                for (int i = 0; i < maxY() - minY() - 1; i++) {
                    if (!(world.getBlockState(controlRod.getPos().add(0, -1 - i, 0)).getBlock() instanceof ReactorFuelRod)) {
                        return false;
                    }
                }
            }

            for (ReactorFuelRodTile fuelRod : fuelRods) {
                if (!(world.getBlockState(new BlockPos(fuelRod.getPos().getX(), maxY(), fuelRod.getPos().getZ())).getBlock() instanceof ReactorControlRod)) {
                    return false;
                }
            }

            return true;
        });
    }

    private ReactorState reactorState = ReactorState.INACTIVE;

    private final Set<ReactorTerminalTile> terminals = new HashSet<>();
    private final Set<ReactorControlRodTile> controlRods = new HashSet<>();
    private final Set<ReactorFuelRodTile> fuelRods = new HashSet<>();
    private final Set<ReactorPowerPortTile> powerPorts = new HashSet<>();

    @Override
    protected void onPartAdded(MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.add((ReactorTerminalTile) tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            controlRods.add((ReactorControlRodTile) tile);
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.add((ReactorFuelRodTile) tile);
        }
        if (tile instanceof ReactorPowerPortTile) {
            powerPorts.add((ReactorPowerPortTile) tile);
        }
    }

    @Override
    protected void onPartRemoved(MultiblockTile tile) {
        if (tile instanceof ReactorTerminalTile) {
            terminals.remove(tile);
        }
        if (tile instanceof ReactorControlRodTile) {
            controlRods.remove(tile);
        }
        if (tile instanceof ReactorFuelRodTile) {
            fuelRods.remove(tile);
        }
        if (tile instanceof ReactorPowerPortTile) {
            powerPorts.remove(tile);
        }
    }

    public void updateBlockStates() {
        terminals.forEach(terminal -> {
            world.setBlockState(terminal.getPos(), terminal.getBlockState().with(ReactorState.REACTOR_STATE_ENUM_PROPERTY, reactorState));
            terminal.markDirty();
        });
    }

    public void setActive(ReactorState newState) {
        if (reactorState != newState) {
            reactorState = newState;
            updateBlockStates();
        }
    }

    public void toggleActive() {
        setActive(reactorState == ReactorState.ACTIVE ? ReactorState.INACTIVE : ReactorState.ACTIVE);
    }

    protected void read(CompoundNBT compound) {
        if (compound.contains("reactorState")) {
            reactorState = ReactorState.valueOf(compound.getString("reactorState").toUpperCase());
        }
        updateBlockStates();
    }

    protected CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        {
            compound.putString("reactorState", reactorState.toString());
        }
        return compound;
    }

    @Override
    protected void onAssembly() {
        for (ReactorPowerPortTile powerPort : powerPorts) {
            BlockPos pos = powerPort.getPos();
            if(pos.getX() == minX()){
                powerPort.powerOutputDirection = Direction.WEST;
                continue;
            }
            if(pos.getX() == maxX()){
                powerPort.powerOutputDirection = Direction.EAST;
                continue;
            }
            if(pos.getY() == minY()){
                powerPort.powerOutputDirection = Direction.DOWN;
                continue;
            }
            if(pos.getY() == maxY()){
                powerPort.powerOutputDirection = Direction.UP;
                continue;
            }
            if(pos.getZ() == minZ()){
                powerPort.powerOutputDirection = Direction.NORTH;
                continue;
            }
            if(pos.getZ() == maxZ()){
                powerPort.powerOutputDirection = Direction.SOUTH;
                continue;
            }
            // todo error out, shouldn't happen
            return;
        }
    }

    @Override
    protected void onDisassembly() {
        setActive(ReactorState.INACTIVE);
        for (ReactorPowerPortTile powerPort : powerPorts) {
            powerPort.powerOutputDirection = null;
        }
    }


    @Override
    public void tick() {

        if(reactorState == ReactorState.ACTIVE){

        }

        for (ReactorPowerPortTile powerPort : powerPorts) {
            powerPort.distributePower(1000);
        }
    }
}