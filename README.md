* Title: Track-Me-Riding
* Author: Narek Veranyan (veranyan@myumanitoba.ca)
* Student number: 8040209
* Date: November 19, 2025
---

# Overview
> Track-Me-Riding is an implementation of an exercise tracker software for COMP 2450 
> specifically designed for tracking cycling activities. The software offers
>
>   * Multiple profiles in the system that can share routes and view each other's activities,
>   * A feed of activities with each route displayed on a user-defined grid-structured map, 
>   * There are routes and obstacles that can be added to the map,
>   * There are gears to be added and later used in an activity.

## Vision Statement
> Build software that allows exercises to track exercises
> over a map, share information about their exercises,
> and measure performance over time.

## Running
This project was built using Maven and developed in IntelliJ IDEA.
It can be run using several options:

1. Open the `Main.java` class and click the green play button 
    in the top right corner.
2. Run Maven on the command line:
    ```
    mvn compile exec:java -Dexec.mainClass="ca.umanitoba.cs.veranyan.Main"   
    ```
# User Flow Diagram

### main menu

```mermaid
flowchart
    subgraph main menu 
        entrance[[entrance screen]]
        entrance == enter ==> login[[log-in screen]]
        entrance == quit ==> exit{exit system}
        
        login ==> options[menu \n option \n selection]
        
        options == update profile ==> updateScreen[[update screen]] ==> options
        options == find route ==> pathFinder[[path finder]] ==> options
        options == add activity ==> activityInsertionScreen[[activity insertion screen]] ==> options
        options == display feed ==> feedScreen[[feed screen]] ==> options
        options == quit ==> entrance
            
    end
```

### log-in and sign-up

```mermaid
flowchart
    subgraph log-in and sign-up 
        login[[log-in screen]]
        login == sign up ==> profileInsertion
        login == log in ==> checkExistence
        
        %% signup        
        profileInsertion[profile insertion]
        
        profileInsertion == candidate profile ==> createProfile
        
        createProfile{create profile}
        
        createProfile -. profile already exists .-> profileInsertion
        
        createProfile -. profile created .-> loadProfile
        
        %% login
        checkExistence{check profiles exist}
        
        checkExistence -. no profile in system .-> login
        
        checkExistence -. profiles exist .-> profileSelection
        
        profileSelection[profile selection]
        
        profileSelection == selected profile ==> loadProfile

        loadProfile{load profile}
        
        loadProfile -. profile loaded .-> mainMenu
       
        mainMenu[[main menu]]
    end
```

### update profile

#### Note: 
> Error outputs for checking if there are profiles to follow or if profile has friends
> do not go back to the previous subtask but to the ending subtask so that the user is not
> left with the only available choice being to select the other 2 options, while it was not
> the original intention.

```mermaid
flowchart 
    subgraph update profile
        updateScreen[[update screen]]
        
        updateScreen == add gear ==> gearInsertion
        
        updateScreen == follow ==> checkIfCanFollow
        
        updateScreen == unfollow ==> checkIfHasFriends
        
        %% add gear
        
        gearInsertion[gear insertion]
        
        gearInsertion == gear data ==> addGear

        addGear{create and \n save gear}
        
        addGear -. duplicate gear name .-> gearInsertion
        
        addGear -. gear saved .-> mainMenu
        
        %% follow
        
        checkIfCanFollow{check if \n has profiles \n to follow}
        
        checkIfCanFollow -. no one \n left to follow .-> mainMenu
        
        checkIfCanFollow -. has profiles to follow .-> profileSelection
        
        profileSelection[profile selection]
        
        %% unfollow

        checkIfHasFriends{check if \n has friends}
        
        checkIfHasFriends -. there are \n no friends .-> mainMenu
        
        checkIfHasFriends -. has friends .-> profileSelection
        
        profileSelection == selected profile ==> changeConnection{change \n connection \n with profile}
        
        changeConnection -. can't un/follow self or \n already un/followed .-> profileSelection 
        
        changeConnection -. connection changed .-> mainMenu[[main menu]]
    end
```

### insert activity

```mermaid
flowchart TD
subgraph insert activity
    activityInsertionScreen[[activity insertion screen]]
    
    activityInsertionScreen ==> determineGearAvailability
    
    determineGearAvailability{check gear\n available}
    
    determineGearAvailability -. no gear available .-> mainMenu
    
    determineGearAvailability -. gear available .-> gearSelection
    
    gearSelection[gear selection]

    gearSelection == gear ==> saveGear{save gear}
    
    saveGear -. gear saved .-> routeConstructionTypeSelection[route construction \n type selection]
    
    %% route creation
    routeConstructionTypeSelection == create route ==> routeInsertion[[route insertion screen]]
    
    routeInsertion == route ==> saveRoute
    
    %% route selection
    routeConstructionTypeSelection == select route ==> determineRouteAvailability{check route\n available}

    determineRouteAvailability -. no routes available .-> routeConstructionTypeSelection

    determineRouteAvailability -. route available .-> routeSelection[route \n selection]
    
    routeSelection == route ==> saveRoute
    
    %% final processing
    saveRoute{save route}
    
    saveRoute -. route saved .-> obstacleCreationChoiceInsertion
    
    %% obstacle insertion
    obstacleCreationChoiceInsertion[obstacle creation \n choice insertion]
    
    obstacleCreationChoiceInsertion == insert obstacle ==> obstacleInsertion[obstacle insertion]

    obstacleCreationChoiceInsertion == skip ==> mainMenu[main menu]
    
    obstacleInsertion == obstacle data ==> saveObstacle{create and \n save obstacle}

    saveObstacle -. overlap with route \n or out of bounds .-> obstacleInsertion
    
    saveObstacle -. obstacle saved .-> mainMenu[[main menu]]
    
end
```

### insert route

```mermaid
flowchart
    subgraph insert route
        routeInsertionScreen[[route  insertion]]

        routeInsertionScreen == route data ==> validateRoute

        validateRoute{create \n route}
        
        validateRoute -. route created .-> moveInsertion[move insertion]

        validateRoute -. overlap with \n obstacle or \n out of bounds .-> routeInsertionScreen

        moveInsertion == direction and steps ==> applyMove{apply\n move}

        applyMove -. overlap with \n obstacle or \n out of bounds .-> moveInsertion

        applyMove -. moved .-> continuationChoice[continuation choice \n insertion]

        continuationChoice == yes ==> moveInsertion

        continuationChoice == no ==> activityInsertionScreen[[activity insertion screen]]
    end
```

### feed

```mermaid
flowchart
subgraph feed screen 
    feedScreen[[feed screen]] ==> displayFeed
    
    displayFeed{display \n feed} -. feed displayed \n with default filter .-> navigationSelection[navigation selection]
    
    navigationSelection == quit ==> mainMenu[[main menu]]
    
    navigationSelection == filter ==> filterSelection[filter selection]
    
    filterSelection == selected filter ==> setFilter{set filter}
    
    setFilter -. filter set .-> displayFeed
end
```

### path finder

```mermaid
flowchart
subgraph path finder screen
    pathFinderScreen[[path finder screen]] == include friends \n or not ==> fillMap{include \n routes in map}
    
    fillMap -. map filled with \n routes to \n find from .-> startEndInsertion

    startEndInsertion[start and end point \n insertion]
    
    startEndInsertion == start and end coordinates ==> findPath{find path}
    
    findPath -. start and/or end coordinate out of bounds .-> startEndInsertion
    
    findPath -. route found .-> updateMap{fill map with \n route found} 
    
    updateMap -. map updated .-> mainmenu[[main menu]]

    findPath -. route not found .-> mainmenu
    
    
end
```

# Domain Model Diagram

## Resources
* used the following existing system to come up with classes in the domain model: <https://www.strava.com/>
* found information about bike types: <https://www.edinburghbicycle.com/info/blog/types-of-bikes-buying-guide>

Here's my updated diagram for my domain model:

> changes:
> 
> ### Profile
> 1. Exerciser class renamed Profile
> 2. Removed the map attribute, thus removing the composition relationship between Profile and Map
> 3. Removed accessors and mutators for the map attribute
> 4. Added name, gears, activities, and friends attributes to Profile
> 5. Thus added a composition relationship between Profile and Activities, and an aggregation relationship with itself
> 6. Added relevant accessors and mutators for the new attributes
> 7. Added getRoutes() method
> 8. Added follow() and unfollow() methods
> 9. Added getNumbOfSteps() method
> 10. changed method addGear(GearType, String, double) to addGear(Gear)  
> 11. changed invariants accordingly
> 
> ### Map
> 1. made width and length attributes static
> 2. Added grid attribute, thus adding a composition relationship between Map and CoordinateType
> 3. Added grid appending / refilling / clearing methods
> 4. Added grid per coordinate mutators
> 5. Changed Map::addObstacle(int, int, int, int) void to Map::addObstacle(Obstacle) void
> 6. Replaced activities with routes attribute, thus removing the composition relationship between Map and Activities and adding an aggregation relationship between Map and Routes
> 7. Removed accessors and mutators for activities
> 8. Removed isInRoute(), isInObstacle() methods
> 9. Removed getNumbOfSteps() method
> 10. Added accessors and mutators for routes and processed routes
> 11. changed invariants accordingly
> 
> ### Activity
> 1. removed the avgSpeed attribute
> 2. removed Activity::endActivity() method
> 3. changed invariants accordingly
> 
> ### Route
> 1. made Route implement MapFeature
> 2. replaced getStepsAmount() with getMeasure()
> 3. removed getCoordinate() method
> 4. added addCoordinate() method
> 5. changed invariants accordingly
> 
> ### Obstacle
> 1. removed topLeftCoord and bottomRightCoord attributes
> 2. added coordinates attribute
> 3. made Obstacle implement MapFeature
> 4. added GetCoordinates() and getMeasure() methods
> 5. changed invariants accordingly
> 
> ### Coordinate
> 1. added type attribute, thus adding a composition relationship between Coordinate and CoordinateType
> 2. added getLeft(), getRight(), getAbove(), getBelow(), getNeighbours(), and isNeighbourOf() methods
> 3. changed invariants accordingly
> 
> ### Additional
> 1. added a MapFeature interface that Route and Obstacle implement
> 2. added a CoordinateType class
> 

```mermaid 
classDiagram
    class Profile {
        -String name
        -SortedSet~Gear~ gears
        -SortedSet~Activity~ activities
        -SortedSet~Profile~ friends
        
        +getName() String
        +getGears() SortedSet~Gear~
        +getGear(String) Gear
        +addGear(Gear) void
        +removeGear(Gear) void
        +getTotalNumSteps(LocalDate, ChronoUnit) int
        +getActivities(int, int) SortedSet~Activity~
        +addActivity(Activity) void
        +removeActivity(int) void
        +getFriends() Set~Profile~
        +follow(Profile) void
        +unfollow(Profile) void
        +getRoutes() List~ProcessedRoute~
    }
    
    note for Profile"invariants:
        * name != null
        * name.length >= 1
        * gears != null
        * gears.length >= 1
        * loop: no entry is null in gears
        * activities != null
        * loop: no entry is null in activities
        * friends != null
        * loop: no entry is null in friends
        "
    
    Profile --o Profile
    Profile --* Gear
    Profile --* Activity
    
    class Gear {
        -GearType type
        -String name
        -double avgSpeed
    }
    
    note for Gear"invariants:
        * type != null
        * name != null
        * name.length() >= 1
        * avgSpeed > 0"
    
    Gear --* GearType
    
    class GearType {
        <<enumeration>>
        ROAD_BIKE,
        MOUNTAIN_BIKE,
        COMMUTER_BIKE,
        ELECTRIC_BIKE,
        TANDEM_BIKE
    }
    
    class Map {
        -CoordinateType[][] grid
        -List~Obstacle~ obstacles
        -Set~Route~ routes
        
        +getWidth() int
        +getLength() int
        +getCoordinateType(int, int) CoordinateType
        +setCoordinateType(int, int) void
        +getObstacles() List~Obstacle~
        +addObstacle(Obstacle) void
        +removeObstacle(int) void
        +addRoute(Route) ProcessedRoute
        +addProcessedRoute(ProcessedRoute) void
        +addProcessedRoutes(List~ProcessedRoute~) void
        +clearRoutes() void
        +appendToGrid(MapFeature) void
        +refillGrid() void
    }
    
    note for Map"invariants:
        * grid != null
        * obstacles != null
        * routes != null
        * loop: no entry is null in grid
        * loop: no entry is null in obstacles
        * loop: no obstacle is out of bounds
        * loop: no entry is null in routes
        * loop: no route is out of bounds
        * routes and obstacles don't overlap"

    Map --* CoordinateType
    Map --* Obstacle
    Map --o Route
    
    class Activity{
        -Gear gear
        -LocalDateTime start
        -LocalDateTime end
        -ProcessedRoute route
        
        +getAvgSpeed() double
        +getStart() LocalDateTime
        +getEnd() LocalDateTime
        +getGear() Gear
        +getRoute() Route
    }
    
    note for Activity"invariants:
        * gear != null
        * start != null
        * end != null
        * route != null
        * start is before end"
    
    Activity --o Gear
    Activity --* Route
    
    class Route {
        -List~Coordinate~ coordinates
        
        +getCoordinates() List~Coordinate~
        +getMeasure() int
        +addCoordinate(Coordinate) void
        +move(int, int) void
        +contains(int, int) boolean
    }
    
    note for Route"invariants:
        * coordinates != null
        * coordinates.size() >= 1
        * loop: no entry is null in coordinates
        * loop: all entries in coordinates is of type ROUTE"

    Route ..|> MapFeature
    Route --* Coordinate
    
    class Obstacle {
        -List~Coordinate~ coordinates
        
        +getCoordinates() List~Coordinate~
        +contains(int, int) boolean
        +getMeasure() int
    }
    
    note for Obstacle"invariants:
        * coordinates != null
        * coordinates.size() >= 1
        * no entry is null in coordinates
        * loop: all entries in coordinates is of type OBSTACLE"
    
    Obstacle ..|> MapFeature
    Obstacle --* Coordinate
    
    class MapFeature {
        <<interface>>
        +getCoordinates() List~Coordinate~;
        getMeasure() int
        contains(int, int) boolean
    }
    
    class Coordinate {
        -CoordinateType type
        -int x
        -int y
        
        +getLeft() Coordinate
        +getRight() Coordinate
        +getAbove() Coordinate
        +getBelow() Coordinate
        +getNeighbours() Coordinate[]
        +isNeighbourOf(Coordinate) boolean
    }
    
    note for Coordinate "invariants:
        type != null
    "
    
    Coordinate --* CoordinateType
    
    class CoordinateType {
        <<enumeration>>
        EMPTY,
        ROUTE,
        OBSTACLE,
        VISITED,
        CURRENT,
        BORDER
    }
```