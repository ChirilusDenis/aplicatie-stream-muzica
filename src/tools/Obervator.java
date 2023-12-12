package tools;

import lombok.Getter;
import lombok.Setter;
import observer.Observer;

import java.util.ArrayList;

@Getter @Setter
public class Obervator {
    private ArrayList<Observer> users = new ArrayList<>();

    public void notifyAll(int crtTime) {
        for (Observer observer : users) {
            observer.update(crtTime);
        }
    }
}
