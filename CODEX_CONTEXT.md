# Korean Delight Codex Context

## Current Target
- Minecraft: 1.21.1
- Loader: NeoForge first. Fabric exists only as a future target and is not included in `settings.gradle`.
- Java: 21
- Main dependency: Farmer's Delight
- Test/runtime helper dependency: JEI is loaded in the NeoForge dev runtime through CurseMaven.

## Project Shape
- `common/`: shared mod logic, registries, blocks, items, recipes, resources.
- `neoforge/`: NeoForge entrypoint, platform registration provider, datagen hook, run configs.
- `fabric/`: placeholder for later support. Do not treat it as active yet.
- Legacy `src/` is being replaced by the multiloader-style `common/` and `neoforge/` layout.

## Important Build Files
- `settings.gradle`: includes `:common` and `:neoforge`; `:fabric` is commented out.
- `gradle.properties`: authoritative version values.
- `neoforge/build.gradle`: NeoForge runtime, Farmer's Delight dependency, JEI runtime dependency, and `runData` config.
- `common/build.gradle`: common NeoForge compile context and generated resources inclusion.

## Registration Pattern
- Common code uses `Services.PLATFORM.getProvider(Registries.X)` and `RegistrationProvider<T>`.
- NeoForge implementation stores `DeferredRegister`s in `NeoForgePlatformHelper`.
- `KoreandelightNeoForge` calls `Koreandelight.init()`, then registers all collected NeoForge providers to the mod event bus.
- Common init order matters:
  1. fluids
  2. blocks
  3. items / food items
  4. effects, block entities, recipes
  5. creative tab

## Implemented Gameplay
- Crops: kimchi cabbage, red pepper, bean, green onion.
- Foods/materials: kimchi, fresh kimchi, aged kimchi, doenjang, red pepper powder, soy sauce bucket.
- Blocks: onggi, meju, fermented meju, doenjang block, kimjang basin, soy sauce fluid block.
- Fluids:
  - Soy sauce registers both a vanilla `Fluid` and NeoForge `FluidType`.
  - `SoySauceFluid#getFluidType()` must return `ModFluidTypes.SOY_SAUCE_FLUID_TYPE`; otherwise JEI/fluid rendering can crash with "Mod fluids must override getFluidType."
- Custom systems:
  - Onggi fermentation through `koreandelight:fermentation`.
  - Kimjang basin through `koreandelight:kimjang`.
  - Spicy mob effect registered as `koreandelight:spicy`.

## Recipe Direction
- Vanilla crafting recipes should no longer be hand-authored JSON in `common/src/main/resources`.
- Use Builder/datagen instead.
- Current Builder source: `common/src/main/java/com/potan/koreandelight/data/ModRecipeProvider.java`.
- Generated vanilla crafting recipes are written to:
  - `common/src/generated/resources/data/koreandelight/recipe/doenjang.json`
  - `common/src/generated/resources/data/koreandelight/recipe/fresh_kimchi.json`
  - `common/src/generated/resources/data/koreandelight/recipe/kimchi_cabbage_seeds.json`
- Custom recipes and Farmer's Delight recipes still remain as JSON for now:
  - `recipes/fermentation/*`
  - `recipes/kimjang/*`
  - `recipes/cutting/*`
- Custom recipe serializers are 1.21.1-style:
  - `RecipeSerializer#codec()` returns a `MapCodec<T>`.
  - `RecipeSerializer#streamCodec()` returns a `StreamCodec<RegistryFriendlyByteBuf, T>`.
  - Result stacks use `ItemStack.STRICT_CODEC`, so recipe JSON result objects use `id`.
  - Ingredients use `Ingredient.CODEC_NONEMPTY`.
- Fermentation recipes preserve optional `fluid` and `result_fluid` data through common-side `FluidStackData`.
- Current onggi runtime still processes item input/output only; fluid requirements are parsed/synced but not consumed until tank logic is restored.
- JEI support is NeoForge-only for now:
  - Sources live in `neoforge/src/main/java/com/potan/koreandelight/recipe/jei`.
  - `neoforge/build.gradle` uses JEI as `compileOnly` and `runtimeOnly`.
  - Fermentation recipes are registered through `@JeiPlugin`, using JEI 19.x `RecipeType`, `IRecipeExtrasBuilder`, and fluid slots from `FluidStackData`.

## Datagen
- Run:
  ```powershell
  .\gradlew.bat :neoforge:runData --stacktrace
  ```
- Output path is configured in `neoforge/build.gradle` to write into `common/src/generated/resources`.
- `.cache` files from datagen are excluded in root `build.gradle`.

## Validation Commands
- Build:
  ```powershell
  .\gradlew.bat :neoforge:build --stacktrace
  ```
- Check runtime classpath includes JEI:
  ```powershell
  .\gradlew.bat :neoforge:dependencies --configuration runtimeClasspath
  ```
- Run client:
  ```powershell
  .\gradlew.bat :neoforge:runClient
  ```

## Known Gotchas
- Resource paths changed in 1.21.1: generated recipe path is `data/<namespace>/recipe`, not `recipes`.
- Recipe `result` objects use `id`, not `item`.
- Ingredient entries still use `item`.
- PowerShell's JSON parser can complain about valid Minecraft blockstate variants with an empty key `""`; use a proper JSON parser or game/datagen validation instead.
- `RecipeCategory.FOOD` still writes crafting book category as `misc` in this mapping because `RecipeBuilder.determineBookCategory` defaults food to misc.
- `KoreandelightNeoForge` currently registers datagen through `GatherDataEvent`.
- `EffectEventHandler` and JEI plugin code from the old `src/` tree have not yet been restored in the new `common/neoforge` structure.

## Recent Completed Work
- Added JEI to NeoForge dev runtime with:
  - `curse.maven:jei-238222:${jei_curseforge_file_id}`
- Converted vanilla crafting recipe JSONs to Builder/datagen.
- Refactored custom `fermentation` and `kimjang` recipe serializers for Minecraft 1.21.1 codec/stream codec APIs.
- Updated recipe JSONs to the 1.21.1 `result.id` stack format and restored NeoForge JEI fermentation category support.
- Restored soy sauce `FluidType` registration and `getFluidType()` override for NeoForge fluid rendering/JEI compatibility.
- Verified `:neoforge:runData` and `:neoforge:build` successfully after recipe refactors.
