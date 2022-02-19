package edu.kit.informatik.networkdetection;

import edu.kit.informatik.networkdetection.exception.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IPTest {

    @Test
    void patternMatching() throws ParseException {
        assertThrows(ParseException.class, () -> new IP("01.0.0.0"));
        assertThrows(ParseException.class, () -> new IP("0.01.0.0"));
        assertThrows(ParseException.class, () -> new IP("0.0.01.0"));
        assertThrows(ParseException.class, () -> new IP("0.0.0.01"));
        assertThrows(ParseException.class, () -> new IP("0.0.0 1"));

        assertThrows(ParseException.class, () -> new IP(""));
        assertThrows(ParseException.class, () -> new IP("abc"));
        assertThrows(ParseException.class, () -> new IP("#0.0.0.0"));

        assertThrows(ParseException.class, () -> new IP("0 .0.0.0"));
        assertThrows(ParseException.class, () -> new IP("0.0 .0.0"));
        assertThrows(ParseException.class, () -> new IP("0.0.0.0.0"));
        assertThrows(ParseException.class, () -> new IP(" 0.0.0.0"));
        assertThrows(ParseException.class, () -> new IP(".0.0.0.0"));
        assertThrows(ParseException.class, () -> new IP("0..0.0"));
        assertThrows(ParseException.class, () -> new IP("0.0.0.256"));
        assertThrows(ParseException.class, () -> new IP("0.0.256.0"));
        assertThrows(ParseException.class, () -> new IP("0.256.0.0"));
        assertThrows(ParseException.class, () -> new IP("256.0.0.0"));
        assertThrows(ParseException.class, () -> new IP("-1.0.0.0"));

        String[] ips = {"0.0.0.0", "0.0.0.255", "0.0.255.0", "0.255.0.0", "255.0.0.0", "255.255.255.255"};
        for (String ip : ips) {
            assertEquals(ip, new IP(ip).toString());
        }
    }

    @Test
    void compareTo() throws ParseException {
        IP ip1 = new IP("192.168.178.65");
        IP ip2 = new IP("192.168.178.60");
        IP ip3 = new IP("192.168.178.65");

        assertEquals(1, ip1.compareTo(ip2));
        assertEquals(0, ip1.compareTo(ip3));
        assertEquals(-1, ip2.compareTo(ip1));

        IP[] ips = {new IP("0.0.0.0"), new IP("127.255.255.255"), new IP("128.0.0.0"), new IP("255.255.255.255")};

        for (int x = 0; x < ips.length; x++) {
            for (int y = 0; y < ips.length; y++) {
                int res = Integer.compare(x, y);
                assertEquals(res, ips[x].compareTo(ips[y]));
            }
        }

    }

    @Test
    void equalsAndHashcode() throws ParseException {
        IP ip1 = new IP("192.168.178.65");
        IP ip2 = new IP("192.168.178.60");
        IP ip3 = new IP("192.168.178.60");
        IP ip4 = new IP("192.168.178.65");

        assertEquals(ip1, ip4);
        assertEquals(ip4, ip1);
        assertEquals(ip1.hashCode(), ip4.hashCode());

        assertEquals(ip2, ip3);
        assertEquals(ip3, ip2);
        assertEquals(ip2.hashCode(), ip3.hashCode());

        assertNotEquals(ip1, ip2);
        assertNotEquals(ip2, ip1);
        assertNotEquals(ip1.hashCode(), ip2.hashCode());
    }
}