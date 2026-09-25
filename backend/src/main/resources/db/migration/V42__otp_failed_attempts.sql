-- Wrong codes tried against a login OTP. A 6-digit code lived its 5 minutes with no bound on the
-- guesses, so the million combinations could be walked through from a script; AuthService.verifyOtp
-- now burns the code after a handful of misses.
alter table auth_tokens
    add column failed_attempts integer not null default 0;
