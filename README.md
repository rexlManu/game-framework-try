# Game Framework

## Current state

That was my unsuccessful try to make a game framework for minigames.

I realised that it takes way too much time to create such a thing. Also many things get too complex and I actually need
to design first the whole concept and not just start it. But I would like to start it again in the future with a more a
plan but solong I will release it and maby somebody it could be helpful. Things like the own schematic system, async
block placement or anything else.

You will find the planing here c:

## Intention

Create easily minigames with little effort and don't waste time with boiler code.

## Goals

- [ ] Arena System
  - [ ] Single Arena
  - [ ] Multi Arena
- [ ] Template System
  - [ ] Configuration
- [ ] Map System
  - [ ] Setup Tool
  - [x] Custom Format
  - [ ] Map Voting
- [ ] Custom Events
- [ ] Team System
- [ ] GameState
  - [ ] LobbyState
  - [ ] PlayingState
  - [ ] EndState
- [ ] Countdown
- [ ] Stats
- [ ] 1.8-1.16 Support
- [ ] Playerhandling
  - [ ] Kill
  - [ ] Death

## Planing

- [ ] Custom Inventories
- [ ] Custom Properties
  - [ ] Team
  - [ ] Player
- [ ] Kit System
- [ ] Achievements
- [ ] Chestloot

## Explaining

### Arena System

This is planned for that you can decide how the game will be played. Like normally on a single server happens one game a
time or multiple games on a server and with signs or other options to join a game

### Template System

Template are configurations for games. As example in bedwars it would he something like which team size, team amount and
which map.

### Map System

The maps should be like schematics for earily provding for multi arena. Also the setup tool should help non technical
player to create maps.

### Custom Events

On the game mode itself you shouldn't listen for the bukkit events because they call for every action in every game. We
want only the action in the current game

### GameState

The gamestate describes in which state the game is and which listeners are enabled. Lobby and End state are often really
same. Basic stuff like lobby countdown, leave items and such things should be aleady be done

#### The playing state

The playing state will be state with the most action happen there. For simple games like death match, there arent match
todo but like bedwars we need to spawn items, handle shop, bed breaking.

### State

The stats should be automatic collected. The basic concept is that you have points that you get for every action like
kills, bed breaking or assets. Stats should be monthly.

### 1.8-1.16 Support

The main goal here is that 1.16 players will have a better playing experience because they get features like rgb, longer
scoreboards, colored bossbar and such stuff.

### Playerhandling

Here we should handle every player action like damage, killing and death. Also it would be great to expand it with
custom actions like bed destroy as example. The optimal option would be that also stats will be handled here

### Planing

Planing are things that are more specific and will be added at a time with something will be implemented that will need
that feature.

## Basic code idea

```java

GameFramework
  .create(plugin)
  .arenaProvider(ArenaProvider.single())

```

## My intention

I would like to write much more minigames and copy famous minigames easily without writing so much boilercode. Also I
would like have something where I can expand everything.

~ 6/15/21

## Editor Format

Editor Namen:

Wartelobby:

- spawn
- hologram

SkyWars Ingame:

- center
- s1-sn spawn punkte

Property

- zone, bereich wo man bauen kann
- loot_(name des loots) range wo der loot kommt
- builder - builder
- display_item - display item
- display_data - display item data