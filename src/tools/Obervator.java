package tools;

import lombok.Getter;
import lombok.Setter;
import observer.Observer;

import java.util.ArrayList;

@Getter @Setter
public final class Obervator {
    private ArrayList<Observer> users = new ArrayList<>();

    /** notifies all users to update their players */
    public void notifyAll(final int crtTime) {
        for (Observer observer : users) {
            observer.update(crtTime);
        }
    }
}
