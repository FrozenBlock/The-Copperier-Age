/*
 * Copyright 2025-2026 FrozenBlock
 * This file is part of The Copperier Age.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.thecopperierage.mixin.entity.minecart_coupling;

import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingUtil;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements CouplingToEntityInterface {

	@Unique
	@Nullable
	private Entity theCopperierAge$coupledTo = null;

	@Unique
	@Nullable
	private Vec3 theCopperierAge$coupleStartOffset0 = null;
	@Unique
	@Nullable
	private Vec3 theCopperierAge$coupleStartOffset = null;

	@Unique
	@Nullable
	private Vec3 theCopperierAge$coupleVector0 = null;
	@Unique
	@Nullable
	private Vec3 theCopperierAge$coupleVector = null;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/MinecartBehavior;tick()V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$tickCoupling(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		MinecartCouplingUtil.tickCoupling(minecart);

		if (this.theCopperierAge$coupledTo == null) {
			this.theCopperierAge$clearCouplingVectors();
			return;
		}

		final VoxelShape thisShape = Shapes.create(minecart.getBoundingBox());
		final VoxelShape otherShape = Shapes.create(this.theCopperierAge$coupledTo.getBoundingBox());

		final Optional<Vec3> startPos = thisShape.closestPointTo(this.theCopperierAge$coupledTo.position());
		final Optional<Vec3> targetPos = otherShape.closestPointTo(minecart.position());
		if (startPos.isEmpty() || targetPos.isEmpty()) {
			this.theCopperierAge$clearCouplingVectors();
			return;
		}

		this.theCopperierAge$coupleStartOffset0 = this.theCopperierAge$coupleStartOffset;
		this.theCopperierAge$coupleStartOffset = startPos.get().subtract(minecart.position());

		this.theCopperierAge$coupleVector0 = this.theCopperierAge$coupleVector;
		this.theCopperierAge$coupleVector = targetPos.get().subtract(minecart.position());
	}

	@Unique
	private void theCopperierAge$clearCouplingVectors() {
		this.theCopperierAge$coupleStartOffset0 = null;
		this.theCopperierAge$coupleStartOffset = null;
		this.theCopperierAge$coupleVector0 = null;
		this.theCopperierAge$coupleVector = null;
	}

	@Unique
	@Override
	public void theCopperierAge$setCoupledTo(@Nullable Entity entity) {
		this.theCopperierAge$coupledTo = entity;
	}

	@Unique
	@Nullable
	@Override
	public Vec3 theCopperierAge$getCoupleStartOffset(float partialTicks) {
		if (this.theCopperierAge$coupleStartOffset == null) return null;
		if (this.theCopperierAge$coupleStartOffset0 == null) return this.theCopperierAge$coupleStartOffset;
		return Mth.lerp(partialTicks, this.theCopperierAge$coupleStartOffset0, this.theCopperierAge$coupleStartOffset);
	}

	@Unique
	@Nullable
	@Override
	public Vec3 theCopperierAge$getCoupleVector(float partialTicks) {
		if (this.theCopperierAge$coupleVector == null) return null;
		if (this.theCopperierAge$coupleVector0 == null) return this.theCopperierAge$coupleVector;
		return Mth.lerp(partialTicks, this.theCopperierAge$coupleVector0, this.theCopperierAge$coupleVector);
	}
}
