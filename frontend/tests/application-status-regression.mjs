import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import vm from "node:vm";

const html = readFileSync(new URL("../pages/application-status.html", import.meta.url), "utf8");
const match = html.match(/^        function normalizeStatus\(value\) \{[\s\S]*?^        \}/m);

assert.ok(match, "normalizeStatus must exist in application-status.html");
const normalizeStatus = vm.runInNewContext("(" + match[0].trim().replace(/^function normalizeStatus/, "function") + ")");

const cases = [
  ["SUBMITTED", "PENDING"],
  ["PENDING", "PENDING"],
  ["verified", "VERIFIED"],
  ["VERIFIED", "VERIFIED"],
  ["PROCESSING", "PROCESSING"],
  ["APPROVED", "APPROVED"],
  ["REJECTED", "REJECTED"],
  ["unknown-new-status", "PENDING"],
];

for (const [input, expected] of cases) {
  assert.equal(normalizeStatus(input), expected, `normalizeStatus(${input}) should be ${expected}`);
}

assert.ok(html.includes('id="timelineVerificationStatus"'), "verification stage needs a dynamic status label");
assert.ok(html.includes('id="timelineDecisionStatus"'), "decision stage needs a dynamic status label");
assert.ok(html.includes('id="timelineCompletionStatus"'), "completion stage needs a dynamic status label");
assert.ok(!html.includes('status.includes("PROCESS")'), "VERIFIED must not be swallowed by a broad PROCESS substring match");

console.log(`Application status regression checks passed (${cases.length} status cases + timeline markup).`);
