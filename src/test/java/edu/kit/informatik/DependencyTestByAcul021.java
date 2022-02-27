package edu.kit.informatik;

import edu.kit.informatik.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SameParameterValue")
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

    @Test
    void constructWithRootAndChildren() {
        List<IP> ips = ip("1.1.1.1", "2.2.2.2");
        Network net = new Network(ip("0.0.0.0"), ips);
        assertIterableEquals(ips("0.0.0.0", "1.1.1.1", "2.2.2.2"), net.list());
        assertEquals("(0.0.0.0 1.1.1.1 2.2.2.2)", net.toString(ip("0.0.0.0")));
        ips.addAll(ips("3.3.3.3", "4.4.4.4", "0.0.0.0"));
        ips.remove(ip("1.1.1.1"));
        assertIterableEquals(ips("0.0.0.0", "1.1.1.1", "2.2.2.2"), net.list());
        assertEquals("(0.0.0.0 1.1.1.1 2.2.2.2)", net.toString(ip("0.0.0.0")));
    }

    @Test
    void testAdd() {
    }

    @Test
    void testList() {
        Network net = net("(0.0.0.0 1.1.1.1 2.2.2.2)");
        List<IP> res = net.list();
        mightThrow(UnsupportedOperationException.class,
            () -> res.addAll(ip("0.0.0.0", "1.1.1.1", "2.2.2.2", "3.3.3.3", "4.4.4.4")));
        assertIterableEquals(ip("0.0.0.0", "1.1.1.1", "2.2.2.2"), net.list());
        mightThrow(UnsupportedOperationException.class, () -> net.list().remove(0));
        assertIterableEquals(ip("0.0.0.0", "1.1.1.1", "2.2.2.2"), net.list());

    }

    @Test
    void testLevels() {
        Network net = net("(0.0.0.0 1.1.1.1 2.2.2.2 3.3.3.3 4.4.4.4 (5.5.5.5 6.6.6.6 (7.7.7.7 8.8.8.8)))");
        List<List<IP>> levels = net.getLevels(ip("8.8.8.8"));
        assertIterableEquals(List.of(ips("8.8.8.8"), ips("7.7.7.7"), ips("5.5.5.5"), ip("0.0.0.0", "6.6.6.6"),
            ip("1.1.1.1", "2.2.2.2", "3.3.3.3", "4.4.4.4")), levels);
        mightThrow(UnsupportedOperationException.class,
            () -> levels.add(ip("1.1.1.1", "2.2.2.2", "9.9.9.9", "10.10.10.10")));
        mightThrow(UnsupportedOperationException.class, () -> levels.forEach((l) -> l.remove(0)));
        assertIterableEquals(List.of(ips("8.8.8.8"), ips("7.7.7.7"), ips("5.5.5.5"), ip("0.0.0.0", "6.6.6.6"),
            ip("1.1.1.1", "2.2.2.2", "3.3.3.3", "4.4.4.4")), net.getLevels(ip("8.8.8.8")));
    }

    @Test
    void testGetRoute() {
        Network net = net("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 4.4.4.4))))");
        List<IP> route = net.getRoute(ip("4.4.4.4"), ip("0.0.0.0"));
        assertIterableEquals(ip("4.4.4.4", "3.3.3.3", "2.2.2.2", "1.1.1.1", "0.0.0.0"), route);
        assertEquals("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 4.4.4.4))))", net.toString(ip("0.0.0.0")));
        route.addAll(ip("1.1.1.1", "2.2.2.2", "7.7.7.7"));
        route.remove(ip("0.0.0.0"));
        assertIterableEquals(ip("4.4.4.4", "3.3.3.3", "2.2.2.2", "1.1.1.1", "0.0.0.0"),
            net.getRoute(ip("4.4.4.4"), ip("0.0.0.0")));
        assertEquals("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 4.4.4.4))))", net.toString(ip("0.0.0.0")));
    }

    private void mightThrow(Class<? extends Exception> clazz, Executable executable) {
        try {
            executable.execute();
        } catch (Exception e) {
            assertInstanceOf(clazz, e);
        }
    }

    private Network net(String net) {
        try {
            return new Network(net);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @FunctionalInterface
    private interface Executable {
        void execute() throws Exception;
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
