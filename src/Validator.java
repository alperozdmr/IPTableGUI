public class Validator {
    public static boolean isValid(Rule rule) {
        
        boolean okProto = "tcp".equals(rule.protocol) || "udp".equals(rule.protocol);

        
        boolean okSrcIP  = rule.srcIP .isEmpty() || isValidIP(rule.srcIP);
        boolean okDstIP  = rule.dstIP .isEmpty() || isValidIP(rule.dstIP);
        boolean okSrcPort= rule.srcPort.isEmpty() || isValidPort(rule.srcPort);
        boolean okDstPort= rule.dstPort.isEmpty() || isValidPort(rule.dstPort);

        return okProto && okSrcIP && okDstIP && okSrcPort && okDstPort;
    }

    public static boolean isValidIP(String ip) {
        return ip.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+(\\/\\d+)?$");
    }

    public static boolean isValidPort(String port) {
        try {
            int p = Integer.parseInt(port);
            return p >= 1 && p <= 65535;
        } catch (Exception e) {
            return false;
        }
    }
}
