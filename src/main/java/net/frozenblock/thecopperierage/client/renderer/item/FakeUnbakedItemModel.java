package net.frozenblock.thecopperierage.client.renderer.item;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemModel;

@Environment(EnvType.CLIENT)
public record FakeUnbakedItemModel(ItemModel wrapped) implements ItemModel.Unbaked {

	@Override
	public MapCodec<? extends ItemModel.Unbaked> type() {
		return null;
	}

	@Override
	public ItemModel bake(ItemModel.BakingContext bakingContext) {
		return this.wrapped;
	}

	@Override
	public void resolveDependencies(Resolver resolver) {
	}
}
