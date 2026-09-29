-- docs/LEDGER_*.md ISSUE-7: a bug report may come without a description — the member who hit an
-- error may not know what happened, and the context, error and log speak for them. A suggestion
-- still needs one; FeedbackService enforces it.
alter table feedback_reports
    alter column message drop not null;
