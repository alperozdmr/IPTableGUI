
public class Rule {
    String protocol;
    String srcIP;
    String dstIP;
    String srcPort;
    String dstPort;
    String action;
    String flags;

    public Rule(String protocol, String srcIP, String dstIP, String srcPort, String dstPort, String action, String flags) {
        this.protocol = protocol;
        this.srcIP = srcIP;
        this.dstIP = dstIP;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.action = action;
        this.flags = flags;
    }
}
