# ClanSocket
[![](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/clansocket)](https://runelite.net/plugin-hub/show/clansocket)
[![](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/clansocket)](https://runelite.net/plugin-hub/show/clansocket)

Streams real-time OSRS clan and gameplay telemetry over a single WebSocket to a clan dashboard at [clansocket.com](https://clansocket.com). Live world map, member roster, per-stream privacy gates, GDPR-grade data rights.

The plugin targets whichever in-game clan you are currently a member of. Telemetry only reaches a dashboard if your clan has been claimed there; for unclaimed clans the server drops events silently.

---

### **Setup**

1. Install **ClanSocket** from the Plugin Hub (`Configure` → `Plugin Hub` → search "ClanSocket").
2. Enable the plugin.
3. Log into RuneScape on a character that is a member of your clan.

If your clan is not yet claimed, ask your Owner or Deputy Owner to claim it from clansocket.com — see the [Claim your clan](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Claim-Your-Clan) wiki page for the full walkthrough.

---

### **Side Panel**

The plugin adds a side panel to the RuneLite right-rail. The panel is the primary UI for everything except the WebSocket endpoint (which lives in the RuneLite Configuration tab).

![panel](https://github.com/osrs-clansocket/clansocket-plugin/wiki/screenshots/plugin-active.png)

- **Header** — connection state (Online / Offline / Reconnect), clan status (`clan: name` green when registered, `(unclaimed)` orange if not), and a reset button for the per-account event counters.
- **Presets** — Manual or Clan mode toggle, plus 7 save slots.
- **Telemetry cards** — 24 cards, one per data stream. Click to toggle off; click again to re-enable. Frequent streams render a 60-second sparkline; infrequent streams show a `Last: Nm ago` indicator.
- **Footer** — links to clansocket.com and the GitHub repo.

When the dashboard sends a consent request, a green-pulsing banner appears above the cards with confirm + reject buttons and a countdown.

![consent banner](https://github.com/osrs-clansocket/clansocket-plugin/wiki/screenshots/ingame-panel-notif-clan-claim.png)

---

### **Configuration**

`Configuration → ClanSocket → Network customization` (collapsed by default) — one field, in the RuneLite Configuration tab:

| Setting | Effect |
| --- | --- |
| WebSocket URL | `wss://` URL of your clan's dashboard endpoint. Blank uses clansocket.com. Bare host (no scheme) is auto-prefixed with `wss://`. |

All other settings live in the side panel.

---

### **What It Streams**

When the plugin is enabled and you are logged in to a clan-member character, events flow over a single WebSocket connection as per-tick batches:

**Game telemetry (your character only).** Identity (RSN, account hash, account type, world, world types, current activity, clan name + rank + join date + member counts, session start); clan roster snapshots; skills snapshot + XP gains + level-ups; combat (hitsplats dealt and taken, current target); player deaths; slayer state; vitals (run energy, weight, special attack); active prayers; stat boosts; status effects (poison, venom, disease, cold); location (x, y, plane, region, area); inventory + equipment + seed vault (baseline snapshot per session, then per-change deltas); bank open/close snapshots; rune pouch slot contents; loot drops; pet drops; quest snapshots and completions; diary snapshots and completions; clue scroll opens and completions; collection log per-item notifications and full snapshots; combat achievement catalog and per-task completions; farming patch changes; menu actions on game objects and NPCs.

**Clan chat.** Every message and Jagex-generated broadcast visible in the configured clan channel — same pattern as the long-running [clan-chat-webhook](https://github.com/pascalla/clan-chat-webhook) plugin. Scoped strictly to your configured clan; never any other channel.

### **What It Does NOT Stream**

- Public chat, private messages, friends-list chat, guest chat, or any clan channel other than the configured one.
- Other players' character data (only their messages in your clan channel, and Jagex's broadcasts about them).
- Any data when you are not logged in to RuneScape.
- Any data when your in-game clan has not been claimed on the dashboard.

---

### **Privacy**

Three layers of control:

- **24 per-stream toggles** in the side panel — click any card to disable a stream.
- **Explicit consent** dialogs on Location re-enable and on the Manual → Clan mode switch.
- **Data rights** on clansocket.com — browse every stored row, export a GDPR-style portable zip, or remove all data tied to your account.

Location streams real-time coords (x, y, plane, region, area) to your clan dashboard while you are logged in, visible to clan members only. The explicit YES/NO consent dialog fires when you toggle Location from OFF back to ON, so you see it any time you re-enable after disabling. On a fresh install no dialog appears; Location streams from the moment you log in. To gate this before any coords leave your client, toggle Location off from the panel right after install and re-enable when you are ready.

For the full privacy model and the data-rights surface, see [Privacy overview](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Privacy-Overview) and [Data rights](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Data-Rights) in the wiki.

---

## **Safety & Compliance**

All plugins that are successfully merged into the [Plugin Hub](https://github.com/runelite/plugin-hub) are reviewed and verified by the RuneLite development team, ensuring they are safe to use. For more information, you can view the [Plugin Hub readme](https://github.com/runelite/plugin-hub#Reviewing).

Furthermore, [RuneLite itself has been confirmed as fully compliant by Jagex](https://secure.runescape.com/m=news/a=13/another-message-about-unofficial-clients?oldschool=1).

---

## **Creator Tag**
![Smoke](https://i.ibb.co/PTYfzqB/Rune-LITE-By-Smoke.png)

For additional support or questions, reach out via [Discord](https://discord.gg/RQ9H9naf7E).
