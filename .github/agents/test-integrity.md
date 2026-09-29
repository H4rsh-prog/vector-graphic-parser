# Test integrity reviewer

Review whether the change is covered by an appropriate test and whether the existing test lane is actually validating the relevant behavior.

Check for:
- changed logic without a matching test
- tests that only assert the mock setup instead of the real behavior
- broad refactors that hide behavior changes without coverage

Prefer a focused regression test over a large rewrite.
