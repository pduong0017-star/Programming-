# VibeCut Firestore Security Specification

## 1. Data Invariants
- **Global Default Deny**: Any path not explicitly matched under `/video_projects/{projectId}` or `/ai_templates/{templateId}` is strictly denied (`allow read, write: if false`).
- **Authentication & Multi-User Isolation**: Every read (`get`, `list`) and write (`create`, `update`, `delete`) requires `request.auth != null` and enforces strict ownership (`userId == request.auth.uid`).
- **Query Enforcer**: `allow list` on both `/video_projects` and `/ai_templates` verifies `resource.data.userId == request.auth.uid`, requiring client queries to filter with `.whereEqualTo("userId", uid)`.
- **Immutable Metadata**: `userId` and `createdAt` cannot be modified after creation.
- **Temporal Integrity**: `createdAt` and `updatedAt` must be Firestore `timestamp` values `<= request.time`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read**: `unauthDb.collection("video_projects").get()` -> Reject (`PERMISSION_DENIED`).
2. **Cross-User Read (`get`)**: Alice reading Bob's `video_projects/bob_proj` -> Reject.
3. **Unfiltered List Query**: Alice calling `db.collection("video_projects").get()` without `.where("userId", "==", ALICE_UID)` -> Reject.
4. **Identity Spoofing on Create**: Alice creating a `video_projects` doc with `userId: "bob_456"` -> Reject.
5. **Shadow Field Injection on Create**: Creating a `video_projects` doc with extra field `isVerifiedAdmin: true` -> Reject via `keys().hasOnly(...)`.
6. **Shadow Field Injection on Update**: Updating a `video_projects` doc with `ghostField: "hacked"` -> Reject via `affectedKeys().hasOnly(...)`.
7. **Immutable Field Tampering (`userId`)**: Updating `userId` to another user on an existing document -> Reject.
8. **Immutable Field Tampering (`createdAt`)**: Updating `createdAt` to a new timestamp -> Reject.
9. **Enum Poisoning**: Setting `scenePreset: "invalid_preset"` or `aiFilter: "hacked_filter"` -> Reject.
10. **Boundary Overflow (Denial of Wallet)**: Setting `title` > 120 chars or `bpm` = 9999 -> Reject.
11. **Unbounded Array Poisoning**: Creating an `ai_templates` doc with 15 `storyboardBeats` items (limit is 10) -> Reject.
12. **Future Timestamp Spoofing**: Creating a document with `createdAt` set in the future (`> request.time`) -> Reject.
