import assert from "node:assert/strict";
import { test } from "node:test";

import { contentSecurityPolicy } from "../src/security/contentSecurityPolicy.ts";

test("scripts run from this origin or with the page's nonce, and nothing else inline", () => {
  const policy = contentSecurityPolicy("abc", false);
  assert.match(policy, /script-src 'self' 'nonce-abc';/);
  assert.doesNotMatch(policy, /unsafe-eval|unsafe-inline|unsafe-hashes/);
});

test("styles come from this origin or with the page's nonce, and no style attribute applies", () => {
  const policy = contentSecurityPolicy("abc", false);
  assert.match(policy, /style-src 'self' 'nonce-abc';/);
  assert.doesNotMatch(policy, /style-src-(elem|attr)|unsafe-inline|unsafe-hashes/);
});

test("fonts come from this origin only", () => {
  const policy = contentSecurityPolicy("abc", false);
  assert.match(policy, /font-src 'self';/);
  assert.doesNotMatch(policy, /fonts\.googleapis\.com|fonts\.gstatic\.com/);
});

test("no other site may frame the page", () => {
  assert.match(contentSecurityPolicy("abc", false), /frame-ancestors 'none'/);
});

test("the development server may evaluate code, apply its own inline styles and show images over plain HTTP", () => {
  const policy = contentSecurityPolicy("abc", true);
  assert.match(policy, /script-src 'self' 'nonce-abc' 'unsafe-eval';/);
  assert.match(policy, /style-src 'self' 'unsafe-inline';/);
  assert.match(policy, /img-src 'self' data: blob: https: http:;/);
});
