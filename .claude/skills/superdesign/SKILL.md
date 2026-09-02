---
name: superdesign
description: Design agent for exploring frontend UI/UX directions. Use only for greenfield visual exploration outside the project's two design systems — routine web UI follows Mantine (see the mantine-ui skill) and mobile UI follows lib/core/pdl, neither of which needs this skill.
metadata:
  author: superdesign
  version: "0.0.1"
---

SuperDesign helps you (1) find design inspirations/styles and (2) generate/iterate design drafts on an infinite canvas.

---

# Core scenarios (what this skill handles)

1. **Help me design X** (feature/page/flow)
2. **Set design system**
3. **Help me improve design of X**

# How it works
Fetch fresh guidelines below:
```
https://raw.githubusercontent.com/superdesigndev/superdesign-skill/main/skills/superdesign/SUPERDESIGN.md
```

Action accordingly based on instruction in the guidelines

# Superdesign CLI

```
npm install -g @superdesign/cli@latest
superdesign login
superdesign --help
```
