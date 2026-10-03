#!/usr/bin/env python3
"""
PulseFinance Google Drive OAuth 2.0 Authentication Helper
Generates an OAuth 2.0 Refresh Token for personal Google Drive accounts
so GitHub Actions can upload APKs under your personal 15GB Drive quota.
"""

import sys
import argparse
import subprocess

def main():
    parser = argparse.ArgumentParser(description="Generate Google Drive OAuth Refresh Token")
    parser.add_argument("--client-id", required=True, help="OAuth 2.0 Client ID from Google Cloud Console")
    parser.add_argument("--client-secret", required=True, help="OAuth 2.0 Client Secret from Google Cloud Console")
    args = parser.parse_args()

    try:
        from google_auth_oauthlib.flow import InstalledAppFlow
    except ImportError:
        print("[*] Installing google-auth-oauthlib...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "google-auth-oauthlib"])
        from google_auth_oauthlib.flow import InstalledAppFlow

    scopes = ["https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/drive"]

    client_config = {
        "installed": {
            "client_id": args.client_id,
            "client_secret": args.client_secret,
            "auth_uri": "https://accounts.google.com/o/oauth2/auth",
            "token_uri": "https://oauth2.googleapis.com/token",
        }
    }

    print("\n🌐 Opening browser for Google Drive authorization...")
    flow = InstalledAppFlow.from_client_config(client_config, scopes=scopes)
    credentials = flow.run_local_server(port=0)

    print("\n==================================================")
    print("✅ Authorization Successful!")
    print("==================================================")
    print(f"GDRIVE_CLIENT_ID:     {args.client_id}")
    print(f"GDRIVE_CLIENT_SECRET: {args.client_secret}")
    print(f"GDRIVE_REFRESH_TOKEN: {credentials.refresh_token}")
    print("==================================================")

    # Ask if user wants to save directly to GitHub Secrets using gh CLI
    try:
        subprocess.run(["gh", "secret", "set", "GDRIVE_CLIENT_ID", "--body", args.client_id], check=True)
        subprocess.run(["gh", "secret", "set", "GDRIVE_CLIENT_SECRET", "--body", args.client_secret], check=True)
        subprocess.run(["gh", "secret", "set", "GDRIVE_REFRESH_TOKEN", "--body", credentials.refresh_token], check=True)
        print("\n🚀 All 3 secrets were automatically saved to your GitHub Repository Secrets!")
    except Exception as e:
        print(f"\nCould not auto-save secrets via gh CLI: {e}")
        print("Please add the secrets manually in GitHub Repository Settings -> Secrets and variables -> Actions.")

if __name__ == "__main__":
    main()
