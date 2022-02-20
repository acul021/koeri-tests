package edu.kit.informatik;

import edu.kit.informatik.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DependencyTest {

    @Test
    public void testIndependence() {
        Network net = net("(0.0.0.0 1.1.1.1 2.2.2.2 5.5.5.5)");
        Network net2 = net("(2.2.2.2 3.3.3.3)");
        Network net3 = net("(3.3.3.3 4.4.4.4)");
        Network net4 = net("(6.6.6.6 7.7.7.7)");

        assertTrue(() -> net2.add(net3));
        assertEquals("(2.2.2.2 (3.3.3.3 4.4.4.4))", net2.toString(ip("2.2.2.2")));
        assertTrue(() -> net.add(net2));
        assertEquals("(0.0.0.0 1.1.1.1 (2.2.2.2 (3.3.3.3 4.4.4.4)) 5.5.5.5)", net.toString(ip("0.0.0.0")));
        assertTrue(() -> net2.disconnect(ip("3.3.3.3"), ip("2.2.2.2")));
        assertEquals("(0.0.0.0 1.1.1.1 (2.2.2.2 (3.3.3.3 4.4.4.4)) 5.5.5.5)", net.toString(ip("0.0.0.0")));
        assertEquals(net3.toString(ip("3.3.3.3")), net2.toString(ip("3.3.3.3")));
        assertEquals(net3.toString(ip("4.4.4.4")), net2.toString(ip("4.4.4.4")));
        assertTrue(() -> net.add(net4));
        assertTrue(() -> net.connect(ip("7.7.7.7"), ip("5.5.5.5")));
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
