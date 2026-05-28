# Graph Report - Martial_Madness_New  (2026-05-27)

## Corpus Check
- 1 files · ~1,513 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 24 nodes · 32 edges · 6 communities detected
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]

## God Nodes (most connected - your core abstractions)
1. `MartialMadness` - 4 edges
2. `FirstPagePanel` - 4 edges
3. `ImagePanel` - 4 edges
4. `MartialMadnessHolder` - 3 edges
5. `FixedPanelHolder` - 3 edges
6. `InstructionPanel` - 3 edges
7. `LevelPanelHolder` - 3 edges
8. `LevelPanel` - 3 edges
9. `Information` - 2 edges

## Surprising Connections (you probably didn't know these)
- `FirstPagePanel` --extends--> `JPanel`  [EXTRACTED]
  MartialMadness.java →   _Bridges community 1 → community 0_
- `FixedPanelHolder` --extends--> `JPanel`  [EXTRACTED]
  MartialMadness.java →   _Bridges community 1 → community 4_
- `InstructionPanel` --extends--> `JPanel`  [EXTRACTED]
  MartialMadness.java →   _Bridges community 1 → community 5_
- `LevelPanelHolder` --extends--> `JPanel`  [EXTRACTED]
  MartialMadness.java →   _Bridges community 1 → community 2_

## Communities

### Community 0 - "Community 0"
Cohesion: 0.33
Nodes (2): FirstPagePanel, ImagePanel

### Community 1 - "Community 1"
Cohesion: 0.4
Nodes (3): JPanel, LevelPanel, MartialMadnessHolder

### Community 2 - "Community 2"
Cohesion: 0.4
Nodes (2): Information, LevelPanelHolder

### Community 3 - "Community 3"
Cohesion: 0.67
Nodes (1): MartialMadness

### Community 4 - "Community 4"
Cohesion: 1.0
Nodes (1): FixedPanelHolder

### Community 5 - "Community 5"
Cohesion: 1.0
Nodes (1): InstructionPanel

## Knowledge Gaps
- **Thin community `Community 0`** (6 nodes): `FirstPagePanel`, `.FirstPagePanel()`, `.paintComponent()`, `ImagePanel`, `.ImagePanel()`, `.paintComponent()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 2`** (5 nodes): `Information`, `.Information()`, `MartialMadness.java`, `LevelPanelHolder`, `.LevelPanelHolder()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 3`** (4 nodes): `MartialMadness`, `.main()`, `.MartialMadness()`, `.run()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 4`** (2 nodes): `FixedPanelHolder`, `.FixedPanelHolder()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 5`** (2 nodes): `InstructionPanel`, `.InstructionPanel()`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MartialMadness` connect `Community 3` to `Community 2`?**
  _High betweenness centrality (0.245) - this node is a cross-community bridge._
- **Why does `FirstPagePanel` connect `Community 0` to `Community 1`, `Community 2`?**
  _High betweenness centrality (0.158) - this node is a cross-community bridge._
- **Why does `ImagePanel` connect `Community 0` to `Community 1`, `Community 2`?**
  _High betweenness centrality (0.158) - this node is a cross-community bridge._