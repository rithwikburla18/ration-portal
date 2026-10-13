-- Allow full citizen grievance details without truncating the submitted description.
ALTER TABLE grievances ALTER COLUMN description TYPE TEXT;
