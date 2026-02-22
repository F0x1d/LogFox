# Navigation and Structure

## Navigation conventions

- Reducer emits navigation side effects.
- Container consumes navigation side effects and executes navigation.
- Do not navigate directly from passive UI elements.

## File organization conventions

- One top-level type per file.
- File name equals type name.
- Avoid helper type bundles in a single file.
- Co-locate related types by layer and feature package.

## Practical refactor strategy

When legacy code does not match target architecture:

1. Preserve behavior first.
2. Enforce dependency boundaries first.
3. Introduce `ViewState` boundary if missing.
4. Move side effects out of reducers/UI.
5. Normalize naming and file split last.

## Architecture review template

Use this template in final reports:

```text
Applied rules:
- ...

Modules changed:
- ...

Boundary checks:
- presentation->api only: pass/fail
- impl leakage: pass/fail

TEA checks:
- reducer purity: pass/fail
- main-thread send: pass/fail
- viewstate mapping: pass/fail

Known deviations and why:
- ...

Recommended follow-ups:
- ...
```
