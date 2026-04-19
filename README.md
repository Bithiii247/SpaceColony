# 🚀 Space Colony

Object-Oriented Programming Project — Android App

## How to Open

1. Open **Android Studio**
2. `File → Open` → select this `SpaceColony` folder
3. Wait for Gradle sync
4. Run on device or emulator (API 24+)

## Project Structure

```
app/src/main/
├── java/com/spacecolony/
│   ├── model/          ← All game logic (OOP classes)
│   │   ├── CrewMember.java   (abstract)
│   │   ├── Pilot.java
│   │   ├── Engineer.java
│   │   ├── Medic.java
│   │   ├── Scientist.java
│   │   ├── Soldier.java
│   │   ├── Threat.java
│   │   ├── Storage.java      (singleton, HashMap)
│   │   ├── MissionControl.java
│   │   ├── Quarters.java
│   │   ├── Simulator.java
│   │   ├── MissionResult.java
│   │   ├── Location.java     (enum)
│   │   └── MissionType.java  (enum)
│   ├── ui/             ← Activities
│   │   ├── MainActivity.java
│   │   ├── RecruitActivity.java
│   │   ├── QuartersActivity.java
│   │   ├── SimulatorActivity.java
│   │   ├── MissionControlActivity.java
│   │   ├── MissionActivity.java
│   │   └── StatisticsActivity.java
│   └── adapter/        ← RecyclerView adapters
│       ├── CrewAdapter.java
│       └── StatsAdapter.java
└── res/
    ├── layout/         ← XML screen layouts
    ├── drawable/       ← Vector icons (5 specializations + launcher)
    └── values/         ← colors.xml, strings.xml, themes.xml
```

## Bonus Features Implemented
- ✅ RecyclerView (+1)
- ✅ Crew Images — unique vector drawables per specialization (+1)
- ✅ Mission Visualization — live energy bars, coloured by spec (+2)
- ✅ Tactical Combat — ATTACK / DEFEND / HEAL each turn (+2)
- ✅ Statistics screen (+1)
- ✅ No Death — Medbay instead of permanent removal (+1)
- ✅ Randomness — threat damage has random 0–2 variance (+1)
- ✅ Specialization Bonuses — mission-type-specific +2 skill bonuses (+2)

**Total targeted bonus: +11 points**

## Documentation
See `Documentation.pdf` and `UML_Class_Diagram.png` in the project root.
