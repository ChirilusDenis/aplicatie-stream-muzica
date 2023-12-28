package tools;

import entities.Artist;
import entities.Host;
import entities.User;

public final class UserFactory {

    public User createUser (Command cmd) {
        switch (cmd.getType()) {
            case "user":
                User user = new User(cmd.getUsername(), cmd.getCity(), cmd.getAge());
                DataBase.getDB().getNewUsers().add(user);
                return user;
            case "artist":
                Artist artist = new Artist(cmd.getUsername(), cmd.getCity(), cmd.getAge());
                DataBase.getDB().getArtists().add(artist);
                return artist;
            case "host":
                Host host = new Host(cmd.getUsername(), cmd.getCity(), cmd.getAge());
                DataBase.getDB().getHosts().add(host);
                return host;
        }
        return null;
    }
}
