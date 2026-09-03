# KoreanDelight 텍스처 작업 목록

다음 4개 아이템 텍스처는 최종본으로 유지한다.

- `green_onion` (대파)
- `red_pepper` (고추)
- `red_pepper_powder` (고춧가루)
- `kimchi` (김치)

배추와 옹기를 포함한 나머지 등록 아이템·블록 텍스처는 임시본이며 교체 대상이다. 아이템 PNG의 목표 경로는
`common/src/main/resources/assets/koreandelight/textures/item/<id>.png`이다.

## 아이템 텍스처 목록

상태 표기:

- `임시`: 현재 표시용 텍스처이며 최종 텍스처로 교체 필요
- `임시 복사`: `bean.png`를 복사해 임시 표시 중이며 최종 텍스처로 교체 필요
- `동적 모델`: PNG 없이 NeoForge 유체 컨테이너 모델이 양동이와 유체를 합성함

### 작물·씨앗

| ID | 이름 | 상태 | 비고 |
| --- | --- | --- | --- |
| `kimchi_cabbage` | 배추 | 임시 | 기존 텍스처도 최종본으로 교체 필요; 작물 4단계 블록 텍스처 포함 |
| `red_pepper` | 고추 | 유지 | 아이템 및 작물 4단계 블록 텍스처 유지 |
| `green_onion` | 대파 | 유지 | 아이템 및 작물 4단계 블록 텍스처 유지 |
| `garlic` | 마늘 | 임시 복사 | `bean.png` 복사 완료; 아이템과 마늘 작물 4단계 블록 텍스처 필요 |
| `bean` | 콩 | 임시 복사 | 기준 텍스처; 감자처럼 자체 파종용 |
| `kimchi_cabbage_seeds` | 배추 씨앗 | 임시 복사 | 최종 씨앗 텍스처 필요 |
| `red_pepper_seeds` | 고추 씨앗 | 임시 복사 | 최종 씨앗 텍스처 필요 |
| `green_onion_seeds` | 대파 씨앗 | 임시 복사 | 최종 씨앗 텍스처 필요 |

### 양념·중간 재료

| ID | 이름 | 상태 | 현재 모델 |
| --- | --- | --- | --- |
| `soy_sauce_bucket` | 간장 양동이 | 동적 모델 | 별도 PNG 없음; NeoForge 유체 컨테이너 모델이 양동이와 간장을 합성 |
| `red_pepper_powder` | 고춧가루 | 유지 | 기존 텍스처 유지 |
| `gochujang` | 고추장 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `fish_sauce` | 액젓 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `minced_garlic` | 다진 마늘 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `tteok` | 떡 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `minced_fish` | 다진 생선살 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `raw_fish_cake` | 생 어묵 반죽 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `raw_kimchi_jeon` | 생 김치전 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `raw_pajeon` | 생 해물파전 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |

### 음식·완제품

| ID | 이름 | 상태 | 현재 모델 |
| --- | --- | --- | --- |
| `kimchi` | 김치 | 유지 | 기존 텍스처 유지 |
| `aged_kimchi` | 묵은지 | 임시 복사 | `bean.png` 복사 완료; 최종 묵은지 외형 필요 |
| `doenjang` | 된장 | 임시 복사 | `bean.png` 복사 완료; 최종 된장 질감 필요 |
| `fish_cake` | 어묵 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `kimchi_jeon` | 김치전 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `pajeon` | 해물파전 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `bibimbap` | 비빔밥 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `chicken_kalguksu` | 닭칼국수 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `tteokbokki` | 떡볶이 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `jeyuk_bokkeum` | 제육볶음 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `pork_gukbap` | 돼지국밥 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `doenjang_jjigae` | 된장찌개 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |
| `sikhye` | 식혜 | 임시 복사 | `bean.png` 복사 및 전용 모델 연결 완료 |

## 블록·작물 텍스처

아래 블록 및 작물 단계 텍스처 중 대파·고추를 제외한 항목은 임시본이다. 블록 아이템은 연결된 블록 텍스처를 사용한다.

| 대상 | 경로 | 상태 |
| --- | --- | --- |
| 옹기 | `textures/block/onggi.png` | 임시 |
| 메주 블록 | `textures/block/meju_block.png` | 임시 |
| 발효 메주 블록 | `textures/block/fermented_meju_block.png` | 임시 |
| 된장 블록 | `textures/block/doenjang_block.png` | 임시 |
| 김장 대야 | `textures/block/kimjang_basin.png` | 임시 |
| 콩 작물 | `textures/block/bean_crop_stage0.png` ~ `stage3.png` | 임시 |
| 배추 작물 | `textures/block/kimchi_cabbage_crop_stage0.png` ~ `stage3.png` | 임시 |
| 고추 작물 | `textures/block/red_pepper_crop_stage0.png` ~ `stage3.png` | 유지 |
| 대파 작물 | `textures/block/green_onion_crop_stage0.png` ~ `stage3.png` | 유지 |
| 마늘 작물 | `textures/block/garlic_crop_stage0.png` ~ `stage3.png` | 미제작 |

## 모델 작업 규칙

새 PNG를 넣을 때 임시 바닐라 모델을 그대로 두지 말고, 해당 `models/item/<id>.json`을 다음 형태의 생성형 모델로 바꾼다.

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "koreandelight:item/<id>"
  }
}
```

기본 아이템 텍스처는 투명 배경 PNG로 제작하고, 기존 리소스 규격에 맞춰 16×16 또는 동일 배율을 사용한다. 음식은 완성 상태와 조리 전 상태가 한눈에 구분되도록 색상과 실루엣을 다르게 만든다.

## 제작 우선순위

1. `garlic`, `gochujang`, `fish_sauce`, `minced_garlic`
2. `tteok`, `minced_fish`, `raw_fish_cake`, `fish_cake`
3. `raw_kimchi_jeon`, `kimchi_jeon`, `raw_pajeon`, `pajeon`
4. `bibimbap`, `chicken_kalguksu`, `tteokbokki`, `jeyuk_bokkeum`, 국밥류, `sikhye`
5. 기존 아이템, 씨앗·콩·된장·묵은지와 블록·작물 텍스처 교체
