#!/usr/bin/env python3
"""
PulseFinance Google Drive Release Upload Script
Uploads built APK/AAB bundles to Google Drive folder using a Service Account.
"""

import os
import sys
import json
import base64
import argparse
from pathlib import Path

def parse_args():
    parser = argparse.ArgumentParser(description="Upload APK/Release artifacts to Google Drive.")
    parser.add_argument("--file", "-f", required=True, help="Path to the file to upload (e.g., .apk, .aab)")
    parser.add_argument(
        "--folder-id", "-d",
        default=os.environ.get("GDRIVE_FOLDER_ID", "1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d"),
        help="Google Drive destination Folder ID"
    )
    parser.add_argument("--name", "-n", default=None, help="Destination filename on Google Drive")
    parser.add_argument("--credentials", "-c", default=None, help="Path to Service Account JSON key or JSON string")
    parser.add_argument("--dry-run", action="store_true", help="Simulate upload without actual API calls")
    return parser.parse_args()

def load_credentials(creds_input):
    from google.oauth2 import service_account

    scopes = [
        "https://www.googleapis.com/auth/drive",
        "https://www.googleapis.com/auth/drive.file"
    ]

    # 1. From CLI argument or environment variables
    raw = creds_input or os.environ.get("GDRIVE_SA_KEY") or os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")
    if not raw:
        raise ValueError(
            "Missing Google Drive Service Account credentials! "
            "Please provide --credentials or set GDRIVE_SA_KEY in environment/secrets."
        )

    # Check if raw is an existing file path
    if os.path.exists(raw):
        print(f"[*] Loading credentials from file: {raw}")
        return service_account.Credentials.from_service_account_file(raw, scopes=scopes)

    # Check if raw is Base64 encoded JSON
    try:
        decoded = base64.b64decode(raw).decode("utf-8")
        if "{" in decoded and "client_email" in decoded:
            print("[*] Loaded credentials from base64-encoded string.")
            info = json.loads(decoded)
            return service_account.Credentials.from_service_account_info(info, scopes=scopes)
    except Exception:
        pass

    # Check if raw is plain JSON string
    try:
        info = json.loads(raw)
        print("[*] Loaded credentials from raw JSON string.")
        return service_account.Credentials.from_service_account_info(info, scopes=scopes)
    except Exception as e:
        raise ValueError(f"Could not parse Service Account credentials: {e}")

def main():
    args = parse_args()
    file_path = Path(args.file).resolve()

    if not file_path.exists():
        print(f"[!] Error: File '{file_path}' does not exist.", file=sys.stderr)
        sys.exit(1)

    file_name = args.name or file_path.name
    file_size_mb = file_path.stat().st_size / (1024 * 1024)

    print("==================================================")
    print("🚀 PulseFinance Release Artifact Uploader")
    print("==================================================")
    print(f"📦 Local File:     {file_path}")
    print(f"🏷️  Upload Name:    {file_name}")
    print(f"📏 File Size:      {file_size_mb:.2f} MB")
    print(f"📁 Drive Folder ID: {args.folder_id}")
    print("==================================================")

    if args.dry_run:
        print("[*] Dry run mode enabled. Skipping actual upload.")
        sys.exit(0)

    try:
        from googleapiclient.discovery import build
        from googleapiclient.http import MediaFileUpload
    except ImportError:
        print("[!] Missing dependencies. Run: pip install google-api-python-client google-auth", file=sys.stderr)
        sys.exit(1)

    credentials = load_credentials(args.credentials)
    service = build("drive", "v3", credentials=credentials, cache_discovery=False)

    # Determine MIME type
    mime_type = "application/vnd.android.package-archive" if file_path.suffix.lower() == ".apk" else "application/octet-stream"

    # Check if file with same name already exists in target folder
    query = f"'{args.folder_id}' in parents and name = '{file_name}' and trashed = false"
    response = service.files().list(q=query, spaces='drive', fields='files(id, name)').execute()
    existing_files = response.get('files', [])

    media = MediaFileUpload(
        str(file_path),
        mimetype=mime_type,
        resumable=True,
        chunksize=5 * 1024 * 1024
    )

    if existing_files:
        existing_id = existing_files[0]['id']
        print(f"[*] Found existing file in folder ({existing_id}). Updating file content...")
        file_obj = service.files().update(
            fileId=existing_id,
            media_body=media,
            fields='id, name, webViewLink, webContentLink, size'
        ).execute()
    else:
        file_metadata = {
            'name': file_name,
            'parents': [args.folder_id]
        }
        print("[*] Uploading new file to Google Drive...")
        file_obj = service.files().create(
            body=file_metadata,
            media_body=media,
            fields='id, name, webViewLink, webContentLink, size'
        ).execute()

    file_id = file_obj.get("id")
    web_view_link = file_obj.get("webViewLink", f"https://drive.google.com/file/d/{file_id}/view")
    download_link = file_obj.get("webContentLink", f"https://drive.google.com/uc?id={file_id}&export=download")

    print("\n✅ Upload Complete!")
    print(f"🆔 Drive File ID: {file_id}")
    print(f"🔗 View Link:     {web_view_link}")
    print(f"📥 Direct Link:   {download_link}")

    # Set GitHub Actions output parameters if running in GitHub Actions
    github_output = os.environ.get("GITHUB_OUTPUT")
    if github_output and os.path.exists(github_output):
        with open(github_output, "a") as gh_out:
            gh_out.write(f"drive_file_id={file_id}\n")
            gh_out.write(f"drive_web_view_link={web_view_link}\n")
            gh_out.write(f"drive_download_link={download_link}\n")

    # Add to GitHub Step Summary
    github_summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if github_summary and os.path.exists(github_summary):
        with open(github_summary, "a") as gh_sum:
            gh_sum.write(f"### 🚀 APK Uploaded to Google Drive\n\n")
            gh_sum.write(f"- **File Name:** `{file_name}`\n")
            gh_sum.write(f"- **File Size:** {file_size_mb:.2f} MB\n")
            gh_sum.write(f"- **Google Drive Folder:** [View Folder](https://drive.google.com/drive/folders/{args.folder_id})\n")
            gh_sum.write(f"- **Direct File Link:** [Open in Drive]({web_view_link})\n")

if __name__ == "__main__":
    main()
