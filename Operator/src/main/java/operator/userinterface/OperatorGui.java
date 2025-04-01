package operator.userinterface;

import operator.connection.OperatorManager;
import operator.dtos.OperatorEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OperatorGui extends JFrame implements ObserverOperatorGui {
    
	OperatorManager operatorManager;

	JTextArea taskInformation;

	JLabel lblAvailable;
	JLabel lblRequestingUnit;
	JLabel lblSuggestedPriority;
	JLabel lblRequestingClient;

	JButton btnApprove;
	JButton btnDecline;
	JButton btnAnnounceAvailability;

	JComboBox<String> unitIdSelectDrop;
	JComboBox<String> prioritySelectDrop;

    public OperatorGui(OperatorManager operatorManager) {
		this.operatorManager = operatorManager;
		operatorManager.register(this);
    }

    public void setUp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/* 
		JButton accessUnit = new JButton("Access Unit");
		accessUnit.setBounds(6, 6, 117, 30);
		contentPane.add(accessUnit);
		*/
		
		/*
		JButton btnDeployInvestigation = new JButton("Deploy Investigation");
		btnDeployInvestigation.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnDeployInvestigation.setBounds(125, 6, 135, 30);
		contentPane.add(btnDeployInvestigation);
		*/

		lblAvailable = new JLabel("NOT AVAILABLE");
		lblAvailable.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		lblAvailable.setBounds(300, 6, 135, 30);
		contentPane.add(lblAvailable);

		btnAnnounceAvailability = new JButton("Toggle Availability");
		btnAnnounceAvailability.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		btnAnnounceAvailability.setBounds(300, 30, 135, 30);
		contentPane.add(btnAnnounceAvailability);
		
		JLabel lblNextTask = new JLabel("Next Task");
		lblNextTask.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblNextTask.setBounds(26, 20, 78, 16);
		contentPane.add(lblNextTask);

		taskInformation = new JTextArea();
		taskInformation.setEditable(false);
		taskInformation.setLineWrap(true);
		taskInformation.setForeground(new Color(77, 77, 77));
		taskInformation.setText("(task information)");
		taskInformation.setBounds(26, 93, 393, 86);
		contentPane.add(taskInformation);
		
		btnApprove = new JButton("Approve");
		btnApprove.setBounds(69, 189, 117, 29);
		contentPane.add(btnApprove);
		
		btnDecline = new JButton("Decline");
		btnDecline.setBounds(241, 189, 117, 29);
		contentPane.add(btnDecline);

		JLabel unitIdText = new JLabel("Unit Id");
		unitIdText.setForeground(new Color(77, 77, 77));
		unitIdText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		unitIdText.setBounds(100, 220, 90, 16);
		contentPane.add(unitIdText);
		
		unitIdSelectDrop = new JComboBox<String>();
		unitIdSelectDrop.setBounds(70, 240, 127, 27);
		contentPane.add(unitIdSelectDrop);

		JLabel requestingClientText = new JLabel("Client:");
		requestingClientText.setForeground(new Color(77, 77, 77));
		requestingClientText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		requestingClientText.setBounds(25, 45, 90, 16);
		contentPane.add(requestingClientText);

		lblRequestingClient = new JLabel("N/A");
		lblRequestingClient.setForeground(new Color(77, 77, 77));
		lblRequestingClient.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblRequestingClient.setBounds(75, 45, 37, 16);
		contentPane.add(lblRequestingClient);

		JLabel requestingUnitText = new JLabel("Requesting Unit: ");
		requestingUnitText.setForeground(new Color(77, 77, 77));
		requestingUnitText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		requestingUnitText.setBounds(26, 65, 117, 16);
		contentPane.add(requestingUnitText);
		
		lblRequestingUnit = new JLabel("N/A");
		lblRequestingUnit.setForeground(new Color(77, 77, 77));
		lblRequestingUnit.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblRequestingUnit.setBounds(136, 65, 37, 16);
		contentPane.add(lblRequestingUnit);
		
		JLabel suggestedPriorityText = new JLabel("Suggested Priority:");
		suggestedPriorityText.setForeground(new Color(77, 77, 77));
		suggestedPriorityText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		suggestedPriorityText.setBounds(221, 65, 127, 16);
		contentPane.add(suggestedPriorityText);
		
		lblSuggestedPriority = new JLabel("N/A");
		lblSuggestedPriority.setForeground(new Color(77, 77, 77));
		lblSuggestedPriority.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblSuggestedPriority.setBounds(347, 65, 40, 16);
		contentPane.add(lblSuggestedPriority);

		JLabel priorityText = new JLabel("Priority");
		priorityText.setForeground(new Color(77, 77, 77));
		priorityText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		priorityText.setBounds(260, 220, 90, 16);
		contentPane.add(priorityText);
		
		String[] priorityChoices = {"1","2","3","4"};
		prioritySelectDrop = new JComboBox<String>(priorityChoices);
		prioritySelectDrop.setBounds(241, 240, 127, 27);
		contentPane.add(prioritySelectDrop);

		addListeners();

        setVisible(true);
    }

	public void update(OperatorEvent operatorEvent, Object msg) {

		if (!msg.getClass().isArray() && msg.getClass().componentType() != String.class) return;

		String[] msgArray = (String[]) msg;
		
		if (msgArray.length == 0) return;

		switch (operatorEvent) {
			case NEW_TASK:
				if (msgArray.length < 4) return;
				//System.out.println(msgArray[0] + " " + msgArray[1] + " " + msgArray[2] + " " + msgArray[3]);
				taskInformation.setText(msgArray[0]);
				lblSuggestedPriority.setText(msgArray[1]);
				prioritySelectDrop.setSelectedItem(msgArray[1]);
				lblRequestingUnit.setText(msgArray[2]);
				unitIdSelectDrop.setSelectedItem(msgArray[2]);
				lblRequestingClient.setText(msgArray[3]);

				break;
		
			case AVAILABLE:
				lblAvailable.setText(msgArray[0]);
				break;

			case ADD_UNIT:
				unitIdSelectDrop.addItem(msgArray[0]);
				break;

			case REMOVE_UNIT:
				unitIdSelectDrop.removeItem(msgArray[0]);
				break;

			default:
				break;
		}
	}

	public void addListeners() {
		btnApprove.addActionListener(new ApproveActionListener());
		btnDecline.addActionListener(new DeclineActionListener());
		btnAnnounceAvailability.addActionListener(new ToggleAvailability());
	}

	// Action listeners
	public class ApproveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int adjustedPriority = 0;
			if (prioritySelectDrop.getSelectedItem() != null){
				adjustedPriority = Integer.valueOf((String) prioritySelectDrop.getSelectedItem());
			}

			String adjustedUnitId = "0";
			if (unitIdSelectDrop.getSelectedItem() != null){
				adjustedUnitId = (String)unitIdSelectDrop.getSelectedItem();
			}
			operatorManager.decideOnTask(true, adjustedPriority, adjustedUnitId);

			resetTaskInfo();
		}
	}

	public class DeclineActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			operatorManager.decideOnTask(false);
			resetTaskInfo();
		}
	}

	public class ToggleAvailability implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			operatorManager.announceAvailability(!operatorManager.isAvailable());
		}
	}

	public void resetTaskInfo() {
		taskInformation.setText("");
		lblSuggestedPriority.setText("N/A");
		lblRequestingUnit.setText("N/A");
		lblRequestingClient.setText("N/A");
	}
}
