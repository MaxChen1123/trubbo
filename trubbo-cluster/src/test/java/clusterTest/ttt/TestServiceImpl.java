package clusterTest.ttt;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TestServiceImpl implements TestService {
    private static final Map<Integer, User> DATABASE = new HashMap<>() {{
        put(1, new User(1L, "Jack", new ArrayList<>(List.of("1", "2"))));
        put(2, new User(2L, "Mike", new ArrayList<>(List.of("3", "4"))));
        put(3, new User(3L, "Lily", new ArrayList<>(List.of("5", "6"))));
    }};

    public CompletableFuture<String> testMethodAsync(String s, int i) {
        return CompletableFuture.completedFuture(s);
    }

    public String testMethod(String s, int i) {
        return s;
    }

    @Override
    public User testUser(int id) {
        return DATABASE.get(id);
    }

    @Override
    public CompletableFuture<User> testUserAsync(int id) {
        return CompletableFuture.supplyAsync(() -> DATABASE.get(id));
    }

    @Override
    public void testTimeout(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int testException(int i) {
        throw new RuntimeException("test exception");
    }

    @Override
    @SneakyThrows
    public void testOneWay() {
        System.out.println("one way method start at: " + System.currentTimeMillis());
        Thread.sleep(5000);
        System.out.println("one way method end at: " + System.currentTimeMillis());
    }
}