# pc-doqtar-connection-service

Secure server-side database connection service for PC DOQTAR.

## Firebase Authentication (server)

The API validates Firebase ID tokens with the Firebase Admin Java SDK. The public `GET /` liveness endpoint remains unauthenticated. All other routes, including the existing database test endpoints, require:

```http
Authorization: Bearer <Firebase ID token>
```

The authenticated Firebase UID and email are available to controllers as request attributes named `firebaseUid` and `firebaseEmail`. Use the verified UID for ownership checks; never trust a UID supplied in a request body or query parameter.

### Firebase project

The default Firebase project ID is `pc-doqtar`. Override it with `FIREBASE_PROJECT_ID` if necessary.

The server needs a trusted service-account credential to verify tokens:

- **Local Eclipse development:** set `GOOGLE_APPLICATION_CREDENTIALS` in the Run Configuration environment to the path of a service-account JSON file downloaded from Firebase/Google Cloud IAM for the `pc-doqtar` project. Keep that file outside the repository.
- **Hosted environment:** configure `FIREBASE_SERVICE_ACCOUNT_JSON` as a secret environment variable containing the service-account JSON, or use the host's Google Application Default Credentials where supported.

Never commit service-account JSON, private keys, Firebase ID tokens, or refresh tokens. The Firebase client API key is not a substitute for server credentials.

### Authentication endpoint

`GET /auth/me` returns the verified token identity:

```json
{
  "uid": "Firebase-authenticated-user-uid",
  "email": "user@example.com"
}
```

It returns HTTP 401 when the bearer token is missing, invalid, expired, revoked, or belongs to a disabled account.

### Local test

1. Enable the intended sign-in provider (for example Email/Password) in Firebase Console → Authentication → Sign-in method.
2. Configure Firebase Admin credentials in the Eclipse Run Configuration as described above.
3. Run `mvn clean verify`, then start the application.
4. Confirm `GET /` still returns the public server-running message.
5. Confirm `GET /auth/me` without an Authorization header returns HTTP 401.
6. Sign in with the desktop client to obtain a Firebase ID token, then call `GET /auth/me` with `Authorization: Bearer <ID token>`; it should return the token's UID and email.

This change verifies identity at the API boundary. Database records and future device/report endpoints must still enforce ownership using the verified UID.
