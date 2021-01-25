package com.absys.test.service;

import com.absys.test.model.Criminal;
import com.absys.test.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.absys.test.model.UserState;

@Service
public class UserService {

    @Autowired
    private SimpMessagingTemplate webSocketTemplate;

    @SuppressWarnings("serial")
    private List<User> memoryDatabase = new LinkedList<User>() {
        {
            add(new User("SFES45", "JEAN", "DUPONT", new Date(), "FRANCE", "FARMER"));
            add(new User("SFES43", "JOE", "BIDEN", new Date(), "USA", "PRESIDENT"));
            add(new User("SFES39", "JOHN", "WAYNE", new Date(), "USA", "FARMER"));
            add(new User("SFES41", "BARACK", "OBAMA", new Date(), "USA", "PRESIDENT"));
            add(new User("SFES44", "MARC", "DORCEL", new Date(), "FRANCE", "FARMER"));
            add(new User("SFES40", "JACQUES", "CHIRAC", new Date(), "FRANCE", "PRESIDENT"));
            add(new User("SFES42", "DONALD", "TRUMP", new Date(), "USA", "PRESIDENT"));
        }
    };
    private List<Criminal> earthCriminalDatabase = Criminal.earthCriminal();

    /**
     * Create an ID and a user then return the ID
     * 
     * @param user
     * @return
     */
    public User createUser(User user) {
        try {
            // generate key
            String key = generateId();
            user.setId(key);
            memoryDatabase.add(user);
            // notify
            webSocketTemplate.convertAndSend("/workflow/states", user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("Error has occured");
        }

    }

    public List<User> findAll() {
        return memoryDatabase;
    }

    /**
     *
     * @param userid
     * @return
     */
    public User workflow(String userid) {
        // fetch user from memory database
        // what to return if user can't found ?
        User user = login(userid);

        // next step on workflow
        // CREATED -> EARTH_CONTROL -> MARS_CONTROL -> DONE
        // Check criminal list during "EARTH_CONTROL" state, if the user is in the list,
        // set state to REFUSED
        switch (user.getState()) {
            case CREATED: {
                user.setState(UserState.EARTH_CONTROL);
                break;
            }
            case EARTH_CONTROL: {
                List<Criminal> criminal = earthCriminalDatabase.stream()
                        .filter(item -> (item.getFirstname().equals(user.getFirstname())
                                && item.getLastname().equals(user.getLastname())))
                        .collect(Collectors.toList());

                // Ask to client : what to do if registered user isn't in Earth criminal list ?
                if (criminal.size() == 0)
                    user.setState(UserState.MARS_CONTROL);
                else
                    user.setState(criminal.get(0).isNotAllowedToMars() ? UserState.REFUSED : UserState.MARS_CONTROL);
                break;
            }
            case MARS_CONTROL: {
                // Check if user already existing based on : firstname, lastname, birthday
                List<User> users = memoryDatabase.stream()
                        .filter(item -> (item.getFirstname().equals(user.getFirstname())
                                && item.getLastname().equals(user.getLastname())
                                && item.getBirthday().equals(user.getBirthday())
                                && item.getState().equals(UserState.DONE)))
                        .collect(Collectors.toList());
                if (users.size() == 0)
                    user.setState(UserState.DONE);
                else
                    user.setState(UserState.REFUSED);
                break;
            }
            case DONE:
                break;
            default:
                break;
        }

        // send update to all users
        webSocketTemplate.convertAndSend("/workflow/states", user);
        return user;
    }

    /**
     * Return all user group by its job then its country
     * 
     * @return
     */
    public Object findByJobThenCountry() {
        // Return an Object containing user sort by Job then Country (you are not
        // allowed to just return List<User> sorted)
        // Why not use List<User> type ??
        List<User> sortedUsers = new ArrayList<User>();

        // Sort by earth job (copy without reference to not alter original database)
        List<User> memoryDatabaseTmp = new ArrayList<User>(memoryDatabase);
        memoryDatabaseTmp.sort((o1, o2) -> o1.getEarthJob().compareTo(o2.getEarthJob()));

        // Divide database into several lists for each job
        Map<String, List<User>> jobUserLists = memoryDatabaseTmp.stream().collect(Collectors.groupingBy(User::getEarthJob));

        // Sort job databases by country and reunite job database
        for(List<User> jobUserList : jobUserLists.values()) {
            jobUserList.sort((o1, o2) -> o1.getEarthCountry().compareTo(o2.getEarthCountry()));
            sortedUsers = Stream.concat(sortedUsers.stream(), jobUserList.stream())
                             .collect(Collectors.toList());
        }

        // Reunite job databases
        return sortedUsers;
    }

    /**
     * Find the user in the memory database by its ID
     * 
     * @param userid
     * @return
     */
    public User login(String userid) {
        // fetch user from memory database
        List<User> users = memoryDatabase.stream().filter(item -> item.getId().equals(userid))
                .collect(Collectors.toList());
        // what to return if user can't found ?
        if (users.size() == 0)
            return null;
        ;
        return users.get(0);
    }

    // Generate MARS-51*2 ID (4 chars + 2 numbers)
    private String generateId() {
        String id = "";

        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        final char[] numbers = "0123456789".toCharArray();

        final Random random = new Random();
        for (int i = 0; i < 6; i++) {
            id = id + (i < 4 ? chars[random.nextInt(chars.length)] : numbers[random.nextInt(numbers.length)]);
        }

        return id;
    }
}
