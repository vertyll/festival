# Media storage

Where uploaded files are written, and how they reach the browser.

Images are uploaded through the admin panel to `POST /api/admin/media` and stored in the object storage (Garage locally,
any S3-compatible service in production). They are served straight from the storage's public address
(`application.media.public-base-url`), never through the back-end.
