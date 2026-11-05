# Deleting an account re-authenticates first, then erases the data, then the sign-in

Kavindu's records are the only copy of his financial history that the app holds, and the four previous tools failed him partly because data was hard to take back (SRS FR-DATA-1). The same trust means he must also be able to remove everything. Deleting an account has to do four things: prove it is him, erase his Firestore records (expenses, income, goals, accounts and the user document with his reviews, reserves and onboarding), delete the Firebase sign-in, and empty the on-device cache. The order matters because Firebase refuses to delete a sign-in that wasn't used recently, and a half-finished deletion is worse than none.

The order is therefore: re-authenticate (password, or the Google account picked again), erase the Firestore data, delete the Firebase user, clear Room. The Firestore step reads from the server and waits for each commit, so being offline fails with a clear message instead of reporting a deletion that hasn't reached the server.

## Considered Options

- **Delete the Firebase user first**: rejected — once the sign-in is gone the security rules refuse the data deletes, leaving his records orphaned in Firestore with no way to remove them.
- **Fire-and-forget deletes, like ordinary saves**: rejected — Firestore queues writes offline and reports success at once, so the app could say "deleted" while his data was still on the server.
- **A Cloud Function that deletes on account removal**: rejected for now — it is the robust answer for orphaned subcollections, but it needs the Blaze plan and server code the project doesn't have; revisit if records grow new subcollections.

## Consequences

If re-authentication fails, nothing is erased and he is told so. If the data deletes succeed and removing the sign-in then fails, the account is empty but still signs in; he can retry.
