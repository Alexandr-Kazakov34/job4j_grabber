package ru.job4j.gc.leak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserGenerator implements Generate {
    public static final Integer NEW_USERS = 1000;

    public static List<String> names;
    public static List<String> surnames;
    public static List<String> patrons;
    private static List<User> users = new ArrayList<>();
    private Random random;

    public UserGenerator(Random random) {
        this.random = random;
        readAll();
    }

    @Override
    public void generate() {
        users.clear();
        for (int i = 0; i < NEW_USERS; i++) {
            users.add(createUsers(
                    surnames.get(random.nextInt(surnames.size())),
                    names.get(random.nextInt(names.size())),
                    patrons.get(random.nextInt(patrons.size()))));
        }
    }

    private void readAll() {
        try {
            names = read("src/main/java/ru/job4j/gc/leak/files/names.txt");
            surnames = read("src/main/java/ru/job4j/gc/leak/files/surnames.txt");
            patrons = read("src/main/java/ru/job4j/gc/leak/files/patr.txt");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public User randomUser() {
        return users.get(random.nextInt(users.size()));
    }

    public static List<User> getUsers() {
        return users;
    }

    private User createUsers(String surname, String name, String patrons) {
        return new User(surname + " " + name + " " + patrons);
    }
}