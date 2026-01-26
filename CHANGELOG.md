Please clear changelog after each release.
Put the changelog BELOW the dashes. ANYTHING ABOVE IS IGNORED.
-----------------
- Removed the item for Copper Fire, as it was never meant to be registered.
- Fixed Weighted Pressure Plates not having map colors.
- Fixed Chimes not having map colors.
- Added Crates.
- Copper Golems can now occasionally press Copper Buttons.
- Added waxing recipes that were missing.
- Added waxing recipes for Copper Tools/Equipment.
  - Both the stats and visuals of the item will remain the same once waxed.
  - Waxed items cannot be unwaxed.
- The Copperier Age's models for oxidizing models are now generated on runtime.
  - This change was made in order to improve compatibility with other mods and resource packs.
  - Modders can now add `search terms` for The Copperier Age's automatic oxidizing model generation.
    - This method can be found in `OxidizableItemHelper`.
