package ttt;

import java.util.concurrent.CompletableFuture;

public class TestService {
    public CompletableFuture<String> testMethod(String s, int i) {
        return CompletableFuture.completedFuture(s);
    }
}
