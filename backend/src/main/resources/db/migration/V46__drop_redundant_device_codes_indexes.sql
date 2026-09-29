-- docs/LEDGER_*.md AUD-17 (audit B15): V5 declared device_code_hash and user_code UNIQUE, which
-- already gives each an index (device_codes_device_code_hash_key, device_codes_user_code_key), then
-- created a second, plain index on each. The duplicates only cost writes and space.
DROP INDEX IF EXISTS idx_device_codes_device_code_hash;
DROP INDEX IF EXISTS idx_device_codes_user_code;
