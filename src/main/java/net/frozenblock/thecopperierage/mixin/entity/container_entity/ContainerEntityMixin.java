package net.frozenblock.thecopperierage.mixin.entity.container_entity;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.entity.vehicle.Minecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerEntity.class)
public interface ContainerEntityMixin {

	@WrapWithCondition(
		method = "chestVehicleDestroyed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;angerNearbyPiglins(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Z)V"
		)
	)
	default boolean theCopperierAge$onlyAngerPiglinsIfMinecartHasChest(ServerLevel level, Player player, boolean requiresLineOfSight) {
		// TODO: Config
		return !(ContainerEntity.class.cast(this) instanceof Minecart minecart) || !minecart.getType().is(TCAEntityTypeTags.NON_CHEST_MINECARTS);
	}

}
