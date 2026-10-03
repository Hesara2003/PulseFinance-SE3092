#!/usr/bin/env python3
"""
PulseFinance Google Drive OAuth 2.0 Authentication Helper
Generates an OAuth 2.0 Refresh Token for personal Google Drive accounts
so GitHub Actions can upload APKs under your personal 15GB Drive quota.
"""

import os
import sys
import argparse
import subprocess

def main():
    parser = argparse.ArgumentParser(description="Generate Google Drive OAuth Refresh Token")
    parser.add_argument("--client-id", required=True, help="OAuth 2.0 Client ID")
    parser.add_argument("--client-secret", required=True, help="OAuth 2.0 Client Secret")
    parser.add_argument("--port", type=int, default=8085, help="Local redirect port")
    args = parser.parse_args()

    try:
        from google_auth_oauthlib.flow import InstalledAppFlow
    except ImportError:
        print("[*] Installing google-auth-oauthlib...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "google-auth-oauthlib"])
        from google_auth_oauthlib.flow import InstalledAppFlow

    scopes = [
        "https://www.googleapis.com/auth/drive",
        "https://www.googleapis.com/auth/drive.file"
    ]

    client_config = {
        "installed": {
            "client_id": args.client_id,
            "client_secret": args.client_secret,
            "auth_uri": "https://accounts.google.com/o/oauth2/auth",
            "token_uri": "https://oauth2.googleapis.com/token",
        }
    }

    flow = InstalledAppFlow.from_client_config(client_config, scopes=scopes)
    
    print("\n==================================================")
    print("🌐 GOOGLE DRIVE OAUTH AUTHORIZATION")
    print("==================================================")
    print("Starting local listener on port 8085 and opening browser...")
    sys.stdout.flush()

    # Start local server to receive the authorization code with automatic browser open and URL display
    credentials = flow.run_local_server(
        port=args.port,
        access_type="offline",
        prompt="consent",
        open_browser=True
    )

    refresh_token = credentials.refresh_token
    print("\n==================================================")
    print("✅ Authorization Successful!")
    print("==================================================")
    print(f"GDRIVE_CLIENT_ID:     {args.client_id}")
    print(f"GDRIVE_CLIENT_SECRET: {args.client_secret}")
    print(f"GDRIVE_REFRESH_TOKEN: {refresh_token}")
    print("==================================================")

    # Automatically set GitHub Secrets
    try:
        subprocess.run(["gh", "secret", "set", "GDRIVE_CLIENT_ID", "--body", args.client_id], check=True)
        subprocess.run(["gh", "secret", "set", "GDRIVE_CLIENT_SECRET", "--body", args.client_secret], check=True)
        subprocess.run(["gh", "secret", "set", "GDRIVE_REFRESH_TOKEN", "--body", refresh_token], check=True)
        print("\n🚀 All 3 secrets were automatically saved to your GitHub Repository Secrets!")
    except Exception as e:
        print(f"\n[!] Note: Could not auto-save secrets via gh CLI: {e}")

    # Now verify upload with the new OAuth token!
    apk_path = "app/build/outputs/apk/release/app-release-unsigned.apk"
    if os.path.exists(apk_path):
        print("\n[*] Testing upload to Google Drive using your personal OAuth token...")
        os.environ["GDRIVE_CLIENT_ID"] = args.client_id
        os.environ["GDRIVE_CLIENT_SECRET"] = args.client_secret
        os.environ["GDRIVE_REFRESH_TOKEN"] = refresh_token
        subprocess.run([
            sys.executable, "scripts/upload_to_gdrive.py",
            "--file", apk_path,
            "--name", "PulseFinance-v1.0.0.apk",
            "--folder-id", "1GRAAyZV_luvfbuwS06Pa01GojCZQkg7d"
        ])

if __name__ == "__main__":
    main()
