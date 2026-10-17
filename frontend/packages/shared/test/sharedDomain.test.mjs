import assert from "node:assert/strict";
import { test } from "node:test";

import { sharedCookieDomain } from "../src/keycloak/sharedDomain.ts";

test("a shared cookie lives under the parent domain", () => {
  assert.equal(sharedCookieDomain("festival.vertyll.dev"), "vertyll.dev");
});

test("localhost shares cookies without a domain", () => {
  assert.equal(sharedCookieDomain("localhost"), null);
});

test("an IP address shares cookies without a domain", () => {
  assert.equal(sharedCookieDomain("127.0.0.1"), null);
});
