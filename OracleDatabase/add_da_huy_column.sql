-- Migration script to add cancellation status column to CT_DKDV table
-- Execute this script on your database before running the application

-- Add DA_HUY column to CT_DKDV table
ALTER TABLE CT_DKDV ADD DA_HUY NUMBER(1) DEFAULT 0 CHECK (DA_HUY IN (0, 1));

-- Update existing records to set DA_HUY = 0 (not cancelled)
UPDATE CT_DKDV SET DA_HUY = 0 WHERE DA_HUY IS NULL;

-- Commit changes
COMMIT;
