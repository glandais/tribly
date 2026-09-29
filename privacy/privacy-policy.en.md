# Privacy Policy

**Last updated: September 29, 2026**

This privacy policy describes how Pedalons ("we", "our", "us") collects, uses, and protects your personal data when you use our platform (website, mobile app, GPS device extensions).

For any questions about your personal data, you can contact us at: **privacy@pedalons.fr**

---

## 1. Data We Collect

### Account Data

When you create an account, we collect:

- **Email address**: for authentication and service-related communications
- **Display name**: chosen by you, visible to your team members
- **Profile picture** (optional): image you upload to personalize your profile. In the mobile app, you pick it from your photo library through the system picker: the app receives only the chosen photo, and has no access to the camera or to the rest of your library.
- **Preferences**: unit system (metric/imperial), display theme (light, dark or system), language, time zone, whether other members may contact you about your classified ads, and your notification settings. Unit system, theme, language and time zone are stored in your account only once you choose them (unit system, theme and language on the website or in the app, the time zone on the website); a saved preference then follows you across devices, and until you choose one, your browser's or device's setting applies. Contact about your ads is allowed unless you turn it off in your profile. Our emails are sent in French or English: sign-in codes and email-verification or password-reset emails are written in the language of the website or app you requested them from; emails triggered by someone else (notifications, messages about your classified ads, team invitations) are written in the language saved in your account — if you have not chosen one, notifications are in French and the other two in the sender's language. Dates and times in notifications are shown in your saved time zone, or in Paris time if you have not chosen one.
- **Acceptance of the terms of service**: the date on which you accepted them at sign-up, kept as proof of that acceptance
- **Account status data**: whether your email address has been verified and the date of that verification, the date your account was created and last modified, the site (domain) your account belongs to, and — for the small number of accounts that administer the platform — an administrator role flag.
- **Team memberships**: the teams you belong to, your role in each team (member, organiser, administrator) and the date you joined. Team administrators can see their team's member list (display name, profile picture, role, join date) and can search it by email address. Organisers can see members' display names and pictures, so that they can choose a group leader. Only if a team's administrators turn on the member directory (it is off by default) can every member of the team see the list, with each member's role and join date. The member list never shows email addresses to other members.

### Teams Coming from Biketeam

Some teams move to Pedalons from the Biketeam platform.

A Biketeam team administrator moves their team themselves: they start the transfer from Biketeam, then sign in (or sign up) on Pedalons and confirm it. Our server then fetches the team's content directly from Biketeam's server: the team's name and settings, its about page (including the contact details it shows), its FAQ page and logo, places, routes with their GPX files, ride templates, publications, rides, trips and their images. **No member data comes over**: no accounts, memberships, registrations or comments. The Pedalons account that confirmed the transfer becomes the team's administrator and is shown as the author of everything imported; members then join through the usual invitation link. For each transfer request we record the account that confirmed it, the Biketeam team concerned, the dates and the outcome.

### Authentication Data

To secure access to your account, we process:

- **Password** (if you choose to set one): we never store your password itself. It is stored only as an irreversible bcrypt hash, which we use to verify your sign-in and which is replaced whenever you reset your password. We never send it to you by email.
- **Passkeys (WebAuthn)**: credential ID, public key, signature counter, the transport types your authenticator supports (USB, NFC, Bluetooth, internal), an authenticator model identifier (AAGUID) that indicates the type of security key or platform authenticator used, a device label you can choose when registering the passkey, and the dates the passkey was created and last used. The private key stays on your device and is never transmitted to us.
- **Session tokens**: a refresh token (hashed, never stored in plain text) is kept in a secure HttpOnly cookie (website) or in your device's secure storage (mobile app) for up to 30 days. Sessions created for a connected GPS head unit (Karoo, Garmin) last up to 90 days, because those devices are not connected continuously.
- **One-time passwords (OTP)**: hashed server-side, valid for 5 minutes, and invalidated after 5 wrong attempts.
- **Email verification links**: when you register, we store your email address, the display name you chose, the bcrypt hash of your password and the time you accepted the terms of service, together with a hashed, single-use verification token valid for 24 hours. Your account is only created once you click the link.
- **Password reset links**: a hashed, single-use token valid for 1 hour, linked to your email address.
- **Email change verification**: when you change the email address on your account, we store the new address together with a hashed, single-use verification token valid for 24 hours; the address is only applied to your account once you follow the link.
- **GPS device pairing codes**: temporary codes (10 minutes) to connect Karoo or Garmin devices.
- **Calendar token**: if you use the calendar subscription feature, we generate a random token for your account. It is embedded in the feed address, does not expire, and is what identifies you when a calendar application fetches your feed. Because your calendar application must be able to send it on every request, this token is stored in readable form on our servers; anyone holding the feed address can read your rides calendar. You can regenerate it at any time from your calendar settings, which immediately invalidates the previous address.

### Session Data

Each time you sign in, we record:

- **IP address** and **user agent** (browser/device type): for account security and suspicious activity detection.
- **Last login date** and **last session usage time**.
- **Server logs**: our servers keep technical logs of requests and authentication events (date and time, IP address, browser/device type, requested address and response code). They may contain your IP address, your email address and technical identifiers, and are used only for security, abuse detection and troubleshooting — never for profiling or advertising. The Garmin app, and the mobile app when you use "Around me", pass your coordinates in the requested address (see below): they are removed from the address before the access log is written, as are the tokens and codes passed as parameters of an address. Access logs are kept for 14 days (see section 6).

### Location and GPS Data

When you create or view routes:

- **GPS tracks**: geographic coordinates (latitude, longitude, altitude) taken either from GPX files you import or from routes you draw yourself with our route planner (where your site or team has turned it on; it is off by default), in which case the points you place on the map are sent to our self-hosted routing engine and stored as a track.
- **GPX files you import**: on import we keep only the track (latitude, longitude, altitude) and its waypoints (position and name). The timestamps and sensor measurements that a GPS device or activity-tracking app records with a route (heart rate, cadence, power, temperature), and the file's metadata (author, email address, device), are removed before anything is stored: they are in none of the files we keep, nor in the GPX and FIT files offered for download or sent to your GPS services. Files imported before this change are cleaned the same way, automatically (for backups, see "Photos and images"). A GPX or FIT file attached to a post as a plain attachment is not affected: it is kept as you upload it. You can delete an imported file at any time by deleting the route or the analysed file it belongs to.
- **Waypoints**: names and coordinates of places you add.
- **Team location** (optional): geographic point representing your team's location.
- **Ad and meeting-place locations**: the postal address or map point you attach to a classified ad or to a team meeting place. The map point of an ad is shown to other members only as an approximate area (see "Classified ads" below). For the free-text location of an ad and for meeting places, choose a nearby landmark rather than your home address if you do not want your address visible to your team.

We do not continuously track or record your movements, and we never store a location history. Two exceptions apply:

- **Garmin Edge app**: if you install it, the app reads your device's current GPS position each time its main screen is shown, when it loads your routes, and sends it to our server for the sole purpose of sorting your team's routes by distance from you. This requires the Garmin "Positioning" permission, which you grant when installing the app.
- **iOS/Android app**: the app can ask for your approximate location, but only if you tap "Around me" in the routes list. It then reads your position once, at low accuracy: on Android it asks only for the approximate-location permission, on iOS only for access while you use the app, never in the background. While that filter is on, the position is sent with the route-list requests, to filter and sort your team's routes by distance from you. The app does not save it on your phone.

In both cases these coordinates are used only for that sorting and filtering and are not stored in your account or in our database; they are also removed from our access logs (see "Session Data"). All other GPS data comes from routes you import as a GPX file or draw yourself in the route planner. Our website and our Hammerhead Karoo extension do not request or use your device location.

### GPX Analysis Tool

Our GPX tool lets you analyse a GPX file, or a route you draw, without attaching it to a team. Using it requires an account. For each file analysed we store: the GPX file you uploaded (without timestamps or sensor measurements, see "GPX files you import"), a simplified version of it, a FIT export, a rendered map image of the track, the computed statistics (distance, elevation gain/loss, hilliness), the name of the track, and a link to your user account (so we can show you your own history and let you delete or edit your files). These files are kept for **30 days** and then deleted automatically, together with the stored files; you can delete them earlier at any time. A list of the files you analysed in the last 30 days is visible to you on the "My files" page.

**Anyone who has the link can see the file.** Each analysed file gets a long random address that is impossible to guess. Anyone holding that address — including people who are not signed in and are not members of your teams — can view the track on a map, see its statistics and download it as a GPX or FIT file. Only you can modify or delete it. Treat the link as the secret: share it only with people you want to give the track to, and delete the file if a link has been shared more widely than you intended.

### Content You Create

- **Rides**: title, description, date and time, pace groups, associated route, meeting and finish places, publication status (draft, published, cancelled) and scheduled publication date.
- **Trips**: multi-day trips and their individual stages (title, description, dates, routes, start and finish places).
- **Sign-ups (participations)**: when you register for a ride or a trip, we record which ride or trip you registered for, the pace group you chose, and the date and time of your registration. Your display name and profile picture then appear in that ride's or trip's participant list, which is visible to everyone who can see the ride or trip — including visitors who are not logged in, when the ride or trip is public. Cancelling your registration deletes the record.
- **Posts**: title and text in Markdown format.
- **Comments**: text attached to a post, ride, trip or route.
- **Classified ads**: title, description, photos, type (sale, purchase, rental), price, rental period, and the location you enter (free text and/or a point on the map). Ads are only visible to signed-in members of your team, never publicly. We store the map point you enter exactly, but only you, your team's administrators and the platform administrators see it exactly; other members see only an area about 1 km across. Location text you type is shown as you typed it. Photos you attach are stripped of their GPS position before they are stored (see "Photos and images"), so they carry no position more precise than that area.
- **Messages about a classified ad**: when you use "Contact the seller", we email your message (up to 2,000 characters) to the ad's author, along with your display name, the ad's title and a link to it. Your email address is set as the reply address, so the author sees it and can reply to you directly; the author's address is never shown to you. We do not keep the text of your message in our database: it passes only through our email provider. We record only that you wrote about that ad, and when, to limit abuse (at most 10 messages per hour). You can turn off messages about your own ads in your profile.
- **Team pages**: the "about" page and any additional pages your team publishes.
- **Ride templates**: reusable ride models (title, description, pace groups) saved by your team's organisers.
- **Places**: names, postal addresses, links and coordinates of meeting and finish points you save for your team.
- **Routes**: name, distance, elevation gain and loss, hilliness, surface type, GPS tracks and waypoints. For each route and each file analysed with the GPX tool we also generate and store two derived files — a simplified GPX and a FIT file — so that you can send the route to a GPS device.
- **Photos and images**: the files you upload, together with their original file name, format and dimensions. Before storing a JPEG, PNG, WebP or GIF image (a ride or classified-ad photo, an avatar, a logo, an image or an attachment), we remove all the metadata added by your camera, phone or software: EXIF data (including the GPS position where the photo was taken, the date and the device model), XMP, IPTC, comments and embedded thumbnails. Only the image's orientation is kept, so that it displays the right way up; the image itself is not recompressed. The stored file, the one downloaded by people allowed to see the content it illustrates, and the one in your data export therefore do not reveal where the photo was taken. Formats we cannot remove this metadata from (HEIC/HEIF, AVIF, TIFF, RAW files, JPEG XL, JPEG 2000) are refused: convert them to JPEG before uploading. Photos already stored before this change are cleaned the same way, automatically; our earlier backups may still contain the originals until they expire (30 days, see section 6). Other attached files — videos, PDFs, office documents, SVG or ICO images, GPX or FIT files attached as plain attachments — are kept as you upload them, with whatever metadata they contain (a video, for example, can carry the place where it was filmed).

Every item above is stored together with the identity of the account that created it and the creation and last-modification dates.

### Team Invitations

A team administrator can invite someone by email address, whether or not that person already has an account. We store the invited address, the team, the role offered, the administrator who sent the invitation, its status (pending, accepted, revoked, expired), the relevant dates and who accepted or revoked it. The link in the email is stored only as an irreversible hash. Each invitation triggers one email showing the inviting administrator's display name and the team name; sending the invitation again replaces the previous one, and an address that receives too many invitations in one day gets no further emails that day. The email is written in the invitee's chosen language when they have an account, otherwise in the administrator's language. Nobody joins a team without accepting; creating an account does not accept an invitation. The team's administrators can see the invitations they sent, with each address and its status. An invitation can be accepted for 14 days. Once it has been accepted, revoked or has expired, the record — including the invited address, even if that person never created an account — is kept for 365 days so the team can see who brought each member in, and is then deleted automatically.

### Notifications

When something happens in your teams, we create a notification for you: a ride, trip or post is published; a ride or trip you signed up for is cancelled, changes date or meeting point, or loses the group you had joined; a ride you signed up for starts within a day; someone joins a ride you created or lead a group of; someone comments on something you published or replies to one of your comments; you are invited to a team; or, if you moderate a team, something in it is reported. For each one we store its type, the date it was created, and whether and when you read it. The notification also keeps a copy of what it is about, taken when it was created: the name of the member who triggered it, the team's name, the title and date of the ride, trip or post, and, for comments and replies, an extract of up to 280 characters of the comment.

Every notification appears in your inbox on the website and in the app. Some types can also be sent to you by email, or as a push notification on your phone or in your browser. Each type has default settings, which you can change per type and per channel in your notification settings; we store only the settings you change. You can also choose to receive your non-urgent emails as a single daily digest, sent at 7 a.m. in your time zone, and you can mute a team: its announcements (new rides, trips and posts) then no longer reach you, not even in your inbox, while notifications that concern you personally still do. We store these choices too. For each notification sent by email or as a push notification, we record the channel, the delivery status and its date.

If you allow notifications, we store, for each phone or browser: a push token issued by Google Firebase Cloud Messaging that identifies the app installation or the browser, not you as a person; the platform (Android, iOS or web); the device name or model (on Android, manufacturer and model; on iOS, the device name as reported by the system; on the website, the names of the browser and of the system, for example "Firefox · Android"); the app version; and the dates it was registered and last seen. The app and the website only ask for permission to show notifications when you choose to, never at launch. You can withdraw that permission at any time in your phone's or browser's settings, turn off notifications on the website, or turn off a type of notification in your notification settings.

Push notifications show their title and text on your phone or computer, and, depending on your settings, on the lock screen. For comments and replies, this text includes the name of the member who wrote and an extract of what they wrote. If you do not want this visible, change your device's lock-screen notification settings, or turn off push notifications for these types in your notification settings.

**Team chat channels**: a team's administrators can connect the team to a chat channel (Slack, Discord, Mattermost) or to any HTTPS address of their choice. The team's announcements — a ride, trip or post published, and a ride or trip cancelled or changed — are then also posted there, with the name of the member who triggered them, the team's name, the title and date of the item, what changed, and a link to it. Everyone with access to that channel can read these messages, which are then kept by the chosen service under its own rules. For each message we record whether it was delivered, and when.

### Reports and Blocks

To make moderation possible (see section 6 of the terms of service), we record:

- **Your reports**: who reported (you), what is reported (the content or the member, and the team concerned), the author of the content or the member concerned, the reason you chose, the optional message you add, a **copy of the reported text** (at most 1,000 characters), and the decision taken (content removed or report dismissed, by whom and when). The copy lets the decision be checked even if the content has since been edited or deleted.
- **Your blocks**: who blocked whom, and since when.

Who can see them:

- **Your identity as a reporter** is visible **to the Pedalons team only**. It is never shown to the team's organizers, to the author of the content, or to the reported member, and the notification sent to moderators contains none of it.
- **The team's organizers and administrators** see the reported content, its author, the reasons, and the messages added, but not who reported it. A message can identify you by what it says: write it knowing they will read it. An organizer who is the subject of a report does not see it.
- **Your blocks** are visible to you only. The blocked person is not told, and nothing in the service reveals it to them.

Reported content can be hidden from members until a moderator has decided. The notification that alerts moderators to a report contains only the team's name, never the reported content.

**Publication filter**: when you publish, your text is compared with a short list of abusive or hateful terms. If it contains one, publication is refused and the text is not stored; the refusal has no other consequence for your account.

### Problem Reports and Error Reports

To fix bugs, we receive:

- **Your problem reports or suggestions** ("Report a problem", on the website or in the app): the text you write, the platform and the app version, and, if you leave the "Attach technical information" box checked, the page or screen displayed, the team you are viewing, the device's system and model or the browser, the language, the time zone, and a log of your last actions in the app (pages visited, failed requests, errors). The form shows you exactly what will be sent.
- **Automatic error reports** (signed in only): when the app hits an unexpected error, it sends the technical description of the error, the same technical information and the log of your last actions. You can turn this off in **Profile → Preferences**.

The log contains no password, no form content and no web address parameter; any sign-in token or e-mail address found in it is masked by our servers before anything is stored. This information reaches the Pedalons team as tickets in a **private** GitHub repository (see section 4), where you are designated by a technical identifier, never by your name or e-mail address.

### Beta Programme Sign-ups

On the Apps page (website or mobile app), anyone, with or without an account, can leave an email address to hear when a test version of our mobile or Garmin app opens. We store the address, the site it was entered on and the date. No account is needed, the address is not linked to one, and no confirmation email is sent. Only Pedalons platform administrators can see the list, which covers every site. We use the address only to contact you about the beta. If we invite you, we enter your address by hand in the beta-testing service of the store concerned (Apple TestFlight, Google Play Console or Garmin Connect IQ), which then processes it under its own privacy policy. Submitting the same address twice has no effect. To be removed from the list, write to privacy@pedalons.fr.

### Third-Party GPS Service Connections

If you connect an external GPS service (Hammerhead, Garmin, Wahoo):

- **OAuth access tokens**: encrypted with AES-256-GCM before storage. We never store your credentials (username/password) for these services.
- **External user ID**: where the service returns one when you connect (Hammerhead does, Wahoo does not), to link it to your Pedalons account.
- **What we send**: only when you choose to send a route, we send that route to your account on the service: its track (a GPX file for Hammerhead, a course for Garmin, a FIT file for Wahoo) and its name. Wahoo also receives the start point, distance, total climb and descent, and a fingerprint of the file that it uses to avoid duplicates. Connecting Wahoo asks for permission to read your Wahoo profile (Wahoo requires it) and to create routes; we do not read your Wahoo profile or activities.

### Data Stored Locally on Your Device

**In your web browser:**

- **Language**: once you choose a language, it is kept in a `lang` cookie for one year, so that pages are displayed in it.
- **Unit system**, **theme preference** (light or dark display mode) and **map preferences** (chosen map background, relief and 3D display): in local storage (localStorage), kept until you clear your browser data. When you are signed in and choose your language, unit system or theme, it is also saved to your account; the value saved in your account takes precedence, and the copy in your browser is only a local cache.
- **Session cookie**: an HttpOnly cookie named `refresh_token`, containing your session refresh token. Scripts cannot read it. It is sent with every request to the site, including page loads, so that our server can show pages to you already signed in. Each time your session is refreshed, the browser gets a new 30-day expiry for the cookie; the session on our servers still ends 30 days after the sign-in that created it. When you are signed in, pages built by our server contain your profile and a short-lived access token (valid 15 minutes). These pages are sent with the "Cache-Control: no-store" header, which tells browsers and intermediate caches not to keep a copy.
- **Push notifications**: if you turn them on, the token of this browser, so that it can be unregistered when you sign out or turn notifications off. The Firebase messaging component that registers the browser is loaded only once you have turned them on, and keeps its own registration data in the browser's storage.
- **Pending invitation or team transfer**: when you open a team-invitation link, or confirm a team transfer from Biketeam, the invitation's token or the transfer request is kept in the tab's session storage while you sign in or create an account (see section 8).
- **Error reports and install suggestion**: whether you turned off automatic error reports, and when you dismissed the suggestion to install the website as an app.
- **Log of your last actions**: kept in the browser's memory (200 entries at most), for a possible problem report; it only leaves your device in the cases described in "Problem Reports and Error Reports".

**In the mobile app:**

- **Your session token**: the mobile app does not use cookies. Your refresh token is stored in your device's secure storage (iOS Keychain, accessible only after the first unlock of the device, or the Android Keystore-protected encrypted store) and is deleted when you sign out.
- **Display preferences**: a copy of your theme, language and unit system (the value saved in your account takes precedence), plus your chosen map background, the hillshade (relief shading) setting, and the layout of the routes list (list or map, density). These are kept in the app's local settings.
- **Image cache**: the photos and avatars the app has displayed, including your teams' private photos, are cached in the app's own storage so they do not have to be downloaded again. The cache is cleared when you uninstall the app or clear its data.
- **Map cache**: the map renderer keeps the map images and the team route layers it has displayed in the app's own storage, so they do not have to be downloaded again. It is cleared when you uninstall the app or clear its data.
- **Files you download**: when you download a route (GPX/FIT) or the original of an attached file, the app saves it in its temporary folder and hands it to your phone's share sheet. Attached files can include photos shared in your teams, without their metadata, or other files with whatever metadata they contain (see "Photos and images"). The file stays in that folder until the operating system clears it or you uninstall the app.
- **Push token**: the Firebase Cloud Messaging component of the app keeps an installation identifier and a push token on your device. The token is not deleted from the device when you sign out, because it identifies the app installation rather than you, but our server stops using it for your account.
- **Log of your last actions and error reports**: a file of the app holds your last actions (200 entries at most), for a possible problem report; the app also keeps the error reports it could not send yet, and your automatic-error-report setting. None of this leaves your phone except in the cases described in "Problem Reports and Error Reports".
- **No backup**: on Android, the app's data is excluded from cloud backup and from transfer to a new device, so none of the above leaves your phone that way.

**On your GPS device (Karoo, Garmin):**

When you pair a Hammerhead Karoo or a Garmin device, the Pedalons extension stores on that device only: your session tokens (access token and refresh token) and their expiry, plus, during pairing, the temporary pairing code. They are held in the extension's private application storage on the device and are deleted when you disconnect the device or uninstall the extension. Signing out in the Garmin app, or disconnecting in the Karoo extension, erases the tokens from the device only: the session on our servers stays valid until it expires (90 days after pairing). To end it immediately, use "Sign out of every device" in the mobile app's profile. This storage is protected by the device's own application sandbox but is not additionally encrypted by us, so anyone with access to the unlocked device (or to a backup of it) could read those tokens; if you lose the device, use "Sign out of every device". No ride or route content is retained on the device by the extension.

You can revoke all these session credentials at any time with "Sign out of every device" in the mobile app's profile. Signing out of the mobile app removes that phone's push registration, and signing out of the website, or turning off notifications there, removes that browser's, so it stops receiving notifications. "Sign out of every device" ends every session but removes only the push registration of the device you use it from; your other phones and browsers keep receiving push notifications until you sign out on them, turn notifications off, or uninstall the app.

---

## 2. How We Collect Your Data

- **Directly from you**: when you create an account, fill in your profile, import GPX files, create content, or connect a GPS service.
- **Automatically**: IP address and user agent during sign-in; session cookie to maintain your authentication; server logs as described in section 1; and, unless you turn them off, automatic error reports (see "Problem Reports and Error Reports").
- **From the platform Pedalons succeeds**: a Biketeam team administrator can have their team's content transferred from Biketeam's server, as described in section 1 ("Teams Coming from Biketeam").
- **From other members**: a team administrator who invites you gives us your email address (see "Team Invitations"). If you have no account, we use that address only to send you the invitation, to show the invitation to the team's administrators, and to offer it to you if you sign up with that address. It is deleted as described in section 6. A member who reports you or your content gives us the reason for the report, their message and a copy of the reported text (see "Reports and Blocks").
- **From the beta sign-up form**: anyone can enter an email address on the Apps page, with no confirmation email (see "Beta Programme Sign-ups").
- **When you use a location search field** (team location, meeting places, classified ads on the website; available only when signed in): the text you type is sent to our server, which looks it up with the OpenStreetMap Nominatim service on your behalf. Nominatim receives the search text and your display language, sent from our server's address — never your IP address or your identity. Results are kept in our server's memory for up to 24 hours so the same search is not sent twice.

Apart from team transfers from Biketeam, invitations, reports and the services described above, **we do not collect data from third parties**: we never buy or rent data, we run no advertising tracking, and we collect nothing from social networks.

---

## 3. Why We Use Your Data

| Purpose | Legal Basis (GDPR) |
|---------|-------------------|
| Provide the service (account, authentication, navigation) | Performance of contract |
| Display your team's routes, rides, trips and other content | Performance of contract |
| Send verification emails and sign-in codes | Performance of contract |
| Show notifications about your teams in your inbox on the website and in the app | Performance of contract |
| Send you notifications by email, according to your notification settings | Performance of contract (adjustable at any time, per type) |
| Notify you on your phone or in your browser (push notifications) | Consent (permission granted on the phone or in the browser); you can withdraw it in your device's or browser's settings, or turn push off per type in your notification settings |
| Post the team's announcements to the chat channel its administrators connected | Legitimate interest (the team's interest in informing its members) |
| Relay messages about classified ads | Performance of contract |
| Team invitations | Legitimate interest (the team's interest in inviting its members) |
| Moderate content: handle reports, apply your blocks, filter abusive terms at publication | Performance of contract (terms of service) and legitimate interest (protecting members) |
| Keep proof that you accepted the terms of service | Legitimate interest |
| Secure your account (suspicious session detection) | Legitimate interest |
| Sync your routes with connected GPS devices | Consent (voluntary connection) |
| Display maps (map backgrounds downloaded by your device from the chosen provider) | Legitimate interest |
| Approximate location ("Around me", optional, mobile app) | Consent, given through the operating-system permission prompt; you can withdraw it in your device settings |
| Provide your personal calendar feed to the calendar app you choose | Consent (voluntary subscription) |
| Tell you when a beta of our apps opens | Consent (voluntary sign-up) |
| Handle your problem reports and fix the app's errors | Legitimate interest (reliability of the service; automatic reports can be turned off) |
| Continue the service for teams coming from the previous platform | Legitimate interest |
| Operate and support the platform | Legitimate interest |

We **never** use your data for:
- Targeted advertising
- Resale to third parties
- Automated profiling or automated decision-making
- Usage analytics — we run no analytics or tracking tools at all

---

## 4. Data Sharing

### Visibility Within the Platform

- **Team content** ("team"): visible to the members of that team. Drafts and unpublished items are visible only to the team's organisers and administrators.
- **Unlisted content** ("unlisted"): not shown in public lists, search results or directories, but readable by anyone who has the link, including people without an account. Treat an unlisted link as semi-public: we cannot prevent it from being forwarded.
- **Public content** ("public"): content set to "public" in a public team is accessible to anyone on the internet, without an account and without logging in. Such pages are rendered by our server and may be indexed by search engines and displayed as link previews in social networks and messaging apps. This includes the participant lists of public rides and trips (display name and profile picture of each registered member).
- **Your display name and profile picture** are visible to the members of your teams (subject to the member-list rules in section 1). They are also visible to anyone on the internet, without logging in, wherever they appear on public content — for example in the participant list of a public ride or trip, or as the author of a public post. Profile picture files themselves are served from an address that does not require logging in, so anyone holding that address can display the image. Choose a display name and a picture you are comfortable showing publicly; you can remove your picture at any time from your profile.
- **Content shared by link**: a file you analyse with the GPX tool produces a preview page whose address contains a random, unguessable identifier. Anyone who has that address can open the page, download the track as a GPX or FIT file, and see the rendered map image — no account is needed, because the link itself is the key. See "GPX Analysis Tool" in section 1.
- **Link previews**: when a link to a route, a ride or an analysed GPX file is pasted into a messaging app, a social network or a chat tool, that platform's servers fetch the page and download the preview image we publish for it — for GPS tracks this image is a rendered map of the track itself, accompanied by its name, distance and elevation. The platform where you paste the link therefore receives that map image and those statistics, even if nobody clicks the link. This applies to any link to publicly reachable content and to any analysed GPX file. Do not share the link if the track starts at your home.
- **Calendar subscriptions (ICS)**: your profile offers a personal link that lets an external calendar application display your rides and trips. This link contains a secret token and works without logging in: anyone who obtains it can read the rides and trips you can see, including team-only ones, for as long as the link is valid. If you subscribe from a hosted calendar service (Google Calendar, Apple Calendar, Outlook, …), that service fetches the feed on its own servers, roughly once an hour, and stores the titles, dates and links of your teams' rides and trip stages together with your team names; we have no control over what that provider does with it. Treat the feed address as a password. The link has no expiry date; you can invalidate it at any time by regenerating it, which immediately breaks the old link.
- **Team chat channels**: if your team's administrators have connected a chat channel or another address (see "Notifications" in section 1), the team's announcements, with the name of the member who triggered them, are posted to that service. The team chooses the service, which receives these messages as a recipient acting under its own terms, not as our provider.
- **Reports**: the team's organizers and administrators see reports about their team's content without the reporter's identity; the Pedalons team sees who reported (see "Reports and Blocks" in section 1).
- **Platform administration**: a very small number of platform administrator accounts (currently the data controller himself) can technically access all accounts and all content across all sites, including team-only and draft content, and can list account email addresses and last-login dates. Platform administrators can also see the list of email addresses registered for the beta programmes, across all sites. This access is used only to operate, support and moderate the service, and only when necessary.

### Technical Service Providers

We use technical services to operate the platform:

| Service | Role | Data Involved |
|---------|------|--------------|
| OVHcloud (OVH SAS, France) | Application, database, and object storage hosting | All data |
| Scaleway (Scaleway SAS, France) | Delivery of transactional emails and email notifications, through its Transactional Email SMTP relay | Email address, display name, and the contents of the message we ask it to deliver: email-verification link, one-time sign-in code, password-reset link (these links and codes are single-use and short-lived); team invitations (the invitee's email address, which may belong to someone without an account, the inviting administrator's display name, the team name and the invitation link, valid 14 days); messages about classified ads (the author's email address and display name, the sender's display name, the sender's email address as the reply address, the ad's title and link, and the full text of the message); notification emails and daily digests (your display name, the site name, the team's name, the title and date of the ride, trip or post concerned, the name of the member who triggered it, for comments and replies an extract of up to 280 characters, and links to that page and to your notification settings); and the link to your data export |
| GitHub (GitHub, Inc., United States) | Tracking of problem reports and error reports, in a private repository only the Pedalons team can access | Report text, technical information, log of last actions, team viewed, technical account identifier and domain |
| Google Firebase Cloud Messaging (Google Ireland Limited, Ireland) | Routing push notifications to the mobile app, through Apple Push Notification service for iPhones, and to your browser | The push token of your phone or browser, your device's IP address when the app or the browser contacts Firebase, and the content of each notification: title and text (which can include a team name, a ride, trip or post title and date, the name of a member and an extract of a comment), plus technical data used to open the right page (notification type and identifier, team and page identifiers) |
| Apple Push Notification service (Apple Inc., United States) | Delivering push notifications to iPhones, relayed by Firebase Cloud Messaging | The same notification content and your device's Apple push token |
| Hammerhead, Garmin, Wahoo (United States) | Sending your routes to your GPS device, only if you connect the service | OAuth tokens, and the routes you choose to send (track, name; for Wahoo also start point, distance, climb and descent) |

Our server composes each email itself and hands it, complete, to Scaleway's SMTP relay, which therefore receives everything the message contains: the verification link, sign-in code or reset link, the invitation link, the text of a message about an ad, or the content of a notification. Unlike the authentication links and codes, invitation links stay valid for 14 days and notification emails contain lasting links to the content concerned. The provider is contractually bound to use this data only to deliver the email on our behalf.

### Browser Push Services

On the website, a push notification is delivered to your browser by its vendor's push service: Google for Chrome, Mozilla for Firefox, Apple for Safari, Microsoft for Edge. Your browser chooses this service, not us, as for any website that sends notifications. The service receives the encrypted message, which it cannot read, and your browser's subscription address; it also sees your device's IP address when the browser connects to it. It acts as an independent controller, under your browser vendor's policy, not as a provider on our behalf. It is only involved if you turn notifications on for the website.

### Map Display and Address Search

Maps are drawn in your browser or app. The list of map backgrounds, and the style documents of some of them, come from our server; the map images themselves are downloaded by your device directly from the provider of the map style you selected. That provider therefore receives your IP address, your browser or app version, and the coordinates of the map area you are viewing (which reveals, roughly, the area of the route you are looking at). For map backgrounds we send them nothing about you: they only see the request your device makes, and your map-style choice is stored locally on your device. These providers act as independent controllers, under their own policies. Place search, by contrast, goes through our server (see section 2).

| Service | Role | Data Involved |
|---------|------|--------------|
| VersaTiles (tiles.versatiles.org) | Vector map background (default style), map fonts and sprites | IP address, map area viewed |
| Mapterhorn (tiles.mapterhorn.com) | Terrain-elevation and hillshade tiles (3-D relief); also used by our server to correct the altitude of imported tracks | Your browser or app, only when you switch on relief shading or 3-D terrain (both off by default): IP address, map area viewed. Server-side: coordinates of coarse map tiles (~10 km squares) covered by a track and our server's IP address — never your identity, account or IP address |
| IGN / Géoplateforme (data.geopf.fr, France) | French IGN map, satellite and SCAN 25 backgrounds | IP address, map area viewed |
| OpenStreetMap Foundation (tile.openstreetmap.org, United Kingdom) | OpenStreetMap map background | IP address, map area viewed |
| OpenStreetMap France (tile-cyclosm.openstreetmap.fr) | CyclOSM map background | IP address, map area viewed |
| OpenStreetMap Nominatim (nominatim.openstreetmap.org) | Place search, queried by our server | The text you type and your display language; never your IP address or identity |
| Esri (server.arcgisonline.com, United States) | "Satellite (ESRI)" map background | IP address, map area viewed |

The "Michelin" map background is served by us (tiles.pedalons.fr): choosing it involves no third party.

Our image processing (imgproxy), map rendering (tileserver) and route calculation (Valhalla) services are self-hosted and transmit nothing to third parties. The site's and the app's typefaces are bundled with them: none is loaded from a third-party service, except the map-label fonts that come with the map background (see VersaTiles above). The only external services involved in processing a track or a search server-side are the terrain-elevation service and the place-search service listed above; those requests are made by our server, never carry your identity, and are cached so that the same area or search is not requested twice.

### We Do Not Sell Your Data

We do not sell, rent, or share your personal data for commercial or advertising purposes.

### Authorities

We may be required to disclose your data if required by law (judicial request, legal obligation).

---

## 5. International Data Transfers

Our servers are hosted by **OVHcloud** (OVH SAS, Roubaix, France) and are located in France. The data we store remains within the European Union.

Some processing you can trigger involves servers outside the European Union:

- **Connecting a third-party GPS service** (Hammerhead, Garmin, Wahoo) involves a data transfer to these services, located in the United States. This transfer is based on your explicit consent when connecting the service.
- **The "Satellite (ESRI)" map background** is requested directly by your browser or by the mobile app from Esri (United States), which receives your IP address and the map area you are viewing, only if you choose it; you can avoid this transfer entirely by choosing a different background. The OpenStreetMap background is served from the United Kingdom, which benefits from a European Commission adequacy decision.
- **The mobile app includes Google's Firebase messaging component**, which contacts Google's servers when the app starts, even before you sign in or allow notifications. This reveals your device's IP address to Google and creates a Firebase installation identifier. The app includes only this messaging part of Firebase: no Firebase analytics, advertising or tracking component. On the website, this component is loaded only once you have turned on notifications.
- **Push notifications** pass through **Firebase Cloud Messaging**, provided by Google Ireland Limited. Google may process this data in the United States; this transfer is covered by the European Commission's standard contractual clauses and by Google LLC's certification under the EU–US Data Privacy Framework. On iPhone, notifications are delivered by Apple's Push Notification service (Apple Inc., United States), as for any iOS app; in a browser, by your browser vendor's push service, which may be located in the United States but only receives the encrypted message (see "Browser Push Services" in section 4). The title and text of a notification and the push token of your phone or browser may therefore be processed in the United States. This happens only while you allow notifications; you can stop it at any time by turning push off in your notification settings, turning notifications off in your phone's or browser's settings, or signing out.
- **Problem reports and error reports** are sent to **GitHub** (GitHub, Inc.), in the United States; this transfer is covered by the European Commission's standard contractual clauses. They contain neither your name nor your e-mail address.
- **Team chat channels**: if your team's administrators have connected one, the service they chose (for example Slack or Discord, operated from the United States) receives the team's announcements. That transfer is the team's choice and follows that service's own terms.
- **Beta programmes**: if we invite you to test one of our apps, your email address is entered in Apple TestFlight, Google Play Console or Garmin Connect IQ, some of which are operated from the United States.

---

## 6. Data Retention

| Data Type | Retention Period |
|-----------|-----------------|
| Account data (email address, display name, profile picture, preferences, password hash) | For as long as your account exists; erased as soon as you delete it (see section 7) |
| Date of acceptance of the terms of service | As long as your account exists |
| Login sessions (hashed refresh token, IP address, user agent, creation date, last-use date) | 30 days from the sign-in that created the session (the period is not extended by continued use). Expired or signed-out sessions are erased by a nightly clean-up job |
| Sessions of a paired GPS device (Karoo, Garmin) | 90 days from pairing; the device refreshes its access token from this session without extending it. "Sign out of every device" revokes it; signing out on the device itself does not |
| Temporary sign-in codes (OTP) | 5 minutes, or until 5 wrong attempts |
| Email address verification link | 24 hours. Until it is used, this record also holds the display name, the password hash and the time of acceptance of the terms you chose or gave during sign-up |
| Password reset link | 1 hour |
| Email change verification link | 24 hours |
| GPS device pairing codes | Valid for 10 minutes; the record is deleted as soon as the device completes pairing, or by the nightly clean-up if it expired unused |
| Passkey (WebAuthn) challenges | Valid for 5 minutes; the record is deleted when the challenge is used or a new one is requested, or by the nightly clean-up once expired |
| Calendar token (secret URL of your .ics feed) | Kept without expiry until you regenerate it |
| Teams and content (rides, posts, routes, trips, pages) | Deleting an item hides it from members and visitors, but it stays in our database: the team's administrators still see it in their lists, marked "Deleted", and can restore it. The record is kept until permanent erasure is requested |
| Comments | Erased as soon as you or your team delete them, together with the replies they received |
| Files attached to content (images, GPX, FIT, generated map images) | As long as the associated record exists in our database. Files uploaded but never attached to any content are automatically erased one day after upload |
| GPX analysis tool previews (uploaded track, waypoints, generated GPX/FIT files and map thumbnail) | 30 days from creation, then automatically erased together with the stored files (deletable by you at any time) |
| Notifications (inbox entries, read status, the copy of the content they refer to, the record of email and push sends, and the record of messages posted to team chat channels) | 90 days from creation, then erased by a nightly clean-up. Messages already posted to a team's chat channel stay there under that service's rules |
| Notification settings (per-type settings, daily digest, muted teams) | As long as your account exists; erased when you delete it |
| Push registration of a phone or browser (push token, platform, device or browser name, app version, registration and last-seen dates) | Until you sign out of the app on that phone or of the website in that browser, turn off notifications on the website, until a send shows that Firebase Cloud Messaging no longer accepts the token (for example after the app was uninstalled), until another account signs in on that device, or until you delete your account. It is not removed after a period of inactivity |
| Team chat channel settings (address of the channel, language, last delivery status) | Until the team's administrators remove the channel |
| Team invitations (invited email address, role offered, who sent it, and when it was accepted, revoked or expired) | Valid for 14 days; the record is kept for 1 year after it was accepted, revoked or expired, so the team can see who invited whom, then deleted by a nightly job |
| Record of messages sent about a classified ad (sender, ad, date; the message itself is not stored) | As long as the ad is in our database. Deleting an ad only hides it, so in practice the record is kept until the ad is permanently erased |
| Blocks | Until you unblock the person, or until either of your accounts is deleted |
| Reports, copy of the reported text, and decision | As long as the account of the person concerned exists, to keep a record of moderation decisions; when the reporter's account is deleted, kept with no link to them (see section 7) |
| Problem reports and suggestions | 1 year on our servers, or until your account is deleted; the matching GitHub ticket is kept as long as it is useful to track the bug |
| Automatic error reports | 90 days on our servers, or until your account is deleted; the GitHub ticket, shared by every member hit by the same error, is kept as long as it is useful |
| Team transfer requests from Biketeam (account that confirmed, Biketeam team, dates, outcome) | 1 year after the transfer ended, or after the request expired if the transfer never started, then deleted by a nightly clean-up; after the account is deleted, the request is no longer linked to anything that identifies the person |
| Beta programme sign-ups (email address, site, date) | 1 year after sign-up, then deleted by a nightly clean-up; ask privacy@pedalons.fr to be removed sooner |
| Data export archive (ZIP) | 7 days, then erased by a nightly job (at the latest the following night); like any stored file, a copy can remain in backups for up to 30 more days |
| Data export request history (request date, status, archive size, expiry date) | 90 days |
| Server access logs (date and time, IP address, browser or device, requested address without the coordinates or tokens passed as parameters, response code) | 14 days, then deleted automatically |
| Backups (full copy of the database and of stored files) | One copy per night on a separate server; the 30 most recent copies are retained, older ones are deleted automatically |
| Data after account deletion | Erased immediately; gone from backups within 30 days (see section 7) |

The periods above are the periods during which the data can be used. Records that have expired, been used or been signed out are physically erased from the database by clean-up jobs that run once a night, so they may remain stored for up to 24 hours beyond the period shown.

**Backups.** Every night we take a complete copy of the database and of the uploaded files (photos, avatars, GPX files) and store it on a separate server in France, so that the service can be restored after a failure. We keep the last 30 nightly copies; each is deleted automatically once it ages out. This means that when you delete content, or when data is erased, a copy can remain in our backups for up to 30 more days. We do not use backups for any purpose other than restoring the service, and a restore is only performed after an incident.

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

You can exercise the rights of access and portability yourself, without writing to us: on the website, under **Profile → Your data**, choose "Download my data"; in the mobile app, under **Profile → Your data**, tap "Request an export". In both cases we prepare a ZIP archive and email you a download link, which works on any of your devices.

The archive contains your profile, your teams, your sign-ups, everything you have published, and your files (profile picture, uploaded images, and the GPX and FIT files of your routes). It also contains your notifications from the last 90 days (with the emails and push notifications sent for each), your notification settings (including the daily digest and the teams you muted), the phones and browsers registered for push notifications, the members you blocked, the reports you made (without the copy of the reported text, which is someone else's content) and your problem reports and suggestions. The data is in JSON, a structured and machine-readable format. The archive does not yet include your time zone, your ad-contact setting, the team invitations you sent or received, or the record of messages you sent through the classified-ads relay; ask privacy@pedalons.fr for these.

For security reasons, credential material is excluded: your password hash, session tokens, the cryptographic material of your passkeys, your calendar token, the access tokens of your connected GPS services, and the push token of each of your phones and browsers. Their metadata (dates, devices, services involved) is included. The download link expires after **7 days**, after which the archive is deleted from our servers (a copy may remain in our backups for up to 30 days, see section 6). One export per hour per account.

### Delete your account

You can delete your account yourself, at any time, without writing to us:

- **in the mobile app**: **Profile → Account → Danger zone**, then "Delete the account";
- **on the website**: **Profile → Account Actions → Danger Zone**, then "Delete Account".

If you are the only administrator of a team that has other members, you must first make another member an administrator: the app and the website name the teams concerned and tell you what to do. A team of which you are the only member is deleted along with your account, in the same way as any deleted team (see section 6).

If you no longer have access to your account or to the app, write to **privacy@pedalons.fr** from your account's email address asking for its deletion; we will process it within 30 days.

Deletion is irreversible and immediate. As soon as you confirm, your account is deactivated and your personal data is erased: email address, name, profile picture, password and passkeys, sessions (including those of your GPS devices), preferences, calendar token, connected GPS services, team memberships, registrations for upcoming rides and trips, comments, notifications and their settings, phone and browser registrations for push notifications, data exports, GPX previews, your problem reports and error reports, and the record of the messages you sent about classified ads. Your classified ads are withdrawn, stripped of their text, price and location, and their photos are deleted. Blocks are deleted both ways, those you made and those aimed at you, and reports about you are deleted along with the copy of your content they held. Reports you made are kept, with no remaining link to you, so that the decisions taken can still be checked. GitHub tickets already created from your reports remain, designated by a technical identifier that no longer leads to anyone.

What you published for a team (rides, trips, routes, posts and their files) belongs to that team and stays online. That content is from then on credited to "Ancien membre" (French for "former member") and is no longer linked to any data that could identify you. Likewise, a comment other members replied to is kept empty, marked "Comment deleted", so that their replies do not disappear with it. Your registrations for past rides are kept in the same anonymous form and are no longer displayed. Notifications you triggered for other members keep your display name until they expire (90 days), and messages already posted to a team's chat channel stay there.

If you want content published for a team to disappear, delete it before deleting your account, or ask one of the team's organizers. Deleting your account removes the link to your GPS services on our side, but does not withdraw the authorisation you gave them: you can revoke it from your Hammerhead, Garmin or Wahoo account.

Our backups, kept for 30 days, still contain your data until they are renewed; they are only used to restore the service after an incident.

### Contact us

For the other rights, or if you would rather go through us, contact us at: **privacy@pedalons.fr** To protect your data, we may ask you to confirm a request from the email address attached to your account.

We will respond to your request within **30 days**. If we cannot comply, we will explain why.

You may also lodge a complaint with the **CNIL** (French Data Protection Authority): [www.cnil.fr](https://www.cnil.fr)

---

## 8. Cookies and Local Storage

Pedalons uses a minimal number of cookies and local storage items:

| Item | Type | Purpose | Duration |
|------|------|---------|----------|
| refresh_token | HttpOnly cookie (website) | Maintain your authenticated session, including on pages built by our server | 30 days, renewed on use (the session itself ends 30 days after sign-in) |
| refresh_token | iOS Keychain / Android Keystore-encrypted storage (mobile app) | Keep you signed in without re-entering your credentials | Until you sign out (server-side session validity: 30 days) |
| lang | Cookie | Remember the language you chose, so that pages are displayed in it | 1 year |
| pedalons-unit-system | localStorage | Remember your unit system | Persistent |
| mantine-color-scheme-value | localStorage | Remember your theme (light/dark) | Persistent |
| pedalons-map-style, pedalons-map-terrain3d, pedalons-map-hillshade | localStorage | Remember your map display preferences | Persistent |
| pedalons-error-reports | localStorage | Remember that you turned off automatic error reports | Persistent |
| pedalons.webPush.token | localStorage | Unregister this browser from push notifications when you sign out or turn them off | Until you sign out or turn them off |
| Firebase messaging data | Browser storage (website) | Registration of this browser for push notifications, only once you turn them on | Until you clear your browser data |
| pedalons.installBanner.dismissedAt | localStorage | Remember that you dismissed the suggestion to install the website as an app | Persistent (the suggestion comes back after 90 days) |
| pendingInvitationToken, pendingBiketeamMigrationRequest | sessionStorage | Keep a team invitation, or a team transfer request from Biketeam, while you sign in | Until the tab is closed |

The language, unit and theme values stored in your browser or app are a local copy: when you are signed in, the value saved in your account takes precedence. The mobile-app row is stored by the app on your device, not as a browser cookie; it is strictly necessary for the app to keep you signed in. For the other data kept by the mobile app (preferences, caches, push token, log of your last actions and pending error reports), and for the tokens stored by the Karoo and Garmin extensions, see "Data Stored Locally on Your Device" in section 1. The mobile app and the website include only the messaging part of Google Firebase, used for push notifications: no Firebase analytics, advertising or tracking component.

**We do not use any tracking, analytics, or advertising cookies.** No cookie consent is therefore required: the session cookie is strictly necessary for the service to function, and the lang cookie only remembers a choice you made. Note that displaying a map causes your browser to request map tiles directly from the provider of the style you selected — see "Map Display and Address Search" in section 4.

---

## 9. Security

We implement the following measures to protect your data:

- **Encryption in transit**: all communications use HTTPS (TLS).
- **Encryption at rest**: GPS service OAuth tokens are encrypted with AES-256-GCM.
- **Password hashing**: passwords are stored using bcrypt, a deliberately slow one-way transformation — never in readable form.
- **Secret hashing**: your session refresh tokens, one-time passwords (OTP), email-verification, email-change and password-reset links, and team-invitation links are stored only as irreversible hashes — we never keep the original value. Two secrets are an exception and are stored in readable form because our systems must look them up directly: the token contained in your calendar subscription (ICS) link, and the short pairing code displayed on your GPS device (which expires after 10 minutes).
- **Secure cookies**: the session cookie is HttpOnly (scripts cannot read it), Secure (sent over HTTPS only) and SameSite=Lax. It is sent when you arrive from a link on another site, so that the page can open already signed in; our server rejects any request that would change data if it comes from another website and is authenticated only by that cookie.
- **On-device token storage**: on mobile, the refresh token is held in the Apple Keychain or the Android Keystore-encrypted store; on GPS devices (Karoo, Garmin), tokens are held in the extension's sandboxed application storage without additional encryption.
- **Multi-tenant isolation**: each domain's data is isolated at the database level; ordinary users and team administrators can never reach another domain's data. Platform administrators are the single documented exception (see section 4).
- **Limits on sign-in codes**: we cap how many one-time sign-in codes, password-reset links and email-change links can be requested for the same address in a short period; every such code or link expires quickly and can only be used once, and a sign-in code is invalidated after 5 wrong attempts.
- **Deletion**: comments (with their replies), photos and other uploaded files, waypoints, places, participations, passkeys, calendar links and GPS-service connections are deleted immediately and permanently when you or your team delete them. If a notification was created about a comment or a reply, the extract kept in the notification stays until the notification expires (90 days), even if the comment is deleted. Teams and published content (rides, posts, routes) are instead marked as deleted: they disappear straight away for members and visitors, while team administrators still see them, marked "Deleted", and can restore them; permanent erasure is performed on request (see section 6). A deleted account, by contrast, is erased immediately (see section 7).
- **Backups on a separate server**: taken nightly and pushed over an encrypted tunnel through a one-way channel, so that a compromise of the production server cannot read or delete the backup history.
- **Server logs**: kept for security, abuse detection and troubleshooting only (see section 1).

No system is infallible. If you notice suspicious activity on your account, contact us immediately.

---

## 10. Children's Privacy

Pedalons is not intended for children under 16. We do not knowingly collect personal data from minors under 16. If you are a parent and believe your child has provided us with data, contact us so we can delete it.

---

## 11. Changes to This Policy

We may update this policy to reflect changes in our practices or in regulations. In case of a substantial change:

- We will publish the updated version on this page.
- We will update the "last updated" date at the top of this document.
- For significant changes, we will inform you by email.

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
