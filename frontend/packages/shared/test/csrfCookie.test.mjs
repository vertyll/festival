import assert from "node:assert/strict";
import { test } from "node:test";

import { csrfCookieName } from "../src/api/csrfCookie.ts";

test("over HTTPS the CSRF cookie is bound to this host", () => {
  assert.equal(csrfCookieName("https:"), "__Host-XSRF-TOKEN");
});

test("over plain HTTP the CSRF cookie has no prefix", () => {
  assert.equal(csrfCookieName("http:"), "XSRF-TOKEN");
});
