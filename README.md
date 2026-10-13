# ClanSocket
[![Plugin Hub rank](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/clansocket)](https://runelite.net/plugin-hub/show/clansocket) [![Plugin Hub installs](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/clansocket)](https://runelite.net/plugin-hub/show/clansocket)

Sends your own Old School RuneScape clan and gameplay events over a single WebSocket to the ClanSocket relay at [clansocket.com](https://clansocket.com). The relay passes them on, live, to the sites and services your clan's Owner or Deputy Owner has approved, and stores none of them. Every stream has its own switch in the side panel.

The plugin follows the in-game clan you are a member of. Your events reach a site only after your clan approved it with an in-game prompt. Until then the relay drops them.

---

### **Setup**

1. Install **ClanSocket** from the Plugin Hub (`Configure` → `Plugin Hub` → search "ClanSocket").
2. Enable the plugin.
3. Log into RuneScape on a character that is a member of your clan.

A site that uses ClanSocket asks your clan for access through a prompt in the side panel, and your Owner or Deputy Owner confirms it there. See the [Claim your clan](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Claim-Your-Clan) wiki page. The sites built on the relay are listed at [clansocket.com](https://clansocket.com/#consumers).

---

### **Side Panel**

The plugin adds a side panel to the RuneLite right-rail. The panel is the primary UI for everything except the WebSocket endpoint (which lives in the RuneLite Configuration tab).

![panel](https://github.com/osrs-clansocket/clansocket-plugin/wiki/screenshots/plugin-active.png)

- **Header**: connection state (Online, Offline, Reconnect), your clan, and a reset button for the per-account event counters.
- **Presets** — Manual or Clan mode toggle, plus 7 save slots.
- **Telemetry cards** — 24 cards, one per data stream. Click to toggle off; click again to re-enable. Frequent streams render a 60-second sparkline; infrequent streams show a `Last: Nm ago` indicator.
- **Footer** — links to clansocket.com and the GitHub repo.

When a site asks your clan for access, or asks to verify your name, a green-pulsing banner appears above the cards with confirm and reject buttons and a countdown. A rejected or unanswered request gives nothing away.

![consent banner](https://github.com/osrs-clansocket/clansocket-plugin/wiki/screenshots/ingame-panel-notif-clan-claim.png)

---

### **Configuration**

`Configuration → ClanSocket → Network customization` (collapsed by default) — one field, in the RuneLite Configuration tab:

| Setting | Effect |
| --- | --- |
| WebSocket URL | The relay's address. Blank uses `wss://ws.clansocket.com/data`. A bare host (no scheme) is prefixed with `wss://`. |

All other settings live in the side panel.

---

### **What It Streams**

When the plugin is enabled and you are logged in to a clan-member character, events flow over a single WebSocket connection as per-tick batches:

**Game telemetry, your character only:**

- Identity: RSN, account hash, account type, world, world types, current activity, clan name, rank, join date, member counts and session start.
- Clan roster and rank title snapshots.
- Skills snapshot, XP gains and level-ups.
- Combat: hitsplats dealt and taken, and the current target.
- Deaths and slayer state.
- Vitals: hitpoints, prayer, run energy, weight and special attack.
- Active prayers, stat boosts, and poison and venom.
- Location (x, y, plane, region, area) and world hops.
- Inventory, equipment and seed vault: a snapshot per session, then each change.
- Bank open and close snapshots, and rune pouch slots.
- Loot and pet drops.
- Quests, diaries, clue scrolls, the collection log and combat achievements: snapshots and completions.
- Farming patch changes, and menu actions on objects, NPCs, items and emotes.

The full list with each event's fields is at [clansocket.com](https://clansocket.com/#plugin).

**Clan chat.** Every message and Jagex-generated broadcast visible in the configured clan channel — same pattern as the long-running [clan-chat-webhook](https://github.com/pascalla/clan-chat-webhook) plugin. Scoped strictly to your configured clan; never any other channel.

### **What It Does NOT Stream**

- Public chat, private messages, friends-list chat, guest chat, or any clan channel other than the configured one.
- Other players' character data (only their messages in your clan channel, and Jagex's broadcasts about them).
- Any data when you are not logged in to RuneScape.
- Anything to a site your clan has not approved. The relay drops events for a clan with no approved site.

---

### **Privacy**

Three layers of control:

- **24 per-stream toggles** in the side panel — click any card to disable a stream.
- **Explicit consent** dialogs on Location re-enable and on the Manual → Clan mode switch.
- **No storage at the relay.** The relay passes events through memory and keeps none of them. A site your clan approved holds what it received, under its own privacy policy: ask that site to see, export or delete your data.

Location streams your real-time position (x, y, plane, region, area) to the sites your clan approved while you are logged in. The explicit YES/NO consent dialog fires when you toggle Location from OFF back to ON, so you see it any time you re-enable after disabling. On a fresh install no dialog appears; Location streams from the moment you log in. To gate this before any coords leave your client, toggle Location off from the panel right after install and re-enable when you are ready.

For the full privacy model, see [Privacy overview](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Privacy-Overview) and [Data rights](https://github.com/osrs-clansocket/clansocket-plugin/wiki/Data-Rights) in the wiki, and the relay's policy at [clansocket.com](https://clansocket.com/#privacy).

---

## **Safety & Compliance**

All plugins that are successfully merged into the [Plugin Hub](https://github.com/runelite/plugin-hub) are reviewed and verified by the RuneLite development team, ensuring they are safe to use. For more information, you can view the [Plugin Hub readme](https://github.com/runelite/plugin-hub#Reviewing).

Furthermore, [RuneLite itself has been confirmed as fully compliant by Jagex](https://secure.runescape.com/m=news/a=13/another-message-about-unofficial-clients?oldschool=1).

---

## **Creator Tag**
![Smoke](https://i.ibb.co/PTYfzqB/Rune-LITE-By-Smoke.png)

For additional support or questions, reach out via [Discord](https://discord.gg/RQ9H9naf7E).
