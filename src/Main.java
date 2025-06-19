import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
    	JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "Enter sudo password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Sudo Password Required", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            char[] password = passwordField.getPassword();
            if (password.length == 0) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty. Exiting.");
                System.exit(0);
            } else {
                IptablesCommand.SUDO_PASS = new String(password);
            }
        } else {
            System.exit(0);
        }

        JFrame frame = new JFrame("Linux Iptables GUI Manager");
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2));

        String[] protocols = {"TCP", "UDP"};
        JComboBox<String> protocolBox = new JComboBox<>(protocols);

        JCheckBox syn = new JCheckBox("SYN");
        JCheckBox ack = new JCheckBox("ACK");
        JCheckBox fin = new JCheckBox("FIN");
        JCheckBox rst = new JCheckBox("RST");
        JCheckBox psh = new JCheckBox("PSH");
        JCheckBox urg = new JCheckBox("URG");

        JTextField srcIP = new JTextField();
        JTextField dstIP = new JTextField();
        JTextField srcPort = new JTextField();
        JTextField dstPort = new JTextField();
        JTextArea whitelistArea = new JTextArea(3, 20);
        JTextArea blacklistArea = new JTextArea(3, 20);

        String[] actions = {"ACCEPT", "DROP"};
        JComboBox<String> actionBox = new JComboBox<>(actions);

        JButton addRuleBtn = new JButton("Add Rule");
        JButton refreshBtn = new JButton("Refresh");
        JButton deleteRules = new JButton("Delete Rules");
        JButton deleteRule = new JButton("Delete");

        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!outputArea.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Found files clicked:\n" + outputArea.getText());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(outputArea);  

        // Panel Layout
        panel.add(new JLabel("Protocol:"));
        panel.add(protocolBox);
        JPanel flagPanel = new JPanel();
        flagPanel.add(syn);
        flagPanel.add(ack);
        flagPanel.add(fin);
        flagPanel.add(rst);
        flagPanel.add(psh);
        flagPanel.add(urg);

        

        panel.add(new JLabel("TCP Flags (only if TCP):"));
        panel.add(flagPanel);
        
        protocolBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selected = (String) protocolBox.getSelectedItem();
                boolean isTcp = "TCP".equals(selected);
                flagPanel.setVisible(isTcp);
                
                frame.revalidate();
                frame.repaint();
            }
        });
        
        panel.add(new JLabel("Source IP:"));
        panel.add(srcIP);
        panel.add(new JLabel("Destination IP:"));
        panel.add(dstIP);
        panel.add(new JLabel("Source Port:"));
        panel.add(srcPort);
        panel.add(new JLabel("Destination Port:"));
        panel.add(dstPort);
        panel.add(new JLabel("Action:"));
        panel.add(actionBox);
                
        panel.add(new JLabel("Whitelist IPs (newline):"));
        panel.add(new JScrollPane(whitelistArea));
        panel.add(new JLabel("Blacklist IPs (newline):"));
        panel.add(new JScrollPane(blacklistArea));
        
        panel.add(new JLabel("Rule Mode:"));
        String[] insertModes = {"INSERT (-I)", "APPEND (-A)"};
        JComboBox<String> modeBox = new JComboBox<>(insertModes);
        panel.add(modeBox);

        panel.add(new JLabel("Position (for INSERT only):"));
        JTextField positionField = new JTextField();
        panel.add(positionField);
        modeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedMode = (String) modeBox.getSelectedItem();
                boolean isInsert = selectedMode.startsWith("INSERT");
                positionField.setVisible(isInsert);
                frame.revalidate();
                frame.repaint();
            }
        });

        panel.add(addRuleBtn);
        panel.add(refreshBtn);
        panel.add(deleteRules);
        panel.add(deleteRule);

       
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.getContentPane().add(BorderLayout.SOUTH, scrollPane);

        frame.setVisible(true);

        // Logic
        addRuleBtn.addActionListener(e -> {
            String proto = protocolBox.getSelectedItem().toString().toLowerCase();
            String action = actionBox.getSelectedItem().toString();

            String flags = "";
            if (proto.equals("tcp")) {
                if (syn.isSelected()) flags += "SYN,";
                if (ack.isSelected()) flags += "ACK,";
                if (fin.isSelected()) flags += "FIN,";
                if (rst.isSelected()) flags += "RST,";
                if (psh.isSelected()) flags += "PSH,";
                if (urg.isSelected()) flags += "URG,";
                if (!flags.isEmpty()) flags = flags.substring(0, flags.length() - 1);
            }

            Rule rule = new Rule(proto, srcIP.getText(), dstIP.getText(), srcPort.getText(), dstPort.getText(), action, flags);
            
            String modeValue = modeBox.getSelectedItem().toString();
            String mode = modeValue.contains("INSERT") ? "I" : "A";
            int position = 0;
            if (mode.equals("I")) {
                try {
                    position = Integer.parseInt(positionField.getText().trim());
                } catch (NumberFormatException ex) {
                    outputArea.append("Invalid position number.\n");
                    return;
                }
            }
            if(!rule.srcIP.isEmpty() || !rule.dstIP.isEmpty() || !rule.srcPort.isEmpty()|| !rule.dstPort.isEmpty()) {
	            if (Validator.isValid(rule)) {
	                try {
	                    IptablesCommand.addRule(rule, mode, position);
	                    outputArea.append("Rule added.\n");
	                } catch (Exception ex) {
	                    outputArea.append("Error adding rule: " + ex.getMessage() + "\n");
	                }
	            } else {
	                outputArea.append("Invalid input. Please check IPs and ports.\n");
	            }
            }

            // Add whitelist/blacklist IPs
            String[] whitelist = whitelistArea.getText().split("\n");
            for (String ip : whitelist) {
                if (Validator.isValidIP(ip)) {
                    IptablesCommand.addSimpleRule(ip.trim(), "ACCEPT");
                    outputArea.append("Whitelisted: " + ip + "\n");
                }
            }

            String[] blacklist = blacklistArea.getText().split("\n");
            for (String ip : blacklist) {
                if (Validator.isValidIP(ip)) {
                    IptablesCommand.addSimpleRule(ip.trim(), "DROP");
                    outputArea.append("Blacklisted: " + ip + "\n");
                }
            }
        });

        refreshBtn.addActionListener(e -> {
            List<String> rules = IptablesCommand.listRules();
            outputArea.setText("");
            for (String r : rules) {
                outputArea.append(r + "\n");
            }
        });
        
        deleteRule.addActionListener(e ->{
        	String SelectRule = "Select Rule Number";
        	String Rule = JOptionPane.showInputDialog(SelectRule);
        	int Num = Integer.parseInt(Rule);
        	try {
				IptablesCommand.deleteRule(Num);
			} catch (Exception e1) {
				 outputArea.append("Error deleting rule: " + e1.getMessage() + "\n");
			}
        	  
        });
        deleteRules.addActionListener(e ->{
        	try {
				IptablesCommand.deleteRules();
			} catch (Exception e1) {
				 outputArea.append("Error deleting rule: " + e1.getMessage() + "\n");
			}
        });
    }
}
