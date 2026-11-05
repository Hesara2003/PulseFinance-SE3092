# Automated Release Workflow & Google Drive Integration

## 1. Overview

PulseFinance-SE3092 has an automated release pipeline in [`.github/workflows/release-apk-to-drive.yml`](../.github/workflows/release-apk-to-drive.yml). It gives the team (and the markers) an installable build of whatever is on `main`, without anyone building it locally.

It runs on **every push to `main`** (which includes every merged PR), on pushed version tags (`v1.2.3`), on a published GitHub Release, and manually (§2, Option C). Each run:
1. Compiles and packages the Android Application Package (**APK**) and Android App Bundle (**AAB**).
2. Signs the APK and AAB with the team's permanent release key and verifies the signature (§4), so the APK installs on physical devices and updates over the previous build.
3. Automatically uploads the bundled artifacts to the shared Google Drive folder:
   - **Target Folder URL:** [`https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d`](https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d?usp=sharing)
   - **Target Folder ID:** `1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d`
4. Attaches the release APK/AAB to the GitHub Release (tag or release triggers only), and keeps the build as a GitHub Actions artifact for 30 days.

**Version names:** pushes to `main` and manual runs without a version are named `1.0.0`. Each upload therefore replaces `PulseFinance-v1.0.0.apk` in the folder, and its Drive link stays the same. Tag a release (`v1.1.0`) to keep a separate copy.

---

## 2. Triggering Major Version Releases

### Option A: Tagging a Major Version via Git
Pushing any tag adhering to semantic major versions (e.g. `v1.0.0`, `v2.0.0`) automatically kicks off the workflow:

```bash
# 1. Create a major release tag
git tag v1.0.0

# 2. Push tag to GitHub
git push origin v1.0.0
```

### Option B: Publishing a GitHub Release
1. Navigate to your repository: [Hesara2003/PulseFinance-SE3092](https://github.com/Hesara2003/PulseFinance-SE3092).
2. Go to **Releases** $\to$ **Draft a new release**.
3. Create a tag (e.g. `v1.0.0`), write release notes, and click **Publish release**.
4. The workflow detects the published release and triggers the build and drive upload.

### Option C: Manual Workflow Dispatch (Testing & On-Demand)
1. Go to the **Actions** tab in GitHub.
2. Select **"Release APK & Bundle to Google Drive"**.
3. Click **Run workflow**:
   - Choose branch (`main`).
   - Enter version name (e.g. `1.0.0`).
   - Select build type (`release` or `debug`).
   - Choose whether to build `.aab`.
4. Click the green **Run workflow** button.

---

## 3. Google Drive credentials

`scripts/upload_to_gdrive.py` accepts two kinds of credentials. It uses the OAuth refresh token when one is present, and otherwise the service-account key.

| Method | Secrets | Works with | Used by this repo |
|---|---|---|---|
| **A. OAuth refresh token** | `GDRIVE_CLIENT_ID`, `GDRIVE_CLIENT_SECRET`, `GDRIVE_REFRESH_TOKEN` | A **personal** Google Drive, using the owner's storage quota | **Yes.** The team folder is in a personal Drive |
| B. Service account | `GDRIVE_SA_KEY` | A **Shared Drive** (Workspace). Service accounts have no storage quota of their own, so they can't upload into a personal Drive | Set as a fallback |

### 3.1 Method A: OAuth refresh token

1. In the Google Cloud project that owns the OAuth client (`airy-berm-483018-d6`), make sure the **Google Drive API** is enabled. You need an OAuth client of type **Desktop app**.
2. On your machine, run the helper with that client's ID and secret:
   ```bash
   python3 scripts/authenticate_gdrive_oauth.py --client-id <ID> --client-secret <SECRET>
   ```
   - A browser opens; sign in with the Google account that owns the Drive folder and allow Drive access.
   - The script prints the three values and tries `gh secret set` on the current repo.
3. Check that the three secrets are on **PulseFinance-SE3092** (Settings → Secrets and variables → Actions). If not, add them by hand.
4. **Publish the OAuth consent screen** (Google Cloud → APIs & Services → OAuth consent screen → Publish app). While it is in *Testing* mode, Google expires refresh tokens after 7 days and the upload silently stops working.

**Two things to know:**
- **Secrets are read when a run is queued, not when it starts.** A run queued before you added or changed a secret uses the old (or empty) value, so the upload step fails with "Google Drive upload is not configured" and lists the missing secrets. Start a fresh run with Option C once the secrets are in.
- **A run that cannot upload fails; it is never green.** The upload step needs either all three of `GDRIVE_REFRESH_TOKEN`, `GDRIVE_CLIENT_ID` and `GDRIVE_CLIENT_SECRET`, or `GDRIVE_SA_KEY`. If none of those is complete, it stops with an error annotation that names what is missing. (Until 05/11/2026 it only printed a warning and succeeded, so a run could be green with nothing in Drive; that was issue #100.) When an upload works, the step logs `✅ Upload Complete!` and a Drive link.
- **Forks have no secrets,** so a push to `main` in a fork fails at this step. Disable the workflow in the fork if you don't want Drive uploads there.

### 3.2 Method B: Service account (Shared Drives only)

To use a Shared Drive instead, configure a Google Cloud service account:

#### Step 1: Create a Service Account
1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Select your Firebase/Google Cloud project (or create a new project).
3. Go to **APIs & Services** $\to$ **Library**, search for **Google Drive API**, and click **Enable**.
4. Go to **IAM & Admin** $\to$ **Service Accounts**, and click **Create Service Account**:
   - Name: `pulsefinance-drive-uploader`
   - Role: None required at project level (access is granted on folder directly).
5. Click on the created service account, go to the **Keys** tab $\to$ **Add Key** $\to$ **Create new key** $\to$ select **JSON**.
6. Download the generated `.json` key file to your computer.

#### Step 2: Share the Google Drive Folder with the Service Account
1. Open the target Google Drive folder:
   [https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d](https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d?usp=sharing)
2. Click the **Share** button in the top right.
3. Paste the Service Account's email address (e.g. `pulsefinance-drive-uploader@your-project.iam.gserviceaccount.com`).
4. Set permission to **Editor** and click **Send / Share**.

#### Step 3: Add Repository Secret in GitHub
1. Open [PulseFinance-SE3092 → Settings → Secrets → Actions](https://github.com/Hesara2003/PulseFinance-SE3092/settings/secrets/actions).
2. Click **New repository secret**.
3. Name: `GDRIVE_SA_KEY`
4. Value: Paste the entire content of the downloaded Service Account JSON file.
5. Click **Add secret**.

---

## 4. Release signing (required)

Android refuses to install an unsigned APK, and only installs an update signed with the same key as the version already on the phone. Every release is therefore signed with one permanent key, by Gradle itself (`signingConfigs.release` in `app/build.gradle.kts`).

| Secret Name | Description |
| :--- | :--- |
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded release `.jks` file |
| `KEYSTORE_PASSWORD` | Password for the keystore |
| `KEY_ALIAS` | Key alias name |
| `KEY_PASSWORD` | Password for the key alias |

- If `ANDROID_KEYSTORE_BASE64` is missing, a release run **fails** instead of uploading an APK nobody can install.
- After the build, `apksigner verify` checks the APK; an unsigned or broken APK fails the run before anything reaches Google Drive.
- The keystore never enters the repo. Keep a backup of the `.jks` file and its passwords somewhere safe outside GitHub: secrets cannot be read back, and a lost key means no build can ever update the installed app again.
- Google sign-in in release builds needs the key's SHA-1 added to the Android app in the Firebase console.

To build a signed release locally, point Gradle at the keystore:

```bash
RELEASE_KEYSTORE_FILE=/path/to/release.jks RELEASE_KEYSTORE_PASSWORD=… \
RELEASE_KEY_ALIAS=… RELEASE_KEY_PASSWORD=… ./gradlew assembleRelease
```

Without these variables, `assembleRelease` produces an unsigned APK that is only useful for inspection.

---

## 5. Artifacts Generated in Google Drive

When the workflow completes, the Google Drive folder will contain:
- `PulseFinance-v<VERSION>.apk` — Standalone installable Android application.
- `PulseFinance-v<VERSION>.aab` — Android App Bundle for Google Play Console submission.

In addition, each run logs direct links (`webViewLink` and `webContentLink`) in the GitHub Actions summary tab for 1-click access.
