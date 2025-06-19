import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IptablesCommand {
    
    public static  String SUDO_PASS = "";

    public static void addRule(Rule rule,String mode, int position) throws Exception {
    	
        
    	
    	if (!mode.equals("I") && !mode.equals("A")) {
            throw new IllegalArgumentException("Mode must be 'I' (insert) or 'A' (append)");
        }
    	 StringBuilder cmd = new StringBuilder();
    	    cmd.append("echo ").append(SUDO_PASS).append(" | sudo -S iptables");

    	    cmd.append(" -").append(mode).append(" INPUT");
    	    if (mode.equals("I") && position != 0 ) {
    	        cmd.append(" ").append(position);
    	    }

    	    cmd.append(" -p ").append(rule.protocol);

    	    if ("tcp".equals(rule.protocol) && !rule.flags.isEmpty()) {
    	        cmd.append(" --tcp-flags SYN,ACK,FIN,RST,PSH,URG ")
    	           .append(rule.flags);
    	    }

    	    if (!rule.srcIP .isEmpty()) cmd.append(" -s ").append(rule.srcIP);
    	    if (!rule.dstIP .isEmpty()) cmd.append(" -d ").append(rule.dstIP);
    	    if (!rule.srcPort.isEmpty()) cmd.append(" --sport ").append(rule.srcPort);
    	    if (!rule.dstPort.isEmpty()) cmd.append(" --dport ").append(rule.dstPort);

    	    cmd.append(" -j ").append(rule.action);

    	    ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd.toString());
    	    pb.inheritIO();
    	    Process p = pb.start();
    	    if (p.waitFor() != 0) {
    	        throw new RuntimeException("iptables komutu hatayla döndü");
    	    }
    }

    public static void addSimpleRule(String ip, String action) {
        try {
            String simple = String.format(
                "echo %s | sudo -S iptables -I INPUT 1 -s %s -j %s",
                SUDO_PASS, ip, action
            );
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", simple);
            pb.inheritIO();
            pb.start().waitFor();
        } catch (Exception e) {
            System.err.println("Error adding simple rule: " + e.getMessage());
        }
    }
    public static void deleteRule(int lineNumber) throws Exception {
        String cmd = String.format(
            "echo %s | sudo -S iptables -D INPUT %d",
            SUDO_PASS, lineNumber
        );
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new RuntimeException("iptables delete komutu hatayla döndü");
        }
    }
    public static void deleteRules() throws Exception {
        String cmd = String.format(
            "echo %s | sudo -S iptables -F",
            SUDO_PASS
        );
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
        pb.inheritIO();
        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new RuntimeException("iptables delete komutu hatayla döndü");
        }
    }

    public static List<String> listRules() {
        List<String> lines = new ArrayList<>();
        try {
           
            String listCmd = String.format(
              "echo %s | sudo -S iptables -L INPUT -v -n --line-numbers",
              SUDO_PASS
            );
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", listCmd);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            p.waitFor();
        } catch (Exception e) {
            lines.add("Error: " + e.getMessage());
        }
        return lines;
    }
}

