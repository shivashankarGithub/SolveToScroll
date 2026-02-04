# Privacy Policy for SolveToScroll

**Last updated: February 4, 2026**

## Introduction

SolveToScroll ("we", "our", or "the app") is committed to protecting your privacy. This Privacy Policy explains how we collect, use, and safeguard information when you use our Android application.

## Information We Collect

### Usage Statistics Access
SolveToScroll requires access to usage statistics to detect when you open apps that you've chosen to block. This permission is used **solely** to:
- Identify the currently active (foreground) application
- Determine if the active app is on your personal blocklist
- Display the challenge overlay when a blocked app is detected

**We do NOT:**
- Store your app usage history
- Transmit usage data to external servers
- Share usage information with third parties
- Access usage data for any apps other than those you explicitly block

### Display Over Other Apps
This permission allows us to show the challenge overlay on top of blocked applications. It is used **exclusively** for this purpose.

### Local Data Storage
SolveToScroll stores the following information **locally on your device only**:
- List of apps you've chosen to block
- Blocking schedules you've configured
- Challenge attempt counts (for difficulty adjustment)
- Your onboarding completion status

**All data is stored locally on your device and is never transmitted to any external servers.**

## Data Collection

**SolveToScroll does NOT:**
- Collect personal information
- Track your location
- Access your contacts, messages, or photos
- Use analytics or tracking services
- Contain advertisements
- Transmit any data to external servers
- Require account creation or login

## Data Sharing

We do not share any data with third parties because we do not collect any data. All app functionality operates entirely on your device.

## Data Security

All app data is stored locally using Android's secure storage mechanisms:
- SQLite database for app configurations
- DataStore for preferences
- Data is protected by your device's security features

## Data Retention

Data is retained only on your device for as long as you use the app. You can delete all app data at any time by:
- Clearing app data in Android Settings
- Uninstalling the application

## Children's Privacy

SolveToScroll does not collect personal information from anyone, including children under 13.

## Changes to This Privacy Policy

We may update this Privacy Policy from time to time. We will notify you of any changes by updating the "Last updated" date at the top of this policy.

## Your Rights

You have the right to:
- Access all data stored by the app (visible within the app)
- Delete all data by clearing app storage or uninstalling
- Revoke permissions at any time through Android Settings

## Open Source

SolveToScroll respects your privacy by design. The app:
- Works entirely offline
- Requires no internet permission
- Stores no data externally

## Contact Us

If you have any questions about this Privacy Policy, please contact us at:

**Email:** shiva.javascript@gmail.com

---

## Permissions Explained

### QUERY_ALL_PACKAGES
Allows the app to show you a list of installed apps so you can choose which ones to block.

### PACKAGE_USAGE_STATS
Allows the app to detect when you open a blocked app, so it can display the challenge.

### SYSTEM_ALERT_WINDOW
Allows the app to display the challenge screen over blocked apps.

### FOREGROUND_SERVICE / FOREGROUND_SERVICE_SPECIAL_USE
Allows the app to run continuously in the background to monitor for blocked apps.

### RECEIVE_BOOT_COMPLETED
Allows the app to restart automatically when your device boots up.

### POST_NOTIFICATIONS
Allows the app to show a notification while the blocking service is running.

### VIBRATE
Allows the app to provide haptic feedback during challenges.

### REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
Allows the app to request exemption from battery optimization to ensure reliable blocking.
