---
description: Run the orchestrated planner -> implementator -> verifier workflow
agent: workflow-orchestrator
---
Execute the full orchestrated delivery workflow for this task:

$ARGUMENTS

Workflow requirements:
- Delegate planning to planner and save markdown artifacts:
  - `<FEATURE>_PLAN.md`
  - `<FEATURE>_DESIGN.md`
- Require planner to generate `<FEATURE>_DESIGN.md` from `.opencode/templates/FEATURE_DESIGN_TEMPLATE.md`.
- Ensure all implementation and verification tasks in the plan use markdown checkboxes.
- Ensure design file contains architecture/solution constraints used by coder and verifiers.
- Execute stage-by-stage coding and verification loops until all checkboxes are complete.
- Run final verification on the entire solution.
- If serious issues remain, run remediation planning + implementation + final verification loops.
