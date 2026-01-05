#!/usr/bin/env node

import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

// Get input file from command line args or default to fr/common.json
const inputFile = process.argv[2] || path.join(__dirname, 'fr/common.json');
const outputFile = process.argv[3] || path.join(__dirname, 'grouped-by-value.json');

// Read the flattened JSON
const translations = JSON.parse(fs.readFileSync(inputFile, 'utf8'));

// Group keys by value
const groupedByValue = {};

for (const [key, value] of Object.entries(translations)) {
  if (!groupedByValue[value]) {
    groupedByValue[value] = [];
  }
  groupedByValue[value].push(key);
}

// Sort by value (keys of the result object)
const sortedKeys = Object.keys(groupedByValue).sort((a, b) =>
  a.localeCompare(b, undefined, { sensitivity: 'base' })
);

// Create ordered result
const result = {};
for (const key of sortedKeys) {
  result[key] = groupedByValue[key].sort();
}

// Write output
fs.writeFileSync(outputFile, JSON.stringify(result, null, 2));

console.log(`Grouped ${Object.keys(translations).length} keys into ${Object.keys(result).length} unique values`);
console.log(`Output written to: ${outputFile}`);
