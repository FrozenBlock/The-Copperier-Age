# Rails feature — implementation status & notes

Autonomous progress while you were away. **Nothing is committed** — everything is in the working tree for you to review, test, and commit.

## Textures — IMPORTED ✅ (from your `~/Downloads/rail_*.png`)
Imported into `src/main/resources/assets/thecopperierage/textures/block/`:

**Copper rail** (4 files, all provided): `copper_rail.png`, `exposed_copper_rail.png`, `weathered_copper_rail.png`, `oxidized_copper_rail.png`.
- You provided straight 16×16 rails only (no corner texture), so curves currently **reuse the straight texture** — corners will render as a bent straight rail, not a smooth curve. To improve: add `..._corner.png` files and flip one line in `TCAModelProvider.createCopperRail` (noted in that method).

**Cross rail** (1 file): `cross_rail.png` — your `+` intersection; symmetric so it's correct for both axes.

**Relayor rail** (first pass, 2 cells extracted from your 64×48 sheet `~/Downloads/rail_relayor.png`):
- `relayor_rail_on.png` = column 1 (powered, red centre), middle row.
- `relayor_rail.png` = column 3 (inactive/grabbing, dark centre), middle row.
- The full **4-column × 3-row** sheet is still in `~/Downloads/rail_relayor.png`, ready to split when we wire the real state/connection model mapping. Columns: 1 powered, 2 inactive-locking, 3 inactive-grabbing, 4 unpowered-with-comparator; rows: 3 visual-connection variants.

## 1. Copper rail — DONE ✅ (build + datagen verified; only textures missing)
- `block/CopperRailBlock.java` (base, extends `BaseRailBlock`; carries `WeatherState`) + `block/WeatheringCopperRailBlock.java` (oxidises). Full curved `RailShape` like vanilla rail.
- Registered as an 8-variant weathering copper collection (`TCABlocks.COPPER_RAIL`, `TCAItems.COPPER_RAIL`, `TCABlockItemIds.COPPER_RAIL`), oxidation + waxing wired via `OxidizableBlocksRegistry`.
- **Speed by oxidation**: `mixin/entity/minecart/speed/AbstractMinecartMixin` (`@ModifyReturnValue` on `getMaxSpeed`). Hooking `AbstractMinecart.getMaxSpeed` alone covers regular AND furnace carts (furnace funnels through `super.getMaxSpeed`), avoiding double-application.
  - **ASSUMPTION (tunable in `CopperRailBlock.speedMultiplier`)**: unaffected 1.0×, exposed 0.8×, weathered 0.6×, oxidized 0.4×. Unaffected = normal-rail speed per your spec.
- Datagen (blockstates/models/item-models via a new `createCopperRail`), loot (drop self), `BlockTags.RAILS`, 8 lang keys, creative tab (Redstone, after activator rail).
- **No config toggle** added (the slowdown is the feature). Easy to add one if you want.

## 2. Cross rail — REGISTERED, first pass ⚠️ (build + datagen verified)
- `block/CrossRailBlock.java` — straight-only `BaseRailBlock` (`RAIL_SHAPE_STRAIGHT`), registered (`TCABlocks.CROSS_RAIL`), in `BlockTags.RAILS`, datagen (`createCrossRail`), loot, lang, creative tab. Placeable, rideable, renders (needs `cross_rail.png`).
- It must extend `BaseRailBlock` (else carts don't treat it as rail), which forces a `RailShape` property — so "no states" is approximated by always rendering the same cross; movement should pick the axis.
- **IMPLEMENTED:** the straight-through crossing. `CrossRailBlock.alignToCartAxis` re-points the block's own `SHAPE` to the axis of the cart on it (invisible — the cross texture is symmetric), so vanilla's movement carries the cart straight through in **both** movement modes with no behaviour-specific patching. Driven from `mixin/entity/minecart/rail/AbstractMinecartMixin` at `moveAlongTrack` HEAD.

## 3. Relayor rail — REGISTERED, first pass ⚠️ (build + datagen verified)
`block/RelayorRailBlock.java` — straight `BaseRailBlock` with a redstone-driven `POWERED` state and comparator output (currently 15 when a cart sits on it). Registered (`TCABlocks.RELAYOR_RAIL`), in `BlockTags.RAILS`, datagen (vanilla `createActiveRail`, off/`_on`), loot, lang, creative tab. Placeable, rideable, responds to redstone + emits comparator signal. The cart-movement behaviour is intentionally NOT implemented blind.

### Design (for the movement/state pass)
Reference: rails-revamped "Interruption rail" — stops carts and releases them keeping momentum; directional; powered releases; chains release together; carts queue toward the front; comparator = cart count in the chain.

**Your relayor differs**: when **powered**, act like a **directional powered rail** (propel the cart in the stump direction) instead of releasing retained momentum. When **unpowered**, hold/grab carts (queue).

**Texture columns → states (my reading of your spec):**
1. **Powered/activated** — propelling.
2. **Inactive, locking a cart in place** — NOT the first free rail in a sequence.
3. **Inactive, actively grabbing** — the rail in front already holds a cart, OR it's not connected to another relayor (so it grabs naturally).
4. **Unpowered but some relayors in the sequence hold carts** — has comparator output.
5. (later) locked/unlocked extras.

**Rows** = visual connection to neighbouring relayors (like fences), so a chain reads as one piece.

**NOW IMPLEMENTED** (in `RelayorRailBlock`, driven by `mixin/entity/minecart/rail/AbstractMinecartMixin`, both movement modes):
- **Unpowered → jams the cart.** Horizontal momentum is dropped, so carts stop and queue on it.
- **Powered → propels the cart** along `propulsionDirection`: `+0.06`/tick to a moving cart, `0.2` kick from rest (same numbers as a vanilla powered rail).
- **Comparator = cart count across the connected run** of relayors sharing the rail's axis (`countCartsInSequence`, traced up to 64 rails each way).

**Direction is currently derived, not stored:** if exactly one side along the axis is backed by a solid/redstone-conducting block, carts are pushed *away* from it (identical mental model to a powered rail — put a block behind it). With no block, or blocks both sides, a moving cart keeps its direction and the rail acts as a pure booster. **Still open:** storing an explicit stump direction on the blockstate (chosen at placement / flippable), which also unlocks the 4-column × 3-row visual mapping. Deferred because it needs new blockstate properties + a custom datagen dispatch, and the behaviour was the blocker.

**Still open:** queue drive-up ordering (carts advancing to the front of a chain to leave room behind).
