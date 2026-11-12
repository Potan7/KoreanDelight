---
trigger: always_on
---

# Notion Workspace Rules (Antigravity Only)

Notion is connected via the `notion_notion` MCP plugin exclusively in Antigravity (other AIs the user switches between cannot access Notion).

## Strict Access Boundary

- **ONLY access `Korean Delight` and its descendant pages/databases.**
- Never search, read, update, or reference any Notion page or database outside the `Korean Delight` tree.
- Root Page: `Korean Delight` — `https://app.notion.com/p/2d7e7b38a7f580ec89d3c3fd5932c6d4`

## Known Pages & Databases Under `Korean Delight`

- `📦 아이템` (Database): `https://app.notion.com/p/3ece7b38a7f580f8ac9bcdce114a3bfb` (Data Source: `collection://3ece7b38-a7f5-8010-81c1-000bfe9bff17`)
- `🥔 음식`: `https://app.notion.com/p/3ede7b38a7f580c6bdbfe41e93de0729` (Inline DB: `https://app.notion.com/p/3ede7b38a7f58068a01ac9e265a09f10`)
- `🟦 블록`: `https://app.notion.com/p/3ede7b38a7f580f2a626f19d825b7fd5` (Inline DB: `https://app.notion.com/p/3ede7b38a7f580e986def40cd17eece4`)
- `🥕 작물, 재료`: `https://app.notion.com/p/3ede7b38a7f58042a5cff19be21e2b12` (Inline DB: `https://app.notion.com/p/3ede7b38a7f5800a912cde05b602ed3a`)
- `발효`: `https://app.notion.com/p/2d9e7b38a7f5801fbd25f5988cfa22cb`
- `매움 버프`: `https://app.notion.com/p/2d9e7b38a7f5803fabc5ec1b933e770f`
- `조리`: `https://app.notion.com/p/3ede7b38a7f58072bbb2d4976bca8918`
- `김장`: `https://app.notion.com/p/2e4e7b38a7f58069bfebd32291e211dd`
- `🎨 아트 현황`: `https://app.notion.com/p/3ede7b38a7f58068af4bf8a13853beb2` (Inline DBs: `https://app.notion.com/p/3ede7b38a7f580ba805bd2f5206283af`, `https://app.notion.com/p/3eee7b38a7f580acaa43f151ad3886ac` / `collection://3eee7b38-a7f5-805b-baff-000bbb6770e1`)
- `구현 보고`: `https://app.notion.com/p/3ede7b38a7f580589934de84143d088e`
- `버그`: `https://app.notion.com/p/3ede7b38a7f5803bb2f8e2ed593861597`
- `릴리즈 노트`: `https://app.notion.com/p/3ede7b38a7f5801b92fce61c6ab9f602`

## `📦 아이템` Sync Rules

- **Grouped entries**: Some Notion entries represent a derived item family rather than a single registry ID:
  - `간장`: covers liquid/fluid, bucket, and bottle forms.
  - `밥그릇`: covers empty/filled bowl variants.
  - `상추`: covers the `ssam` / `open_ssam` wrapping system (do not create a separate `쌈` row in `📦 아이템`).
- **`비고` column**: Never write mod registry IDs or temporary/placeholder texture notes in `비고`.
- **Multi-AI handoff**: When another AI modified code without Notion access, inspect the workspace changes (`git log` / `git diff`) and sync the corresponding Notion documents within `Korean Delight`.
