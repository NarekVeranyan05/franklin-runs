package ca.umanitoba.cs.veranyan.persistence.json;

import ca.umanitoba.cs.veranyan.logic.MapManager;
import ca.umanitoba.cs.veranyan.logic.exceptions.RouteObstacleOverlapException;
import ca.umanitoba.cs.veranyan.model.Activity;
import ca.umanitoba.cs.veranyan.model.Profile;
import ca.umanitoba.cs.veranyan.model.exceptions.*;
import ca.umanitoba.cs.veranyan.model.gear.Gear;
import ca.umanitoba.cs.veranyan.model.gear.GearType;
import ca.umanitoba.cs.veranyan.model.map.Map;
import ca.umanitoba.cs.veranyan.model.map.Route;
import ca.umanitoba.cs.veranyan.model.map.coordinate.Coordinate;
import ca.umanitoba.cs.veranyan.model.map.coordinate.CoordinateType;
import ca.umanitoba.cs.veranyan.persistence.ProfilePersistence;
import com.google.common.base.Preconditions;

import javax.json.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * {@link ProfilePersistenceJson} is the Json implementation for {@link ProfilePersistence}
 */
public class ProfilePersistenceJson implements ProfilePersistence {
    private final Path profileStorage;

    public ProfilePersistenceJson(Path profileStorage) {
        this.profileStorage = profileStorage;

        checkProfilePersistenceJson();
    }

    @Override
    public void save(Profile profile) {
        Preconditions.checkNotNull(profile, "profile cannot be null");
        checkProfilePersistenceJson();

        // loading all profiles, adding new profile, and saving
        Set<Profile> profiles = loadProfiles();
        profiles.removeIf(existingProfile -> existingProfile.getName().equals(profile.getName()));
        profiles.add(profile);
        saveProfiles(profiles);

        checkProfilePersistenceJson();
    }

    /**
     * Saves multiple profiles into the system
     * @param profiles the profiles to save
     */
    private void saveProfiles(Collection<Profile> profiles){
        Preconditions.checkNotNull(profiles, "profiles cannot be null");
        checkProfilePersistenceJson();

        try {
            JsonWriter writer = Json.createWriter(Files.newOutputStream(profileStorage));
            JsonObjectBuilder profilesJson = Json.createObjectBuilder();

            for (Profile profile : profiles) {
                JsonObjectBuilder profileJson = Json.createObjectBuilder();

                // adding gears, activities, and friends
                profileJson.add("gears", gearsToJson(profile.getGears()));
                profileJson.add("activities", activitiesToJson(profile.getActivities()));
                profileJson.add("friends", friendsToJson(profile.getFriends()));

                profilesJson.add(profile.getName(), profileJson.build());
            }

            writer.writeObject(profilesJson.build());
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

        checkProfilePersistenceJson();
    }

    /**
     * Converts the friends of the profile to save into json
     * @param friends the friend profiles of the profile to save
     * @return json array of friend names
     */
    private JsonArray friendsToJson(Set<Profile> friends) {
        Preconditions.checkNotNull(friends, "friends cannot be null");
        checkProfilePersistenceJson();

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(var friend : friends){
            builder.add(friend.getName());
        }

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Converts the activities of the profile to save into json
     * @param activities the activities of the profile to save
     * @return json array of activities
     */
    private JsonArray activitiesToJson(SortedSet<Activity> activities) {
        Preconditions.checkNotNull(activities, "activities cannot be null");
        checkProfilePersistenceJson();

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(var activity : activities){
            JsonObjectBuilder activityJson = Json.createObjectBuilder()
                    .add("gear", activity.getGear().getName())
                    .add("year", activity.getStart().getYear())
                    .add("month", activity.getStart().getMonthValue())
                    .add("dayOfMonth", activity.getStart().getDayOfMonth())
                    .add("hour", activity.getStart().getHour())
                    .add("minute", activity.getStart().getMinute())
                    .add("duration",
                            ChronoUnit.MINUTES.between(activity.getStart(), activity.getEnd()))
                    .add("route", routeToJson(activity.getRoute()));

            builder.add(activityJson);
        }

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Converts the route of an activity into json
     * @param route the route to convert to json
     * @return json array of route coordinates
     */
    private JsonObject routeToJson(Route route) {
        Preconditions.checkNotNull(route, "route cannot be null");
        checkProfilePersistenceJson();

        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder coordinates = Json.createArrayBuilder();

        for(var coord : route.getCoordinates()){
            coordinates.add(
                    Json.createObjectBuilder().add("x", coord.x()).add("y", coord.y())
            );
        }

        builder.add("coordinates", coordinates);

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Converts the gears of the profile to save into json
     * @param gears the gears to convert to json
     * @return json array of converted gears
     */
    private JsonArray gearsToJson(SortedSet<Gear> gears) {
        Preconditions.checkNotNull(gears, "gears cannot be null");
        checkProfilePersistenceJson();

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(var gear : gears){
            JsonObjectBuilder gearJson = Json.createObjectBuilder()
                    .add("type", gear.getType().toString())
                    .add("name", gear.getName())
                    .add("avgSpeed", gear.getAvgSpeed());

            builder.add(gearJson);
        }

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Loads all profiles into the system
     * @return the set of loaded profiles
     */
    @Override
    public Set<Profile> loadProfiles(){
        checkProfilePersistenceJson();

        var profiles = new HashMap<String, Profile>();

        if(Files.exists(profileStorage) && new File(String.valueOf(profileStorage)).length() != 0){
            try{
                var reader = Json.createReader(Files.newInputStream(profileStorage));
                var profilesJson = reader.readObject();

                // building profiles in the registry
                for (var profileName : profilesJson.keySet()){
                    var profileJson = profilesJson.getJsonObject(profileName);

                    var profile = new Profile.ProfileBuilder().name(profileName).build();

                    java.util.Map<String, Gear> gears = new HashMap<>();

                    // gears of the profile
                    for(JsonValue gearJson : profileJson.getJsonArray("gears")){
                        var gear = gearFromJson(gearJson.asJsonObject());
                        gears.put(gear.getName(), gear);
                        profile.addGear(gear);
                    }

                    // activities of the profile
                    for(JsonValue activityJson : profileJson.getJsonArray("activities")){
                        profile.addActivity(activityFromJson(gears, activityJson.asJsonObject()));
                    }

                    profiles.put(
                            profileName,
                            profile
                    );
                }

                // refilling friends of profiles
                for(String profileName : profilesJson.keySet()){
                    var profileJson = profilesJson.getJsonObject(profileName);

                    Profile currentProfile = profiles.get(profileName);

                    for(JsonValue friendJson : profileJson.getJsonArray("friends")){
                        String friendName = ((JsonString) friendJson).getString();

                        if(profilesJson.containsKey(friendName)){
                            currentProfile.follow(
                                    profiles.get(friendName)
                            );
                        }
                    }
                }

            } catch (RuntimeException | IOException | BlankNameException | NonPositiveSpeedException |
                     InvalidTimeRangeException | CoordinateOutOfBoundsException | DuplicateActivityException |
                     DuplicateGearException | CannotFollowSelfException | CannotFollowAgainException |
                     InvalidDurationException | RouteObstacleOverlapException e) {
                throw new RuntimeException(e);
            }
        }

        checkProfilePersistenceJson();

        return new HashSet<>(profiles.values());
    }

    /**
     * Converts the json representation of a {@link Gear} into a {@link Gear} object
     * @param gearJson the json representation of the {@link Gear}
     * @return the converted {@link Gear} object
     * @throws BlankNameException if the gear as json had blank name
     * @throws NonPositiveSpeedException if the gear as json had non-positive speed
     */
    private Gear gearFromJson(JsonObject gearJson) throws BlankNameException, NonPositiveSpeedException {
        Preconditions.checkNotNull(gearJson, "gearJson cannot be null");
        checkProfilePersistenceJson();

        Gear.GearBuilder builder = new Gear.GearBuilder();

        builder.type(GearType.valueOf(gearJson.getString("type")))
            .name(gearJson.getString("name"))
            .avgSpeed(gearJson.getInt("avgSpeed"));

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Converts the json representation of am {@link Activity} into an {@link Activity} object
     * @param gears the json array representation of {@link Gear} objects
     * @param activityJson the json representation of the {@link Activity}
     * @return the converted {@link Gear} object
     * @throws InvalidTimeRangeException if the activity as json had invalid time range (e.g. month = 13)
     * @throws CoordinateOutOfBoundsException if the {@link Route} of the {@link Activity} was out of bounds of the {@link Map}
     */
    private Activity activityFromJson(java.util.Map<String, Gear> gears, JsonObject activityJson) throws InvalidTimeRangeException, CoordinateOutOfBoundsException, InvalidDurationException, RouteObstacleOverlapException {
        Preconditions.checkNotNull(gears, "gears cannot be null");
        Preconditions.checkNotNull(activityJson, "activityJson cannot be null");
        checkProfilePersistenceJson();

        var builder = new Activity.ActivityBuilder();

        builder.gear(gears.get(activityJson.getString("gear")))
            .startMonth(
                activityJson.getInt("month")
            )
            .startDayOfMonth(
                activityJson.getInt("dayOfMonth")
            )
            .startHour(
                activityJson.getInt("hour")
            )
            .startMinute(
                activityJson.getInt("minute")
            )
            .durationInMinutes(
                activityJson.getInt("duration")
            )
            .route(
                routeFromJson(activityJson.getJsonObject("route"))
            );

        checkProfilePersistenceJson();

        return builder.build();
    }

    /**
     * Converts the json representation of a {@link Route} into an object
     * @param routeJson the json representation of a {@link Route}
     * @return the converted {@link Route} object
     * @throws CoordinateOutOfBoundsException if the {@link Route} was out of bounds.
     */
    private Route routeFromJson(JsonObject routeJson) throws CoordinateOutOfBoundsException {
        Preconditions.checkNotNull(routeJson, "routeJson cannot be null");
        checkProfilePersistenceJson();

        var builder = new Route.RouteBuilder();
        var coordinates = routeJson.getJsonArray("coordinates");

        for(int i = 0; i < coordinates.size(); i++){
            builder.withCoordinate(
                new Coordinate(
                    CoordinateType.ROUTE,
                    coordinates.getJsonObject(i).getInt("x"),
                    coordinates.getJsonObject(i).getInt("y")
                )
            );
        }

        checkProfilePersistenceJson();

        return builder.build();
    }

    private void checkProfilePersistenceJson(){
        Preconditions.checkNotNull(profileStorage, "profileStorage cannot be null");
    }
}
