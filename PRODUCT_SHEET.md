# Pedalons - Product Sheet

## Overview

**Pedalons** is a multi-tenant cycling team platform for clubs and groups to organize rides, share routes, coordinate events, and build community.

**Target audience:** Cycling clubs, informal riding groups, tour organizers, bike shops, corporate teams.

---

## Core Features

### Team Management

| Feature | Description |
|---------|-------------|
| Multi-tenant architecture | Complete team isolation |
| Custom profiles | Name, logo, description, custom pages |
| Visibility control | Public (discoverable) or private teams |
| Role-based access | Member, Organizer, Admin, Owner |
| Team pages | Custom content with markdown and media |
| Configurable features | Enable/disable trips and marketplace |

### Rides & Events

| Feature | Description |
|---------|-------------|
| Ride scheduling | Date, time, location, description |
| Multiple pace groups | A/B/C groups with different speeds |
| Route linking | Attach GPX routes to rides |
| Participant management | Registration, capacity limits, attendee lists |
| Start/end places | Predefined meeting points |
| Draft workflow | Prepare before publishing |
| Ride templates | Reusable templates for recurring rides |

### Routes & GPX

| Feature | Description |
|---------|-------------|
| GPX upload | Import from any cycling app |
| Auto-calculated metrics | Distance, elevation gain/loss, hilliness |
| Surface classification | Road, gravel, MTB, mixed |
| Map visualization | Interactive route display |
| Advanced filtering | By distance, elevation, terrain |
| FIT export | Download for cycling computers |
| Thumbnail generation | Auto-generated previews |

### Multi-Day Trips

| Feature | Description |
|---------|-------------|
| Trip management | Multi-stage cycling events |
| Stage configuration | Individual routes and places per stage |
| Trip participation | Registration for complete trips |

### Posts & Communication

| Feature | Description |
|---------|-------------|
| Team announcements | News and updates |
| Rich content | Markdown, images, videos |
| Visibility options | Team-only or public |

### Marketplace (Ads)

| Feature | Description |
|---------|-------------|
| Listing types | Sale, rental, wanted |
| Pricing | Fixed price or rental periods |
| Search & filter | By type, date, keywords |

### Additional Features

- **Comments**: Threaded discussions on rides, routes, posts, trips
- **Places directory**: Meeting points with geolocation
- **Unified feed**: Combined publication stream across teams

---

## User Management

### Roles & Permissions

| Role | Capabilities |
|------|--------------|
| **Member** | View, participate, comment |
| **Organizer** | Create/edit rides, routes, posts, places |
| **Admin** | Manage members and team settings |
| **Owner** | Delete team, transfer ownership |

### Authentication

- Email + OTP (one-time password sent by email)
- Passkeys / WebAuthn support
- Device Code Flow (RFC 8628) for GPS devices (Karoo, Garmin)
- Automatic user creation on first login
- User profiles with avatars

---

### Key Technical Features

- TSID (time-sortable unique IDs)
- Auto-generated URL slugs with redirect on change
- Multi-language support (EN/FR)
- Dark mode
- Platform admin panel

---

## Asset Management

| Type | Usage |
|------|-------|
| Logo | Team branding |
| Image/Video | Media content |
| GPX/FIT | Route data |
| Thumbnail | Auto-generated previews |

---

## Implemented Integrations

- **Mobile app** (Flutter, iOS/Android) — teams, rides, routes, calendar
- **Garmin Connect IQ app** — route browsing and FIT download on Edge devices
- **Hammerhead Karoo extension** — route browsing and sync
- **GPS device sync** — upload routes to Garmin Connect, Karoo and Wahoo
- **Calendar sync** — iCal feed export

## Roadmap Potential

- Weather forecasts for rides
- Live tracking during rides
- Push notifications
- Statistics and analytics

---

## Summary

Pedalons is a production-ready platform for cycling communities combining team management, event coordination, route sharing, and social features.

**Key differentiators:**
- True multi-tenancy with complete team isolation
- Advanced GPX/route management with terrain analysis
- Flexible ride organization with multiple pace groups
- Multi-day trip support for tours
- Built-in marketplace for equipment exchange
- Modern, responsive web interface
