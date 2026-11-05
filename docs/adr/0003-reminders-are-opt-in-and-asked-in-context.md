# Reminders are opt-in, and the permission is asked for in context

Overdue freelance invoices are something Kavindu has forgotten to chase twice (SRS §3.2), so a reminder is useful. A notification is also the quickest way for a finance app to feel nagging, and Android 13 makes the permission a runtime choice. The app therefore sends nothing until he asks. The Income screen offers reminders only when an invoice is actually overdue, and the notification permission is requested at that moment, so the request makes sense to him. Saying no, or denying the permission, is remembered and not asked again.

Once on, one WorkManager job runs daily, reads the records already cached on the phone, and posts at most one notification a day. It stays silent when signed out, when nothing is overdue, or when the permission is off. A periodic job runs at the system's convenience, so the notification arrives some time in the day, not at a fixed hour.

## Considered Options

- **Ask for the permission at first launch**: rejected — he has no reason yet to say yes, and a refusal at that point is hard to undo.
- **Exact alarms at 9 a.m.**: rejected — needs the exact-alarm permission and drains battery for a reminder that doesn't need minute precision.
- **A push message from a server**: rejected — needs Cloud Functions and tokens for a message the phone can work out from its own data.
