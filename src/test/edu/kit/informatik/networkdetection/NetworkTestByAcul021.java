package edu.kit.informatik.networkdetection;

import edu.kit.informatik.networkdetection.exception.ParseException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    private static final String SMALL_NET
        = "(85.193.148.81 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 "
        + "(231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))";

    private static final String SMALL_NET_ALTERNATIVE
        = "(77.135.84.171 (231.189.0.127 39.20.222.120 252.29.23.0 116.132.83.77 "
        + "(85.193.148.81 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239)))";

    private static final String SMALL_NET_ALTERNATIVE_SORTED
        = "(77.135.84.171 (231.189.0.127 39.20.222.120 (85.193.148.81 34.49.145.239 " +
        "(141.255.1.133 0.146.197.108 122.117.67.158)) 116.132.83.77 252.29.23.0))";

    private static final String SMALL_NET_ALTERNATIVE_SORTED_2
        = "(141.255.1.133 0.146.197.108 (85.193.148.81 34.49.145.239 " +
        "(231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0)) 122.117.67.158)";

    private static final String SMALL_NET_SORTED
        = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) "
        + "(231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    private static final String SMALL_NET_EXTENDED
        = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.0.0.0 0.146.197.108 122.117.67.158) "
        + "(231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    private static final String SMALL_NET_EXTENDED_2
        = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.0.0.0 0.146.197.108 2.2.2.2 122.117.67.158) "
        + "(231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    private static final String MEDIUM_NET =
        "(90.240.18.65 (97.22.140.27 (193.77.65.203 206.41.6.234 (137.57.11.178 53.79.153.118 151.175.20.133 72.204.103.14) (172.217.134.246 125.151.42.40 26.135.185.104 12.104.224.21 97.32.83.116)) 27.191.109.156 (207.93.69.7 221.203.203.33 (211.36.119.36 191.214.220.219 7.33.138.146) (126.171.183.35 0.166.201.82 166.114.94.115)) 25.28.90.184) 124.214.225.52 62.116.50.162 (118.255.66.35 228.203.204.177 (71.130.3.224 (131.230.153.36 39.231.53.70 1.77.201.101 13.163.16.235) 166.101.129.76)))";


    @Test
    public void testConstructorWithRootAndChildren() throws ParseException {
        IP root = new IP("192.168.178.65");
        List<IP> invalidIPs = new ArrayList<>(Arrays.asList(new IP("192.168.178.65"), new IP("192.168.178.66")));
        List<IP> IPs = new ArrayList<>(Arrays.asList(new IP("192.168.178.64"), new IP("192.168.178.66")));
        assertThrows(RuntimeException.class, () -> new Network(null, null));
        assertThrows(RuntimeException.class, () -> new Network(root, null));
        assertThrows(RuntimeException.class, () -> new Network(null, IPs));
        assertThrows(RuntimeException.class, () -> new Network(root, invalidIPs));
        assertThrows(RuntimeException.class, () -> new Network(root, ip("0.0.0.0", "0.0.0.0")));

        Network testNetwork = new Network(root, IPs);
        IPs.clear();
        assertNotEquals(0, testNetwork.list().size());
    }

    @Test
    public void testParsingConstructor() throws ParseException {
        Network smallNet = new Network(SMALL_NET);
        assertEquals(SMALL_NET_SORTED, smallNet.toString(ip("85.193.148.81")));
        assertThrows(ParseException.class, () -> new Network(""));
        assertThrows(ParseException.class, () -> new Network(" "));
        assertThrows(ParseException.class, () -> new Network("$%?!§%*ÄÜÖöäü:;-_"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0)"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0 (1.1.1.1 2.2.2.2 )3.3.3.3 4.4.4.4())"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0  1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network(" (0.0.0.0 1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network("( 0.0.0.0 1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network("(0 0.0.0 1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network("(0,0.0.0 1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network("(0:0.0.0 1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0,1.1.1.1)"));
        assertThrows(ParseException.class, () -> new Network(null));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0 0.0.0.0)"));
        assertThrows(ParseException.class, () -> new Network("(1.1.1.1 0.0.0.0 0.0.0.0)"));
        assertThrows(ParseException.class, () -> new Network("(1.1.1.1 (0.0.0.0 2.2.2.2) 0.0.0.0)"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0 (0.0.0.0 1.1.1.1))"));
        assertThrows(ParseException.class, () -> new Network("(1.1.1.1 (2.2.2.2 0.0.0.0) (3.3.3.3 0.0.0.0))"));
        assertThrows(ParseException.class, () -> new Network("(0.0.0.0 (1.1.1.1 0.0.0.0))"));
    }

    @Test
    public void testList() throws ParseException {
        Network net = new Network("(192.168.178.1 192.168.178.0 192.168.178.15 0.0.0.0 29.65.234.123)");

        List<IP> temp = net.list();
        temp.clear();
        assertNotEquals(net.list().size(), temp.size());
        net = new Network("(1.1.1.1 0.0.0.0)");
        assertIterableEquals(ip("0.0.0.0", "1.1.1.1"), net.list());
        net = new Network(
            "(9.9.9.9 (8.8.8.8 (7.7.7.7 (6.6.6.6 (5.5.5.5 (4.4.4.4 (3.3.3.3 (2.2.2.2 (1.1.1.1 0.0.0.0))))))))))");
        assertIterableEquals(
            ip("0.0.0.0", "1.1.1.1", "2.2.2.2", "3.3.3.3", "4.4.4.4", "5.5.5.5", "6.6.6.6", "7.7.7.7", "8.8.8.8",
                "9.9.9.9"),
            net.list());
    }

    @Test
    public void testHeight() throws ParseException {
        Network med = new Network(MEDIUM_NET);
        assertEquals(0, med.getHeight(null));
        assertEquals(0, med.getHeight(ip("0.0.0.0")));
        assertEquals(4, med.getHeight(ip("90.240.18.65")));
        assertEquals(5, med.getHeight(ip("118.255.66.35")));
        assertEquals(6, med.getHeight(ip("71.130.3.224")));
        assertEquals(7, med.getHeight(ip("211.36.119.36")));
        assertEquals(8, med.getHeight(ip("166.114.94.115")));

        Network net = new Network(SMALL_NET);
        assertEquals(0, net.getHeight(null));
        assertEquals(0, net.getHeight(ip("127.0.0.1")));
        assertEquals(2, net.getHeight(ip("85.193.148.81")));
        assertEquals(3, net.getHeight(ip("141.255.1.133")));
        assertEquals(4, net.getHeight(ip("0.146.197.108")));
    }

    @Test
    public void testAdd() throws ParseException {
        Network net = new Network(SMALL_NET);
        assertFalse(net.add(null));
        assertFalse(net.add(new Network(SMALL_NET_SORTED)));
        assertFalse(net.add(new Network("(141.255.1.133 122.117.67.158 0.146.197.108)")));
        assertTrue(net.add(new Network("(0.0.0.0 141.255.1.133)")));
        assertEquals(SMALL_NET_EXTENDED, net.toString(ip("85.193.148.81")));
        assertFalse(net.add(new Network("(122.117.67.158 0.146.197.108 141.255.1.133 1.1.1.1)")));
        assertTrue(net.add(new Network("(141.255.1.133 122.117.67.158 0.146.197.108 2.2.2.2)")));
        assertEquals(SMALL_NET_EXTENDED_2, net.toString(ip("85.193.148.81")));
        net = new Network(SMALL_NET);
        assertTrue(net.add(new Network("(0.0.0.0 1.1.1.1)")));
        assertEquals("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) "
                + "(231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))",
            net.toString(ip("85.193.148.81")));
        assertTrue(net.add(net("(1.0.0.0 1.0.0.1 1.0.0.2)")));
        assertEquals("(0.0.0.0 1.1.1.1)", net.toString(ip("0.0.0.0")));
        assertEquals("(1.0.0.0 1.0.0.1 1.0.0.2)", net.toString(ip("1.0.0.0")));
        assertTrue(net.add(net("(1.1.1.1 2.2.2.2 1.0.0.1)")));
        assertEquals("(1.1.1.1 0.0.0.0 (1.0.0.1 (1.0.0.0 1.0.0.2)) 2.2.2.2)",net.toString(ip("1.1.1.1")));
    }

    @Test
    public void testConnect() throws ParseException {
        Network net = new Network(SMALL_NET);
        net.add(net("(0.0.0.0 1.1.1.1)"));
        net.add(net("(1.0.0.1 1.0.0.2)"));
        net.add(net("(0.0.0.1 0.0.0.2 0.0.0.3 0.0.0.4)"));
        assertFalse(() -> net.connect(null, null));
        assertFalse(() -> net.connect(ip("0.0.0.0"), null));
        assertFalse(() -> net.connect(null, ip("0.0.0.0")));
        assertFalse(() -> net.connect(ip("0.0.0.0"), ip("9.9.9.9")));
        assertFalse(() -> net.connect(ip("9.9.9.9"), ip("0.0.0.0")));
        assertFalse(() -> net.connect(ip("0.0.0.0"), ip("0.0.0.0")));
        assertFalse(() -> net.connect(ip("0.0.0.0"), ip("1.1.1.1")));
        assertFalse(() -> net.connect(ip("1.1.1.1"), ip("0.0.0.0")));
        assertFalse(() -> net.connect(ip("231.189.0.127"), ip("77.135.84.171")));
        assertFalse(() -> net.connect(ip("77.135.84.171"), ip("231.189.0.127")));
        assertFalse(() -> net.connect(ip("122.117.67.158"), ip("77.135.84.171")));
        assertFalse(() -> net.connect(ip("77.135.84.171"), ip("122.117.67.158")));
        assertTrue(() -> net.connect(ip("1.1.1.1"), ip("1.0.0.2")));
        assertTrue(() -> net.connect(ip("1.0.0.2"), ip("0.0.0.3")));
        assertTrue(() -> net.connect(ip("231.189.0.127"), ip("1.0.0.1")));

        assertEquals(4, net.getHeight(ip("1.0.0.1")));
        assertEquals(
            "(1.0.0.1 (1.0.0.2 (0.0.0.3 (0.0.0.1 0.0.0.2 0.0.0.4)) (1.1.1.1 0.0.0.0)) (231.189.0.127 39.20.222.120 77.135.84.171 (85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158)) 116.132.83.77 252.29.23.0))",
            net.toString(ip("1.0.0.1")));
    }

    @Test
    public void testDisconnect() throws ParseException {
        Network net = new Network("(0.0.0.0 1.1.1.1)");
        assertFalse(net.disconnect(ip("0.0.0.0"), ip("1.1.1.1")));

        Network net2 = new Network("(0.0.0.0 (1.1.1.1 2.2.2.2) 3.3.3.3 (4.4.4.4 5.5.5.5))");

        assertFalse(() -> net2.disconnect(null, null));
        assertFalse(() -> net2.disconnect(ip("0.0.0.0"), null));
        assertFalse(() -> net2.disconnect(null, ip("0.0.0.0")));
        assertFalse(() -> net2.disconnect(ip("0.0.0.0"), ip("7.7.7.7")));
        assertFalse(() -> net2.disconnect(ip("7.7.7.7"), ip("0.0.0.0")));

        assertTrue(() -> net2.disconnect(ip("0.0.0.0"), ip("1.1.1.1")));
        assertEquals("(2.2.2.2 1.1.1.1)", net2.toString(ip("2.2.2.2")));
        assertEquals("(0.0.0.0 3.3.3.3 (4.4.4.4 5.5.5.5))", net2.toString(ip("0.0.0.0")));
        assertTrue(() -> net2.disconnect(ip("1.1.1.1"), ip("2.2.2.2")));
        assertFalse(() -> net2.contains(ip("1.1.1.1")));
        assertFalse(() -> net2.contains(ip("2.2.2.2")));
        assertTrue(() -> net2.disconnect(ip("4.4.4.4"), ip("5.5.5.5")));
        assertFalse(() -> net2.contains(ip("5.5.5.5")));
        assertTrue(() -> net2.contains(ip("4.4.4.4")));
        assertEquals("(0.0.0.0 3.3.3.3 4.4.4.4)", net2.toString(ip("0.0.0.0")));
    }

    @Test
    public void testGetLevels() throws ParseException {
        Network med = new Network(SMALL_NET);
        assertIterableEquals(List.of(), med.getLevels(null));
        assertIterableEquals(List.of(), med.getLevels(ip("0.0.0.0")));
        assertIterableEquals(List.of(ips("85.193.148.81"), ip("34.49.145.239", "141.255.1.133", "231.189.0.127"),
                ip("0.146.197.108", "39.20.222.120", "77.135.84.171", "116.132.83.77", "122.117.67.158", "252.29.23.0")),
            med.getLevels(ip("85.193.148.81")));
        assertIterableEquals(
            List.of(ips("77.135.84.171"), ips("231.189.0.127"),
                ip("39.20.222.120", "85.193.148.81", "116.132.83.77", "252.29.23.0"),
                ip("34.49.145.239", "141.255.1.133"), ip("0.146.197.108", "122.117.67.158")),
            med.getLevels(ip("77.135.84.171")));
        assertIterableEquals(
            List.of(ips("141.255.1.133"), ip("0.146.197.108", "85.193.148.81", "122.117.67.158"),
                ip("34.49.145.239", "231.189.0.127"),
                ip("39.20.222.120", "77.135.84.171", "116.132.83.77", "252.29.23.0")),
            med.getLevels(ip("141.255.1.133")));
    }

    @Test
    public void testGetPath() {
        Network net = net(SMALL_NET);
        assertEquals(List.of(), net.getRoute(null, null));
        assertEquals(List.of(), net.getRoute(ip("141.255.1.133"), null));
        assertEquals(List.of(), net.getRoute(null, ip("141.255.1.133")));
        assertEquals(List.of(), net.getRoute(ip("141.255.1.133"), ip("0.0.0.0")));
        assertEquals(List.of(), net.getRoute(ip("0.0.0.0"), ip("141.255.1.133")));

        assertEquals(ip("85.193.148.81", "141.255.1.133", "122.117.67.158"),
            net.getRoute(ip("85.193.148.81"), ip("122.117.67.158")));
        assertEquals(ip("122.117.67.158", "141.255.1.133", "85.193.148.81", "231.189.0.127", "39.20.222.120"),
            net.getRoute(ip("122.117.67.158"), ip("39.20.222.120")));

        assertTrue(() -> net.add(net("(0.0.0.0 1.1.1.1 2.2.2.2 (3.3.3.3 4.4.4.4))")));
        assertEquals(List.of(), net.getRoute(ip("39.20.222.120"), ip("4.4.4.4")));
    }

    @Test
    public void testEqualsAndHashCode() throws ParseException {
        Network net1 = new Network(MEDIUM_NET);
        Network net2 = new Network(MEDIUM_NET);
        Network net3 = new Network(SMALL_NET);
        Network net4 = new Network(SMALL_NET_ALTERNATIVE);

        assertEquals(net1, net2);
        assertEquals(net2, net1);
        assertEquals(net1.hashCode(), net2.hashCode());

        assertNotEquals(net1, net3);
        assertNotEquals(net3, net1);
        assertNotEquals(net1.hashCode(), net3.hashCode());

        assertEquals(net3, net4);
        assertEquals(net4, net3);
        assertEquals(net3.hashCode(), net4.hashCode());
    }

    @Test
    public void testToString() throws ParseException {
        Network small = new Network(SMALL_NET);
        assertEquals(SMALL_NET_SORTED, small.toString(ip("85.193.148.81")));
        assertEquals(SMALL_NET_ALTERNATIVE_SORTED_2, small.toString(ip("141.255.1.133")));
        assertEquals(SMALL_NET_ALTERNATIVE_SORTED, small.toString(ip("77.135.84.171")));
        assertEquals("", small.toString(ip("0.0.0.0")));
        assertEquals("", small.toString(null));

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