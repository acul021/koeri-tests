package edu.kit.informatik;

import edu.kit.informatik.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class DependencyTest {

    @Test
    public void testSomething() {

    }


    private Network net(String net) {
        try {
            return new Network(net);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<IP> ips(String... ips) {
        return this.ip(ips);
    }

    private List<IP> ip(String... ips) {
        return Arrays.stream(ips).map(this::ip).collect(Collectors.toList());
    }

    private IP ip(String ip) {
        try {
            return new IP(ip);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
