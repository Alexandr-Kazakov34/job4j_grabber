package ru.job4j.grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver"));
            connection = DriverManager.getConnection(config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password"));

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into post(name, text, link, created) values (?,?,?,?) ON CONFLICT (link) DO NOTHING;")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Post createPost(ResultSet resultSet) throws SQLException {
        return new Post(resultSet);
    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from post;")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rsl.add(createPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(int id) {
        List<Post> all = getAll();
        return all.stream().filter(post -> post.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

   /* public static void main(String[] args) throws Exception {
        Post post = new Post(1, "test", "Some description",
                "https://career.habr.com/companies/holdingt1", LocalDateTime.now());
        Post post2 = new Post(2, "test", "Some description2",
                "https://career.habr.com/resumes", LocalDateTime.now());
        Post post3 = new Post(3, "test", "Some description3",
                "https://career.habr.com/vacancies?type=all", LocalDateTime.now());
        Post post4 = new Post(4, "test", "Some description4",
                "https://career.habr.com/salaries", LocalDateTime.now());
        Properties properties = new Properties();
        try (InputStream inputStream = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PsqlStore psqlStore = new PsqlStore(properties);
        psqlStore.save(post);
        psqlStore.save(post2);
        psqlStore.save(post3);
        psqlStore.save(post4);
        System.out.println(psqlStore.getAll());
        System.out.println(psqlStore.findById(1));
        System.out.println(psqlStore.findById(4));
        psqlStore.close();
    }*/
}