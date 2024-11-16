package ttt;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
public class User {
    long id;
    String name;
    List<String> list;
}