# 📜 KoreanDelight 개발 규칙 및 가이드라인 (Development Guidelines)

본 문서는 **KoreanDelight** 모드(Minecraft 1.21.1 / Java 21 / NeoForge / Farmer's Delight 호환) 개발 시 준수해야 하는 아키텍처 규칙, 등록 절차, 데이터 생성(Datagen), 코드 스타일 및 협업 가이드라인을 정의합니다.

---

## 📌 목차
1. [프로젝트 개요 및 환경](#1-프로젝트-개요-및-환경)
2. [모듈 구조 및 역할](#2-모듈-구조-및-역할)
3. [등록(Registry) 아키텍처 및 초기화 순서](#3-등록registry-아키텍처-및-초기화-순서)
4. [신규 콘텐츠 추가 개발 규칙](#4-신규-콘텐츠-추가-개발-규칙)
   - [작물 및 씨앗 추가](#41-작물-및-씨앗-추가)
   - [음식 및 아이템 추가](#42-음식-및-아이템-추가)
   - [옹기 발효 레시피 추가](#43-옹기-발효-레시피-추가)
   - [도마 썰기 레시피 추가](#44-도마-썰기-레시피-추가)
   - [바닐라 제작대/화로 레시피 (Datagen)](#45-바닐라-제작대화로-레시피-datagen)
5. [1.21.1 데이터팩 & JSON 규격 규칙](#5-1211-데이터팩--json-규격-규칙)
6. [코드 스타일 및 네이밍 규칙](#6-코드-스타일-및-네이밍-규칙)
7. [빌드, 실행 및 검증 명령어](#7-빌드-실행-및-검증-명령어)
8. [Git 커밋 및 협업 규칙](#8-git-커밋-및-협업-규칙)

---

## 1. 프로젝트 개요 및 환경

- **마인크래프트 버전**: `1.21.1`
- **Java 버전**: `Java 21` (반드시 JDK 21 사용)
- **주 모드로더**: `NeoForge` (단일 활성 로더 모듈)
- **핵심 종속성(Dependency)**:
  - **Farmer's Delight (FD)**: 도마(Cutting), 요리 냄비(Cooking Pot), 프라이팬, 식재료 태그 연동
  - **JEI (Just Enough Items)**: 발효 및 가공 레시피 시각화 연동 (`neoforge/build.gradle` 참조)
- **설정 파일 권한**: `gradle.properties`에 명시된 버전 설정이 최우선 권한을 가집니다.

---

## 2. 모듈 구조 및 역할

프로젝트는 향후 멀티로더 확장을 염두에 둔 멀티프로젝트 구조로 구성되어 있습니다.

```
KoreanDelight1.20.1/
├── common/                  # 모드로더 독립적인 공통 로직 및 리소스
│   ├── src/main/java/       # 공통 자바 소스 코드
│   ├── src/main/resources/  # 에셋(assets) 및 수동 데이터(data)
│   └── src/generated/       # Datagen으로 생성된 리소스 (build에 자동 포함)
├── neoforge/                # NeoForge 로더 전용 모듈 (엔트리포인트, 이벤트, JEI)
│   ├── src/main/java/       # NeoForge 전용 코드
│   └── src/main/resources/  # META-INF/neoforge.mods.toml
├── docs/                    # 기획, 레시피, 로드맵, 개발 규칙 문서
└── gradle/                  # Gradle 래퍼
```

> ⚠️ **주의사항**:
> - `fabric/` 디렉터리는 향후 지원 예정으로 현재 `settings.gradle`에서 제외되어 있습니다.
> - 로더 종속적인 클래스(NeoForge 이벤트, 특정 플랫폼 API)는 `common/`에 직접 참조하지 말고 `Services.PLATFORM` 추상화 인터페이스를 통해 접근해야 합니다.
> - `docs/reference/`는 참고용 레퍼런스 코드이며 프로덕션 코드가 아닙니다.

---

## 3. 등록(Registry) 아키텍처 및 초기화 순서

### 3.1 등록 방식 (RegistrationProvider)
모든 공통 등록 객체는 `Services.PLATFORM.getProvider(Registries.X)`를 통해 `RegistrationProvider<T>` 인스턴스를 선언하고, 각 플랫폼 모듈(`neoforge`)에서 `DeferredRegister`로 적응하여 등록합니다.

```java
// 예: ModItems.java
public class ModItems {
    public static final RegistrationProvider<Item> ITEMS = Services.PLATFORM.getProvider(Registries.ITEM);

    public static final Supplier<Item> GARLIC = ITEMS.register(
            "garlic",
            () -> new ItemNameBlockItem(ModCropBlocks.GARLIC_CROP.get(), new Item.Properties().food(ModFoodProperties.GARLIC))
    );
}
```

### 3.2 초기화 순서 규칙 (`Koreandelight.init()`)
마인크래프트 레지스트리 종속성에 의해 `Koreandelight.init()` 내부의 호출 순서는 반드시 유지되어야 합니다:

1. **유체 (`ModFluids`)**: 종속 블록이나 양동이 아이템보다 먼저 초기화
2. **블록 (`ModBlocks`, `ModCropBlocks`)**: 아이템 블록보다 먼저 초기화
3. **아이템 (`ModItems`, `ModFoodItems`)**: 블록 및 영양치 초기화 후 등록
4. **효과, 블록 엔티티, 레시피 타입, 메뉴 (`ModEffects`, `ModBlockEntityTypes`, `ModRecipes`, `ModMenuTypes`)**
5. **크리에이티브 탭 (`ModCreativeTabs`)**: 모든 아이템/블록 등록 후 아이템 리스트 구성

> 🚨 **간장 유체(Soy Sauce Fluid) 필수 규칙**:
> NeoForge 환경에서 간장 유체는 반드시 등록된 `FluidType`을 반환해야 합니다. `SoySauceFluid#getFluidType()` 오버라이드가 누락되면 JEI나 유체 렌더링 시 모드가 비정상 종료됩니다.

---

## 4. 신규 콘텐츠 추가 개발 규칙

### 4.1 작물 및 씨앗 추가
1. **작물 블록 클래스 작성**: `block/custom/cropblock/`에 `CropBlock`을 상속하여 작성 (예: 4단계 성장 모델 `GarlicCropBlock`).
2. **블록 및 아이템 등록**:
   - `ModCropBlocks`에 작물 블록 등록.
   - 자체 파종 작물(마늘, 콩): `ModItems` 또는 `ModFoodItems`에 `ItemNameBlockItem`으로 등록.
   - 씨앗 분리 작물(배추, 고추, 대파): 전용 `_seeds` 아이템을 `ItemNameBlockItem`으로 등록하고, 작물 자체는 일반 `Item`으로 등록.
3. **블록스테이트 및 전리품 테이블**:
   - `assets/koreandelight/blockstates/<crop_id>.json`
   - `data/koreandelight/loot_table/block/<crop_id>.json`
4. **씨앗 변환 레시피**:
   - `ModRecipeProvider.java`의 `seedFromCrop()` 메서드를 통해 작물 ➡️ 씨앗 변환 조합법 등록.

### 4.2 음식 및 아이템 추가
1. **음식 영양치 정의**: `item/ModFoodProperties.java`에 `FoodProperties` 상수 선언.
   - 영양치(nutrition), 포화도(saturationModifier), 효과 부여(effect) 설정.
2. **아이템 등록**:
   - 순수 식재료/가공품: `ModItems.java`
   - 섭취 가능한 음식/요리: `ModFoodItems.java`
3. **크리에이티브 탭 등록**: `ModCreativeTabs.java`의 `displayItems`에 `output.accept(...)` 추가.
4. **다국어 번역 등록**:
   - 한국어: `assets/koreandelight/lang/ko_kr.json`
   - 영어: `assets/koreandelight/lang/en_us.json`
5. **아이템 모델 등록**:
   - `assets/koreandelight/models/item/<id>.json` 생성 (생성형 모델 표준 준수).

### 4.3 옹기 발효 레시피 추가
옹기(Onggi) 발효는 `common/src/main/resources/data/koreandelight/recipe/fermentation/`에 JSON으로 작성합니다.

```json
{
  "type": "koreandelight:fermentation",
  "ingredients": [
    { "item": "koreandelight:kimchi_cabbage" },
    { "item": "koreandelight:red_pepper_powder" },
    { "item": "koreandelight:minced_garlic" },
    { "item": "koreandelight:fish_sauce" }
  ],
  "fluid": {
    "fluid": "minecraft:water",
    "amount": 1000
  },
  "result": {
    "id": "koreandelight:kimchi",
    "count": 1
  },
  "fermentation_time": 1200
}
```
- `ingredients` (배열) 또는 `ingredient` (단일 객체) 지원
- `fluid` (투입 유체) 및 `result_fluid` (결과 유체)는 선택 사항
- `fermentation_time`: 틱 단위 (예: 1200틱 = 1분)

### 4.4 도마 썰기 레시피 추가
Farmer's Delight 도마 레시피는 `common/src/main/resources/data/koreandelight/recipe/cutting/`에 JSON으로 작성합니다.

```json
{
  "type": "farmersdelight:cutting",
  "ingredients": [
    { "item": "koreandelight:garlic" }
  ],
  "tool": {
    "tag": "c:tools/knife"
  },
  "result": [
    {
      "item": {
        "id": "koreandelight:minced_garlic",
        "count": 1
      }
    }
  ]
}
```
- 도구 태그는 `c:tools/knife`를 기본으로 사용합니다.

### 4.5 바닐라 제작대/화로 레시피 (Datagen)
일반 무정형/정형 조합법, 화로/훈연기/모닥불 조리 레시피는 **수동 JSON을 작성하지 마십시오.**
반드시 `common/src/main/java/com/potan/koreandelight/data/ModRecipeProvider.java`에 코드로 작성한 후 Datagen 명령어를 실행합니다.

```java
// 예: 생 어묵 반죽 조합법
ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_CAKE.get())
        .requires(ModItems.MINCED_FISH.get())
        .requires(WHEAT_DOUGH)
        .requires(ModItems.MINCED_GARLIC.get())
        .unlockedBy("has_minced_fish", has(ModItems.MINCED_FISH.get()))
        .save(output, modRecipe("raw_fish_cake"));
```

---

## 5. 1.21.1 데이터팩 & JSON 규격 규칙

Minecraft 1.21.1 버전의 데이터팩 형식 변경 사항을 엄격히 준수해야 합니다:

1. **레시피 디렉터리 경로**:
   - `data/<namespace>/recipes/` (X - 구버전)
   - `data/<namespace>/recipe/` (O - 1.21.1 단수형 표준)
2. **결과 아이템 스택 키**:
   - `"result": { "item": "...", "count": 1 }` (X)
   - `"result": { "id": "...", "count": 1 }` (O - 1.21.1 표준)
3. **재료 아이템 스택 키**:
   - 재료 객체는 계속 `"item": "..."` 또는 `"tag": "..."`를 사용합니다.
4. **아이템 모델 기본 템플릿**:
   ```json
   {
     "parent": "minecraft:item/generated",
     "textures": {
       "layer0": "koreandelight:item/<id>"
     }
   }
   ```

---

## 6. 코드 스타일 및 네이밍 규칙

### 6.1 코드 서식
- **들여쓰기**: 4 스페이스 (탭 금지)
- **중괄호**: Same-line (K&R 스타일)
- **최상위 클래스**: 파일당 1개의 `public` 클래스
- **임포트**: 와일드카드(`*`) 임포트 지양, 기존 파일의 인접 스타일 및 임포트 유지

### 6.2 네이밍 규칙
| 대상 | 표기법 | 예시 |
| :--- | :--- | :--- |
| 클래스 / 인터페이스 / Enum | `PascalCase` | `OnggiBlockEntity`, `ModFoodItems` |
| 메서드 / 필드 / 지역 변수 | `camelCase` | `getFluidType()`, `fermentationTime` |
| 상수 / Supplier 필드 | `UPPER_SNAKE_CASE` | `SOY_SAUCE_BUCKET`, `RAW_FISH_CAKE` |
| 레지스트리 ID / 리소스 파일명 | `lower_snake_case` | `kimchi_cabbage`, `fish_cake.json` |
| 다국어 / 태그 키 | `lower_snake_case` | `item.koreandelight.red_pepper` |

### 6.3 언어 및 한글 맞춤법
- 표준 표기법 준수:
  - `고춧가루` (O) / `고추가루` (X)
  - `액젓` (O) / `액젖` (X)
- 레지스트리 ID 및 영문 명칭은 기획 문서(`docs/recipes.md`, `docs/roadmap.md`)의 공식 영문 ID를 우선합니다.

---

## 7. 빌드, 실행 및 검증 명령어

프로젝트 루트 디렉터리에서 실행합니다:

```powershell
# 1. 프로젝트 전체 빌드 및 검사
.\gradlew.bat build

# 2. 데이터젠 (레시피, 발전과제 등 자동 생성)
.\gradlew.bat :neoforge:runData --stacktrace

# 3. 개발 클라이언트 실행 (인게임 기능 검증)
.\gradlew.bat :neoforge:runClient

# 4. 개발 서버 실행
.\gradlew.bat :neoforge:runServer

# 5. 단위 테스트 실행
.\gradlew.bat :neoforge:test
```

> 💡 **검증 필수 체크리스트**:
> - 레시피나 데이터 변경 후 반드시 `:neoforge:runData`를 실행하여 diff를 검토합니다.
> - 신규 아이템/블록/기능 추가 후 `:neoforge:runClient`를 실행하여 크리에이티브 탭, 모델, 번역 누락 여부를 확인합니다.

---

## 8. Git 커밋 및 협업 규칙

### 8.1 커밋 메시지 컨벤션 (Conventional Commits)
간결하고 명확한 접두사를 사용하며, 작업 단위별로 분리하여 커밋합니다.

- `feat:` 새로운 기능, 아이템, 블록, 레시피 추가
- `fix:` 버그 수정, 렌더링/크래시 에러 해결
- `refactor:` 기능 변경 없는 코드 구조 개선
- `style:` 코드 서식, 주석 정리, 네이밍 변경
- `docs:` 기획 문서, 레시피 가이드, README 수정
- `chore:` 빌드 스크립트, 종속성 버전, 데이터젠 캐시 업데이트

### 8.2 협업 및 문서 동기화
- 신규 아이템 추가/상태 변경 시 [docs/notion_item_list.csv](file:///e:/Develop/Java/KoreanDelight1.20.1/docs/notion_item_list.csv) 및 [docs/texture-resources.md](file:///e:/Develop/Java/KoreanDelight1.20.1/docs/texture-resources.md) 문서를 함께 갱신합니다.
- PR(Pull Request) 제출 시 사용자 관점의 변경점, 기술적 변경 내용, 검증에 사용한 실행 명령어를 명시합니다.
