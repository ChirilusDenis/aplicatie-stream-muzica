package entities;

import java.util.ArrayList;

public final class Subject {
    private ArrayList<User> users = new ArrayList<>();

    /** updates all subscribed users **/
    public void notifyAll(final String name, final String description) {
        for (User user : users) {
            user.updateNotif(name, description);
        }
    }

    /** removes a user from the to-be-notified list **/
    public void removeUser(final User user) {
        users.remove(user);
    }

    /** adds a user to the to-be-notified list **/
    public void addUser(final User user) {
        users.add(user);
    }
}
