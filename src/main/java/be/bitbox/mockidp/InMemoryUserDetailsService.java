package be.bitbox.mockidp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    private final Map<String, User> users = new HashMap<>();
    private final PasswordEncoder encoder;

    public InMemoryUserDetailsService(PasswordEncoder encoder) throws IOException {
        this.encoder = encoder;

        ObjectMapper o = new ObjectMapper();
        User[] loaded = o.readValue(this.getClass().getResource("/users.json"), User[].class);
        for (User user : loaded) {
            user.setPassword(encoder.encode(user.getPassword()));
            users.put(user.getUser(), user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        User get = users.get(string);
        if (get == null) {
            throw new UsernameNotFoundException("didn't find " + string);
        }
        return get;
    }

    public void put(User u) {
        users.put(u.getUsername(), u);
    }

    public Collection<User> get() {
        return users.values();
    }
}