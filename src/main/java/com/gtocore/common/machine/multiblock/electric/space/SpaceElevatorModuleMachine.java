package com.gtocore.common.machine.multiblock.electric.space;

import com.gtolib.api.machine.multiblock.CustomParallelMultiblockMachine;
import com.gtolib.api.recipe.Recipe;
import com.gtolib.api.recipe.modifier.ParallelLogic;
import com.gtolib.api.recipe.modifier.RecipeModifierFunction;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.gtolib.api.GTOValues.POWER_MODULE_TIER;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class SpaceElevatorModuleMachine extends CustomParallelMultiblockMachine {

    SpaceElevatorMachine spaceElevatorMachine;

    private final boolean power_module_tier;

    public SpaceElevatorModuleMachine(IMachineBlockEntity holder, boolean power_module_tier) {
        super(holder, false, m -> ((SpaceElevatorModuleMachine) m).getSpaceElevatorTier() > 7 ? (int) Math.pow(((SpaceElevatorModuleMachine) m).isSuper() ? 8 : 4, ((SpaceElevatorModuleMachine) m).spaceElevatorMachine.getCasingTier(POWER_MODULE_TIER) - 1) : 0);
        this.power_module_tier = power_module_tier;
    }

    private int getSpaceElevatorTier() {
        if (spaceElevatorMachine != null && spaceElevatorMachine.getRecipeLogic().isWorking()) {
            return spaceElevatorMachine.getTier();
        }
        return 0;
    }

    private boolean isSuper() {
        return spaceElevatorMachine instanceof SuperSpaceElevatorMachine;
    }

    @Nullable
    @Override
    protected Recipe getRealRecipe(@NotNull Recipe recipe) {
        if (getSpaceElevatorTier() < 8) {
            return null;
        }
        if (power_module_tier && recipe.data.getInt(POWER_MODULE_TIER) > spaceElevatorMachine.getCasingTier(POWER_MODULE_TIER)) {
            return null;
        }
        return RecipeModifierFunction.overclocking(this, ParallelLogic.accurateParallel(this, recipe, getParallel()), false, 1, getDurationMultiplier(), 0.5);
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) return false;
        if (getOffsetTimer() % 10 == 0) {
            return getSpaceElevatorTier() >= 8;
        }
        return true;
    }

    @Override
    public void customText(List<Component> textList) {
        super.customText(textList);
        textList.add(Component.translatable("gtocore.machine.space_elevator." + (getSpaceElevatorTier() < 8 ? "not_" : "") + "connected"));
        textList.add(Component.translatable("gtocore.machine.duration_multiplier.tooltip", FormattingUtil.formatNumbers(getDurationMultiplier())));
    }

    private double getDurationMultiplier() {
        return Math.sqrt(1.0D / ((getSpaceElevatorTier() - GTValues.ZPM) * (isSuper() ? 2 : 1)));
    }
}
