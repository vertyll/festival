# Translation catalog

Where the text behind every message key comes from, and how an administrator's edits survive a deployment.

The catalog ships in `src/main/resources/i18n`. At startup the stored catalog is brought in line with those files.
An admin can override any message, one at a time or by importing a spreadsheet exported from the panel; an override
must parse as ICU MessageFormat and may use only the placeholders of its default.
