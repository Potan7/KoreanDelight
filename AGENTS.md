# Repository Guidelines

## Project Structure & Module Organization

KoreanDelight is a Minecraft 1.21.1 mod using Java 21 and Gradle. Shared logic belongs in `common/src/main/java/com/potan/koreandelight`; keep blocks, items, fluids, recipes, data providers, and platform abstractions in their matching packages. Assets and data packs live in `common/src/main/resources/assets/koreandelight` and `common/src/main/resources/data`. Generated recipes and advancements live in `common/src/generated/resources` and are included by the build.

`neoforge/` is the active loader module and owns its entry point, event handlers, JEI integration, run configurations, and `META-INF/neoforge.mods.toml`. `fabric/` is a future target and is currently excluded from `settings.gradle`. Keep loader-specific behavior outside `common`. Treat `docs/reference/` as reference material, not production code.

## Architecture & Data Rules

Common registrations use `Services.PLATFORM.getProvider(Registries.X)` and `RegistrationProvider<T>`; NeoForge adapts these to `DeferredRegister`. Preserve the initialization order in `Koreandelight.init()`, especially fluids before dependent blocks and items. Soy sauce fluids must continue to return the registered NeoForge `FluidType` from `SoySauceFluid#getFluidType()`.

Create vanilla crafting recipes through `ModRecipeProvider` and datagen rather than hand-writing JSON. Custom fermentation, kimjang, and Farmer's Delight cutting recipes may remain JSON. For Minecraft 1.21.1, generated recipes use `data/<namespace>/recipe`, result stacks use `id`, and ingredient entries use `item`. Review generated diffs before committing.

## Build, Test, and Development Commands

Run commands from the repository root:

- `.\gradlew.bat build` - compile all active modules and run checks.
- `.\gradlew.bat :neoforge:runClient` - launch the development client.
- `.\gradlew.bat :neoforge:runServer` - launch the development server.
- `.\gradlew.bat :neoforge:runData --stacktrace` - regenerate common data resources.
- `.\gradlew.bat :neoforge:test` - run NeoForge tests.

Use Java 21; version values in `gradle.properties` are authoritative.

## Coding Style & Testing

Use four-space indentation, same-line braces, and one public top-level class per file. Name classes in `PascalCase`, members in `camelCase`, constants in `UPPER_SNAKE_CASE`, and resource IDs in lowercase `snake_case`. Keep registry IDs aligned with asset, recipe, tag, and localization keys. No formatter or linter is configured, so preserve nearby style and imports.

Automated tests are not currently committed. Add isolated logic tests under `<module>/src/test/java` with names ending in `Test`. Validate gameplay changes in `runClient`, data changes with `runData`, and confirm affected content loads without errors.

## Commits & Pull Requests

Use concise prefixes seen in history, such as `feat:`, `fix:`, and `refactor:`. Keep commits focused. Pull requests should describe user-visible and technical effects, link relevant issues, list validation commands, and include screenshots for UI, model, texture, or gameplay changes. Explicitly identify generated-resource updates.
