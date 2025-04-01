package client.userInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import client.connection.ClientManager;
import client.dtos.ClientEvent;

public class ClientGui extends JFrame implements ObserverClientGui {

    private final ClientManager clientManager;
    private String clientId;

    private JTextField activityRequestField;

    JPanel contentPane;

    private JPanel pendingPanel;
    private JPanel scheduledPanel;

    private JButton helpMenu;
    private JButton sendRequest;
    private JButton callForHelp;
    private JButton reportEmergency;

    private JComboBox<String> unitSelectDrop;

    private HashMap<String, ArrayList<JLabel>> pendingLabelsMap;
    private HashMap<String, ArrayList<JLabel>> scheduledLabelsMap;

    public ClientGui(ClientManager clientManager, String Id) {

        this.clientId = Id;

        this.clientManager = clientManager;
        clientManager.register(this);

        pendingLabelsMap = new HashMap<String, ArrayList<JLabel>>();
        scheduledLabelsMap = new HashMap<String, ArrayList<JLabel>>();
    }

    public void setUp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 285);

		contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
        
        // Requests
		sendRequest = new JButton("Request");
		sendRequest.setIcon(null);
		sendRequest.setBounds(315, 222, 117, 30);
		contentPane.add(sendRequest);
		
		activityRequestField = new JTextField();
		activityRequestField.setBounds(146, 165, 286, 45);
		contentPane.add(activityRequestField);
		activityRequestField.setColumns(10);
		
        JLabel unitIdText = new JLabel("CLIENT ID:");
		unitIdText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		unitIdText.setBounds(20, 183, 117, 29);
		contentPane.add(unitIdText);
		
		JLabel lblClientId = new JLabel(clientId);
		lblClientId.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblClientId.setBounds(90, 183, 117, 29);
		contentPane.add(lblClientId);

        // Help
        /* 
		callForHelp = new JButton("Call for Help");
		callForHelp.setBounds(6, 183, 117, 29);
		contentPane.add(callForHelp);
        */
		
		reportEmergency = new JButton("Call for Help");
		reportEmergency.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		reportEmergency.setBounds(6, 222, 117, 29);
		contentPane.add(reportEmergency);
		
        // Text Labels
		JLabel pendingLabel = new JLabel("Pending Approval");
		pendingLabel.setBounds(62, 6, 117, 23);
		contentPane.add(pendingLabel);
		
		JLabel scheduledLabel = new JLabel("Scheduled");
		scheduledLabel.setBounds(287, 6, 71, 23);
		contentPane.add(scheduledLabel);
		
        // Pending information
		JScrollPane pendingFrame = new JScrollPane();
		pendingFrame.setBounds(39, 34, 154, 124);
		contentPane.add(pendingFrame);
		
		pendingPanel = new JPanel();
		pendingFrame.setViewportView(pendingPanel);
        pendingPanel.setLayout(new GridLayout(0, 1, 0, 0));

        /* 
		GridBagLayout gbl_pendingPanel = new GridBagLayout();
		gbl_pendingPanel.columnWidths = new int[]{0};
		gbl_pendingPanel.rowHeights = new int[]{0};
		gbl_pendingPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_pendingPanel.rowWeights = new double[]{Double.MIN_VALUE};
		pendingPanel.setLayout(gbl_pendingPanel);
        */
		
        // Scheduled Information
		JScrollPane scheduledFrame = new JScrollPane();
		scheduledFrame.setBounds(243, 34, 154, 124);
		contentPane.add(scheduledFrame);
		
		scheduledPanel = new JPanel();
		scheduledFrame.setViewportView(scheduledPanel);
        scheduledPanel.setLayout(new GridLayout(0, 1, 0, 0));
        /* 
		GridBagLayout gbl_scheduledPanel = new GridBagLayout();
		gbl_scheduledPanel.columnWidths = new int[]{0};
		gbl_scheduledPanel.rowHeights = new int[]{0};
		gbl_scheduledPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_scheduledPanel.rowWeights = new double[]{Double.MIN_VALUE};
		scheduledPanel.setLayout(gbl_scheduledPanel);
        */
		
        unitSelectDrop = new JComboBox<String>();
		unitSelectDrop.setBounds(150, 225, 127, 27);
		contentPane.add(unitSelectDrop);

        // Robot info
		helpMenu = new JButton("?");
		helpMenu.setBounds(287, 221, 30, 30);
		contentPane.add(helpMenu);

        addListeners();

        setVisible(true);
    }

    /*
    public void setUp2() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 231);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel requestFromBroker = new JLabel("Request From Broker");
        requestFromBroker.setBounds(75,15,150,30);
        contentPane.add(requestFromBroker);

        JButton sendRequest = new JButton("Send Request");
        sendRequest.setBounds(100, 120, 117, 29);
        contentPane.add(sendRequest);
        
        JTextField activityRequest = new JTextField();
        activityRequest.setBounds(50, 55, 220, 50);
        contentPane.add(activityRequest);

        ButtonAction sendMessage = new ButtonAction(activityRequest);
        sendRequest.addActionListener(sendMessage);

        this.setVisible(true);
    }
    */

    public void update(ClientEvent guiEvent, String[] msg) {

        System.out.println("Updated: " + guiEvent + " | msg: " + Arrays.toString(msg));

        switch (guiEvent) {

            case ADD_ALL_UNITS:
                for (String unit : msg) {
                    unitSelectDrop.addItem(unit);
                }

                break;

            case SET_CURRENT_INPUT:
                activityRequestField.setText(msg[0]);

                break;

            case ADD_UNIT:
                unitSelectDrop.addItem(msg[0]);

                break;

            case REMOVE_UNIT:
                unitSelectDrop.removeItem(msg[0]);

                break;

            case ADD_PENDING:

                JLabel pendingLabel = new JLabel(msg[0]);
                pendingPanel.add(pendingLabel);

                pendingPanel.revalidate();
                pendingPanel.repaint();

                ArrayList<JLabel> labelsArray = pendingLabelsMap.get(msg[0]);

                if (labelsArray == null) {
                    labelsArray = new ArrayList<JLabel>();
                    labelsArray.add(pendingLabel);
                    pendingLabelsMap.put(msg[0], labelsArray);
                    //System.out.println(msgArray[0]);
                }
                else {
                    labelsArray.add(pendingLabel);
                }

                break;

            case REMOVE_PENDING:

                ArrayList<JLabel> removingLabelList = pendingLabelsMap.get(msg[0]);

                if (removingLabelList == null || removingLabelList.isEmpty()) return;

                JLabel removingLabel = removingLabelList.removeLast();
                pendingPanel.remove(removingLabel);

                pendingPanel.revalidate();
                pendingPanel.repaint();

                break;

            case ADD_SCHEDULED:
                JLabel scheduledLabel = new JLabel(msg[0]);
                scheduledPanel.add(scheduledLabel);

                ArrayList<JLabel> scheduledArray = scheduledLabelsMap.get(msg[0]);

                if (scheduledArray == null) {
                    scheduledArray = new ArrayList<JLabel>();
                    scheduledLabelsMap.put(msg[0], scheduledArray);
                }

                scheduledArray.add(scheduledLabel);

                scheduledPanel.revalidate();
                scheduledPanel.repaint();

                break;

            case REMOVE_SCHEDULED: // TODO: Add call once server done
                ArrayList<JLabel> removingScheduledList = scheduledLabelsMap.get(msg[0]);

                if (removingScheduledList == null || removingScheduledList.isEmpty()) return;

                JLabel removingScheduled = removingScheduledList.removeLast();
                scheduledPanel.remove(removingScheduled);

                scheduledPanel.revalidate();
                scheduledPanel.repaint();

                break;

            default:

            break;
        }
    }

    public void addListeners() {
        sendRequest.addActionListener(new SendRequest());
    }

    public class SendRequest implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

            String request = activityRequestField.getText();
            if (request.length() < 2) return; // TODO: cannot send an empty request
			clientManager.sendRequest(activityRequestField.getText(), (String) unitSelectDrop.getSelectedItem()); // TODO: change unit id
		}
	}
}
