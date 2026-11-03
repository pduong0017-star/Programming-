const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProjectPayload(uid, now) {
  return {
    userId: uid,
    authorName: "Alice Creator",
    title: "Neon Night Dance AI Cut",
    caption: "Testing AI beat sync in Tokyo!",
    hashtags: "#vibecut #aivideo #cyberpunk",
    scenePreset: "cyberpunk",
    aiFilter: "neon_glow",
    transitionStyle: "glitch_cut",
    musicTrack: "Cyber Phonk 2026",
    bpm: 135,
    durationSeconds: 15,
    autoCaptionsJson: '[{"start":0,"end":3,"text":"Wait for the drop!"}]',
    aiDirectorNotes: "Strong 3s hook, high contrast neon pacing.",
    viralScore: 94,
    likesCount: 12,
    isPublished: true,
    mediaUri: "",
    createdAt: now,
    updatedAt: now,
  };
}

function validTemplatePayload(uid, now) {
  return {
    userId: uid,
    name: "Cyber Beat Drop Transition",
    category: "dance_trend",
    hookLine: "Biến hình chỉ sau 1 nhịp bass!",
    storyboardBeats: [
      "0-2s: Mở đầu góc rộng bí ẩn",
      "2-5s: Chuyển cảnh glitch theo nhịp trống",
      "5-10s: Slow-motion neon glow",
    ],
    recommendedPreset: "cyberpunk",
    recommendedFilter: "neon_glow",
    recommendedBpm: 140,
    createdAt: now,
    updatedAt: now,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("1. Unauthenticated user cannot read video_projects", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("video_projects").get());
});

test("2. Authenticated user cannot read another user's video_project", async () => {
  const now = new Date(Date.now() - 1000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("video_projects")
      .doc("bob_proj_1")
      .set(validProjectPayload(BOB_UID, now));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("video_projects").doc("bob_proj_1").get());
});

test("3. Authenticated user can create and query their own video_projects with userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);
  await assertSucceeds(
    aliceDb
      .collection("video_projects")
      .doc("alice_proj_1")
      .set(validProjectPayload(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb.collection("video_projects").where("userId", "==", ALICE_UID).get()
  );
});

test("4. Authenticated user fails unfiltered list query on video_projects", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("video_projects").get());
});

test("5. Identity spoofing on create is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);
  await assertFails(
    aliceDb
      .collection("video_projects")
      .doc("spoof_proj")
      .set(validProjectPayload(BOB_UID, now))
  );
});

test("6. Shadow field injection on create and update is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);
  const basePayload = validProjectPayload(ALICE_UID, now);

  await assertFails(
    aliceDb
      .collection("video_projects")
      .doc("shadow_proj")
      .set({ ...basePayload, isVerifiedAdmin: true })
  );

  await assertSucceeds(
    aliceDb.collection("video_projects").doc("valid_proj").set(basePayload)
  );

  await assertFails(
    aliceDb
      .collection("video_projects")
      .doc("valid_proj")
      .update({ ghostField: "injected", updatedAt: new Date(Date.now() - 500) })
  );
});

test("7. Valid update on video_projects succeeds while immutable fields are protected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 2000);
  await assertSucceeds(
    aliceDb
      .collection("video_projects")
      .doc("proj_update")
      .set(validProjectPayload(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb
      .collection("video_projects")
      .doc("proj_update")
      .update({
        title: "Updated Neon Cut",
        likesCount: 99,
        updatedAt: new Date(Date.now() - 500),
      })
  );

  await assertFails(
    aliceDb
      .collection("video_projects")
      .doc("proj_update")
      .update({
        createdAt: new Date(Date.now() - 100),
        updatedAt: new Date(Date.now() - 100),
      })
  );
});

test("8. AiTemplate CRUD and security boundaries enforce ownership and array bounds", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date(Date.now() - 1000);

  await assertSucceeds(
    aliceDb
      .collection("ai_templates")
      .doc("tpl_1")
      .set(validTemplatePayload(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb.collection("ai_templates").where("userId", "==", ALICE_UID).get()
  );

  // Reject unbounded storyboardBeats (> 10 items)
  const tooManyBeats = Array.from({ length: 12 }, (_, i) => `Beat ${i}`);
  await assertFails(
    aliceDb
      .collection("ai_templates")
      .doc("tpl_overflow")
      .set({
        ...validTemplatePayload(ALICE_UID, now),
        storyboardBeats: tooManyBeats,
      })
  );
});
