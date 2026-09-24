# Privacy Policy

**Last updated: September 24, 2026**

This privacy policy describes how Pedalons ("we", "our", "us") collects, uses, and protects your personal data when you use our platform (website, mobile app, GPS device extensions).

For any questions about your personal data, you can contact us at: **privacy@pedalons.fr**

---

## 1. Data We Collect

### Account Data

When you create an account, we collect:

- **Email address**: for authentication and service-related communications
- **Display name**: chosen by you, visible to your team members
- **Profile picture** (optional): image you upload to personalize your profile. In the mobile app, you pick it from your photo library through the system picker: the app receives only the chosen photo, and has no access to the camera or to the rest of your library.
- **Preferences**: unit system (metric/imperial), language
- **Acceptance of the terms of service**: the date on which you accepted them at sign-up, kept as proof of that acceptance

### Authentication Data

To secure access to your account, we process:

- **Passkeys (WebAuthn)**: credential ID, public key, and signature counter. The private key stays on your device and is never transmitted to us.
- **Session tokens**: a refresh token (hashed, never stored in plain text) is kept in a secure HttpOnly cookie for up to 30 days.
- **One-time passwords (OTP)**: hashed server-side, valid for 5 minutes.
- **GPS device pairing codes**: temporary codes (10 minutes) to connect Karoo or Garmin devices.

### Session Data

Each time you sign in, we record:

- **IP address** and **user agent** (browser/device type): for account security and suspicious activity detection.
- **Last login date** and **last session usage time**.

### Location and GPS Data

When you create or view routes:

- **GPS tracks**: geographic coordinates (latitude, longitude, altitude) from GPX files you import.
- **Waypoints**: names and coordinates of places you add.
- **Team location** (optional): geographic point representing your team's location.

### Approximate Location (mobile app, optional)

If you turn on the "around me" filter in the mobile app, it reads your phone's **approximate** location (to within a few hundred metres, never the precise location) and sends it as a search criterion to sort routes by distance. This location:

- is read only at that moment, while the app is open, and with your permission — never in the background;
- is neither saved to your account nor kept in our database;
- like the address of any request, may appear in our web server's technical logs.

**Important**: we do not track your real-time location and do not record your rides. GPS tracks come exclusively from files you voluntarily import.

### Content You Create

- **Rides**: title, description, date, pace groups, associated route.
- **Posts**: text in Markdown format.
- **Comments**: text attached to a publication.
- **Routes**: name, distance, elevation gain, surface type, GPS tracks and waypoints.
- **Photos and images**: files you upload to illustrate your content.

### Notifications

To let you know what is happening in your teams (a new ride, a comment, a change to a ride you signed up for…), we process:

- **Your notifications**: the type of event, the item it concerns, the text displayed, and when you read it. They appear on the Notifications page of the website and the app.
- **Your notification preferences**: for each type of notification, the channels through which you agree to be notified (email, notification on your phone).
- **Delivery records**: for each notification sent by email or to your phone, the channel, the delivery status and its date.
- **Your phone's registration** (mobile app only, and only if you allow notifications): a registration token issued by Firebase Cloud Messaging (a Google service), the operating system (Android or iOS), the device model and the app version. The token identifies the app installation, not you as a person; we use it solely to address notifications to your phone.

The app only asks for permission to show notifications when you choose to, never at launch. You can withdraw that permission at any time in your phone's settings, or turn off a type of notification in your notification preferences. The token is deleted from our servers when you sign out of the app, when Google tells us it is no longer valid (for example, the app was uninstalled), and when you delete your account.

### Reports and Blocks

To make moderation possible (see section 6 of the terms of service), we record:

- **Your reports**: who reported (you), what is reported (the content or the member, and the team concerned), the author of the content or the member concerned, the reason you chose, the optional message you add, a **copy of the reported text** (at most 1,000 characters), and the decision taken (content removed or report dismissed, by whom and when). The copy lets the decision be checked even if the content has since been edited or deleted.
- **Your blocks**: who blocked whom, and since when.

Who can see them:

- **Your identity as a reporter** is visible **to the Pedalons team only**. It is never shown to the team's organizers, to the author of the content, or to the reported member, and the notification sent to moderators contains none of it.
- **The team's organizers and administrators** see the reported content, its author, the reasons, and the messages added, but not who reported it. A message can identify you by what it says: write it knowing they will read it. An organizer who is the subject of a report does not see it.
- **Your blocks** are visible to you only. The blocked person is not told, and nothing in the service reveals it to them.

The notification that alerts moderators to a report contains only the team's name, never the reported content.

**Publication filter**: when you publish, your text is compared with a short list of abusive or hateful terms. If it contains one, publication is refused and the text is not stored; the refusal has no other consequence for your account.

### Third-Party GPS Service Connections

If you connect an external GPS service (Hammerhead, Garmin, Wahoo):

- **OAuth access tokens**: encrypted with AES-256-GCM before storage. We never store your credentials (username/password) for these services.
- **External user ID**: provided by the third-party service to link with your Pedalons account.

### Data Stored Locally on Your Device

In your web browser or mobile app:

- **Language preference**: in local storage (localStorage)
- **Unit system**: in local storage
- **Map preferences**: chosen map style, in local storage
- **Session cookie**: an HttpOnly cookie containing your refresh token (not accessible by JavaScript)

---

## 2. How We Collect Your Data

- **Directly from you**: when you create an account, fill in your profile, import GPX files, create content, or connect a GPS service.
- **Automatically**: IP address and user agent during sign-in; session cookie to maintain your authentication.
- **We do not collect data from third parties**: no data purchases, no advertising tracking, no collection via social networks.

---

## 3. Why We Use Your Data

| Purpose | Legal Basis (GDPR) |
|---------|-------------------|
| Provide the service (account, authentication, navigation) | Performance of contract |
| Display your team's routes and rides | Performance of contract |
| Send verification emails and sign-in codes | Performance of contract |
| Notify you on the website and in the app of your teams' activity | Performance of contract |
| Notify you by email, according to your notification preferences | Performance of contract (adjustable at any time) |
| Notify you on your phone (push notifications) | Consent (permission granted on the phone) |
| Moderate content: handle reports, apply your blocks, filter abusive terms at publication | Performance of contract (terms of service) and legitimate interest (protecting members) |
| Keep proof that you accepted the terms of service | Legitimate interest |
| Secure your account (suspicious session detection) | Legitimate interest |
| Sync your routes with connected GPS devices | Consent (voluntary connection) |
| Sort routes by distance (the app's "around me" filter) | Consent (location permission granted on the phone) |
| Display maps | Legitimate interest |
| Improve the service (aggregate usage analysis) | Legitimate interest |

We **never** use your data for:
- Targeted advertising
- Resale to third parties
- Automated profiling or automated decision-making

---

## 4. Data Sharing

### Visibility Within the Platform

- **Team content**: visible only to your team members ("team" visibility).
- **Public content**: if you or your team choose "public" visibility, the content is accessible to all platform users.
- **Your display name and profile picture** are visible to members of your teams.

### Technical Service Providers

We use technical services to operate the platform:

| Service | Role | Data Involved |
|---------|------|--------------|
| OVHcloud (OVH SAS, France) | Application, database, and object storage hosting | All data |
| Scaleway (Scaleway SAS, France) | Delivery of transactional emails and email notifications (Transactional Email) | Email address, display name, email content (including the title and text of notifications) |
| Google Firebase Cloud Messaging (Google Ireland Limited, Ireland) | Routing push notifications to the mobile app, through Apple Push Notification service for iPhones | Phone registration token, title and text of each notification, technical identifier used to open the right screen when tapped |

**All our image processing (imgproxy) and route calculation (Valhalla) services are self-hosted** and do not transmit any data to third parties. Fonts are bundled with the website and the app: none is loaded from a third-party service.

### Basemaps

Maps are drawn by your browser or phone, which downloads map images directly from the provider of the chosen basemap. As with any web page, that provider then receives your **IP address** and the **map area displayed**; it receives neither your account, your name, nor your routes. These providers act as independent controllers, under their own policies.

| Basemap | Provider | When |
|---------|----------|------|
| Plan (default) | VersaTiles (tiles.versatiles.org) | Default basemap |
| Relief (hillshading) | Mapterhorn (tiles.mapterhorn.com) | When relief shading is on |
| IGN, Satellite (IGN), IGN SCAN 25 | French National Institute of Geographic and Forest Information — Géoplateforme (France) | If you choose this basemap |
| Satellite (ESRI) | Esri Inc. (United States) | If you choose this basemap |
| OpenStreetMap | OpenStreetMap Foundation (United Kingdom) | If you choose this basemap |
| CyclOSM | OpenStreetMap France | If you choose this basemap |
| Michelin | Served by us (tiles.pedalons.fr) | If you choose this basemap |

### We Do Not Sell Your Data

We do not sell, rent, or share your personal data for commercial or advertising purposes.

### Authorities

We may be required to disclose your data if required by law (judicial request, legal obligation).

---

## 5. International Data Transfers

Our servers are hosted by **OVHcloud** (OVH SAS, Roubaix, France) and are located in France. Your data remains within the European Union.

If you allow push notifications in the mobile app, their content passes through **Firebase Cloud Messaging**, provided by Google Ireland Limited. Google may process this data in the United States; this transfer is covered by the European Commission's standard contractual clauses and by Google LLC's certification under the EU–US Data Privacy Framework. On iPhone, notifications are delivered by Apple's Push Notification service, as for any iOS app.

The "Satellite (ESRI)" basemap is served from the United States: your IP address and the area displayed are sent there only if you choose it. The OpenStreetMap basemap is served from the United Kingdom, which benefits from a European Commission adequacy decision.

Connecting to third-party GPS services (Hammerhead, Garmin, Wahoo) involves a data transfer to these services, located in the United States. This transfer is based on your explicit consent when connecting the service.

---

## 6. Data Retention

| Data Type | Retention Period |
|-----------|-----------------|
| Account data | As long as your account is active |
| Login sessions | 30 days after last use |
| Temporary authentication tokens (OTP) | 5 minutes |
| Device pairing codes | 10 minutes |
| WebAuthn challenges | 5 minutes |
| Content (rides, posts, routes) | Until you delete it |
| Notifications and their delivery records | 90 days, then deleted automatically |
| Notification preferences | As long as your account is active |
| Phone registration for push notifications | Until you sign out of the app, uninstall it, or delete your account |
| Files (images, GPX) | As long as the associated content exists |
| Date of acceptance of the terms of service | As long as your account is active |
| Blocks | Until you unblock the person, or until either of your accounts is deleted |
| Reports, copy of the reported text, and decision | As long as the account of the person concerned exists, to keep a record of moderation decisions; when the reporter's account is deleted, kept with no link to them (see section 7) |
| Data after account deletion | Erased immediately; gone from backups within 30 days (see section 7) |

---

## 7. Your Rights

Under the General Data Protection Regulation (GDPR), you have the following rights:

- **Right of access**: obtain a copy of your personal data.
- **Right to rectification**: correct inaccurate or incomplete data.
- **Right to erasure** ("right to be forgotten"): request deletion of your data.
- **Right to restriction of processing**: temporarily restrict the use of your data.
- **Right to data portability**: receive your data in a structured, machine-readable format.
- **Right to object**: object to processing based on legitimate interest.
- **Right to withdraw consent**: at any time, without affecting the lawfulness of prior processing.

### Export your data yourself

You can exercise the rights of access and portability yourself, without writing to us: on the website, under **Profile → Your data**, choose "Download my data". We prepare a ZIP archive and email you a download link. This feature is not available in the mobile app yet; the emailed link, however, works on any of your devices.

The archive contains your profile, your teams, your sign-ups, everything you have published, your notifications, their delivery records, your notification preferences, the phones registered for push notifications, the members you blocked and the reports you made (without the copy of the reported text, which is someone else's content), and your files (profile picture, uploaded images, and the GPX and FIT files of your routes). The data is in JSON, a structured and machine-readable format.

For security reasons, credential material is excluded: your password hash, session tokens, the cryptographic material of your passkeys, your calendar token, the access tokens of your connected GPS services, and the registration tokens of your phones for push notifications. Their metadata (dates, devices, services involved) is included. The download link expires after **7 days**, after which the archive is deleted from our servers. One export per hour per account.

### Delete your account

You can delete your account yourself, at any time, without writing to us:

- **in the mobile app**: **Profile → Account → Danger zone**, then "Delete the account";
- **on the website**: **Profile → Account Actions → Danger Zone**, then "Delete Account".

If you no longer have access to your account or to the app, write to **privacy@pedalons.fr** from your account's email address asking for its deletion; we will process it within 30 days.

Deletion is irreversible and immediate. As soon as you confirm, your account is deactivated and your personal data is erased: email address, name, profile picture, password and passkeys, sessions, preferences, connected GPS services, team memberships, registrations for upcoming rides and trips, classified ads and their photos, comments, notifications, phone registrations for push notifications, data exports and GPX previews. Blocks are deleted both ways, those you made and those aimed at you, and reports about you are deleted along with the copy of your content they held. Reports you made are kept, with no remaining link to you, so that the decisions taken can still be checked.

What you published for a team (rides, trips, routes, posts and their files) belongs to that team and stays online. That content is from then on credited to "Ancien membre" (French for "former member") and is no longer linked to any data that could identify you. Likewise, a comment other members replied to is kept empty, marked "Comment deleted", so that their replies do not disappear with it. Your registrations for past rides are kept in the same anonymous form and are no longer displayed.

If you want content published for a team to disappear, delete it before deleting your account, or ask one of the team's organizers.

Our backups, kept for 30 days, still contain your data until they are renewed; they are only used to restore the service after an incident.

### Contact us

For the other rights, or if you would rather go through us, contact us at: **privacy@pedalons.fr**

We will respond to your request within **30 days**. If we cannot comply, we will explain why.

You may also lodge a complaint with the **CNIL** (French Data Protection Authority): [www.cnil.fr](https://www.cnil.fr)

---

## 8. Cookies and Local Storage

Pedalons uses a minimal number of cookies and local storage items:

| Item | Type | Purpose | Duration |
|------|------|---------|----------|
| refresh_token | HttpOnly cookie | Maintain your authenticated session | 30 days |
| i18nextLng | localStorage | Remember your language preference | Persistent |
| Unit preferences | localStorage | Remember your unit system | Persistent |
| Map style | localStorage | Remember your map display preferences | Persistent |

**We do not use any tracking, analytics, or advertising cookies.** No cookie consent is therefore required beyond the session cookie, which is strictly necessary for the service to function.

---

## 9. Security

We implement the following measures to protect your data:

- **Encryption in transit**: all communications use HTTPS (TLS).
- **Encryption at rest**: GPS service OAuth tokens are encrypted with AES-256-GCM.
- **Secret hashing**: session and authentication tokens are stored as irreversible hashes.
- **Secure cookies**: HttpOnly, Secure, SameSite=Strict.
- **Multi-tenant isolation**: each domain's data is strictly isolated at the database level.
- **Rate limiting**: protection against brute-force login attempts.
- **Actual deletion**: what you delete, your account included, is erased from our database, not merely hidden; it survives only in our backups, for 30 days at most.

No system is infallible. If you notice suspicious activity on your account, contact us immediately.

---

## 10. Children's Privacy

Pedalons is not intended for children under 16. We do not knowingly collect personal data from minors under 16. If you are a parent and believe your child has provided us with data, contact us so we can delete it.

---

## 11. Changes to This Policy

We may update this policy to reflect changes in our practices or in regulations. In case of a substantial change:

- We will publish the updated version on this page.
- We will update the "last updated" date at the top of this document.
- For significant changes, we will notify you by email or in-app notification.

---

## 12. Data Controller

The data controller for your personal data is:

- **LANDAIS Gabriel** (sole proprietorship)
- **Address**: 29 rue Docteur Jean Rostand, 44800 Saint-Herblain, France
- **SIRET**: 897 872 958 00011

### Data Protection Officer (DPO)

The Data Protection Officer is **Gabriel Landais**. You can contact them at: **privacy@pedalons.fr**

## 13. Contact

For any questions about this policy or your personal data:

- **Email**: privacy@pedalons.fr
- **Response time**: 30 days maximum

---
