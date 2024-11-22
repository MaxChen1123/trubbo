package clusterTest.ttt;

import java.util.concurrent.CompletableFuture;

public interface TestService {
    CompletableFuture<String> testMethodAsync(String s, int i);

    String testMethod(String s, int i);

    User testUser(int id);

    CompletableFuture<User> testUserAsync(int id);
}

