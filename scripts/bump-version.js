#!/usr/bin/env node
"use strict";
const fs = require("node:fs");
const path = require("node:path");

const root = path.resolve(__dirname, "..");
const version = process.argv[2] || new Date().toISOString().replace(/[-:TZ.]/g, "").slice(0, 14);
const allowed = new Set([".html", ".css", ".js"]);
let changed = 0;
function walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const file = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      if (!["node_modules", "tests", ".git"].includes(entry.name)) walk(file);
      continue;
    }
    if (!allowed.has(path.extname(entry.name).toLowerCase())) continue;
    let source = fs.readFileSync(file, "utf8");
    const next = source.replace(/((?:src|href)\s*=\s*["']|url\(\s*["']?)(\.?\.?\/)?([^"'()?#]+\.(?:css|js|png|jpe?g|svg|webp|gif|ico|woff2?))([^"'()?#]*)(["']?\s*\)?)/gi,
      (whole, prefix, rel, resource, query, suffix) => {
        if (/^(?:https?:|data:|\/\/|#)/i.test(resource) || /^(?:https?:|data:|\/\/)/i.test((rel || "") + resource)) return whole;
        const cleanQuery = (query || "").replace(/(?:\?|&)v=[^&]*/g, "").replace(/^&/, "");
        return prefix + (rel || "") + resource + "?v=" + version + (cleanQuery ? "&" + cleanQuery : "") + suffix;
      });
    if (next !== source) {
      fs.writeFileSync(file, next, "utf8");
      changed++;
      console.log(path.relative(root, file));
    }
  }
}
walk(root);
console.log(`Updated ${changed} frontend files to asset version ${version}.`);
