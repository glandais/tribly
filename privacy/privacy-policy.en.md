# Privacy Policy

**Last updated: April 2, 2026**

This privacy policy describes how Pedalons ("we", "our", "us") collects, uses, and protects your personal data when you use our platform (website, mobile app, GPS device extensions).

For any questions about your personal data, you can contact us at: **privacy@pedalons.fr**

---

## 1. Data We Collect

### Account Data

When you create an account, we collect:

- **Email address**: for authentication and service-related communications
- **Display name**: chosen by you, visible to your team members
- **Profile picture** (optional): image you upload to personalize your profile
- **Preferences**: unit system (metric/imperial), language

### Authentication Data

To secure access to your account, we process:

- **Passkeys (WebAuthn)**: credential ID, public key, and signature counter. The private key stays on your device and is never transmitted to us.
- **Session tokens**: a refresh token (hashed, never stored in plain text) is kept in a secure HttpOnly cookie for up to 30 days.
- **Magic links and one-time passwords (OTP)**: hashed server-side, valid for 5 minutes.
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

**Important**: we do not track your real-time location. GPS data comes exclusively from files you voluntarily import.

### Content You Create

- **Rides**: title, description, date, pace groups, associated route.
- **Posts**: text in Markdown format.
- **Comments**: text attached to a publication.
- **Routes**: name, distance, elevation gain, surface type, GPS tracks and waypoints.
- **Photos and images**: files you upload to illustrate your content.

### Third-Party GPS Service Connections

If you connect an external GPS service (Hammerhead, Garmin):

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
| Secure your account (suspicious session detection) | Legitimate interest |
| Sync your routes with connected GPS devices | Consent (voluntary connection) |
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
| Brevo (Sendinblue SAS, France) | Transactional email delivery | Email address |

**All our image processing (imgproxy) and route calculation (Valhalla) services are self-hosted** and do not transmit any data to third parties.

### We Do Not Sell Your Data

We do not sell, rent, or share your personal data for commercial or advertising purposes.

### Authorities

We may be required to disclose your data if required by law (judicial request, legal obligation).

---

## 5. International Data Transfers

Our servers are hosted by **OVHcloud** (OVH SAS, Roubaix, France) and are located in France. Your data remains within the European Union.

Connecting to third-party GPS services (Hammerhead, Garmin) involves a data transfer to these services, located in the United States. This transfer is based on your explicit consent when connecting the service.

---

## 6. Data Retention

| Data Type | Retention Period |
|-----------|-----------------|
| Account data | As long as your account is active |
| Login sessions | 30 days after last use |
| Temporary authentication tokens (OTP, magic links) | 5 minutes |
| Device pairing codes | 10 minutes |
| WebAuthn challenges | 5 minutes |
| Content (rides, posts, routes) | Until you delete it |
| Files (images, GPX) | As long as the associated content exists |
| Data after account deletion | Immediate soft deletion, permanent deletion within 30 days |

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

To exercise your rights, contact us at: **privacy@pedalons.fr**

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
- **Soft deletion**: deleted data is first deactivated before permanent deletion.

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
