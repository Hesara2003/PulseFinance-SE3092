# Automated Release Workflow & Google Drive Integration

## 1. Overview

PulseFinance features an automated CI/CD release pipeline configured via GitHub Actions in [`.github/workflows/release-apk-to-drive.yml`](file:///Users/hesaraperera/Desktop/PulseFinance/.github/workflows/release-apk-to-drive.yml). 

Whenever a major version or release tag (e.g. `v1.0.0`, `v2.0.0`) is pushed or published on GitHub, the pipeline:
1. Compiles and packages the Android Application Package (**APK**) and Android App Bundle (**AAB**).
2. Signs the APK so it is immediately installable on physical Android devices.
3. Automatically uploads the bundled artifacts to the shared Google Drive folder:
   - **Target Folder URL:** [`https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d`](https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d?usp=sharing)
   - **Target Folder ID:** `1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d`
4. Attaches the release APK/AAB to the GitHub Release and preserves build artifacts in GitHub Actions for 30 days.

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
1. Navigate to your repository: [https://github.com/Hesara2003/PulseFinance](https://github.com/Hesara2003/PulseFinance).
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

## 3. Google Drive Service Account Configuration

To enable GitHub Actions to upload files into the private Google Drive folder, configure a Google Cloud Service Account:

### Step 1: Create a Service Account
1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Select your Firebase/Google Cloud project (or create a new project).
3. Go to **APIs & Services** $\to$ **Library**, search for **Google Drive API**, and click **Enable**.
4. Go to **IAM & Admin** $\to$ **Service Accounts**, and click **Create Service Account**:
   - Name: `pulsefinance-drive-uploader`
   - Role: None required at project level (access is granted on folder directly).
5. Click on the created service account, go to the **Keys** tab $\to$ **Add Key** $\to$ **Create new key** $\to$ select **JSON**.
6. Download the generated `.json` key file to your computer.

### Step 2: Share the Google Drive Folder with the Service Account
1. Open the target Google Drive folder:
   [https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d](https://drive.google.com/drive/folders/1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d?usp=sharing)
2. Click the **Share** button in the top right.
3. Paste the Service Account's email address (e.g. `pulsefinance-drive-uploader@your-project.iam.gserviceaccount.com`).
4. Set permission to **Editor** and click **Send / Share**.

### Step 3: Add Repository Secret in GitHub
1. Open your repository: [https://github.com/Hesara2003/PulseFinance/settings/secrets/actions](https://github.com/Hesara2003/PulseFinance/settings/secrets/actions).
2. Click **New repository secret**.
3. Name: `GDRIVE_SA_KEY`
4. Value: Paste the entire content of the downloaded Service Account JSON file.
5. Click **Add secret**.

---

## 4. Keystore Signing Configuration (Optional)

If you have a production release keystore, you can configure these optional secrets for official Play Store release signing:

| Secret Name | Description |
| :--- | :--- |
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded release `.keystore` or `.jks` file |
| `KEYSTORE_PASSWORD` | Password for the keystore |
| `KEY_ALIAS` | Key alias name |
| `KEY_PASSWORD` | Password for the key alias |

> **Note:** If these secrets are not configured, the workflow automatically self-signs the release APK using an automated CI key. This ensures the uploaded APK on Google Drive is **immediately installable** on test Android devices without manual signing.

---

## 5. Artifacts Generated in Google Drive

When the workflow completes, the Google Drive folder will contain:
- `PulseFinance-v<VERSION>.apk` — Standalone installable Android application.
- `PulseFinance-v<VERSION>.aab` — Android App Bundle for Google Play Console submission.

In addition, each run logs direct links (`webViewLink` and `webContentLink`) in the GitHub Actions summary tab for 1-click access.
