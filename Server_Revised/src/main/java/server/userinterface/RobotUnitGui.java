package server.userinterface;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import server.dtos.ServerGuiEvent;
import server.unit.ObservableUnit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class RobotUnitGui extends JFrame implements ServerObserver {
    
	private final ObservableUnit observableUnit;
	private JLabel lblUnitId;
	private JTextArea currentlyExecutingName;
	private JPanel queuePanel;

	private HashMap<String, JLabel> stringQueueMap = new HashMap<>();

    public void setUp(String unitId) {
		observableUnit.register(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 390, 250);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel unitIdText = new JLabel("ROBOT UNIT ID:");
		unitIdText.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		unitIdText.setBounds(102, 6, 124, 16);
		contentPane.add(unitIdText);
		
		lblUnitId = new JLabel(unitId);
		lblUnitId.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblUnitId.setBounds(226, 6, 52, 16);
		contentPane.add(lblUnitId);
		
		currentlyExecutingName = new JTextArea();
		currentlyExecutingName.setBounds(32, 71, 197, 124);
		contentPane.add(currentlyExecutingName);
		
		JScrollPane queueScrollPane = new JScrollPane();
		queueScrollPane.setBounds(261, 71, 108, 124);
		contentPane.add(queueScrollPane);
		
		queuePanel = new JPanel();
		queueScrollPane.setViewportView(queuePanel);
		queuePanel.setLayout(new GridLayout(0, 1, 0, 2));
		
		JLabel currentlyExecutingText = new JLabel("Currently Executing");
		currentlyExecutingText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		currentlyExecutingText.setBounds(71, 48, 131, 16);
		contentPane.add(currentlyExecutingText);
		
		JLabel activityQueueText = new JLabel("Activity Queue");
		activityQueueText.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		activityQueueText.setBounds(269, 48, 102, 16);
		contentPane.add(activityQueueText);

		setVisible(true);
    }

	@Override
	public void update(ServerGuiEvent guiEvent, Object msg) {

		if (msg.getClass() != String.class) return;

		String msgString = (String) msg;

		switch (guiEvent) {
			case ADD_TO_QUEUE:
				JLabel taskItem = new JLabel(msgString);
				queuePanel.add(taskItem);
				stringQueueMap.put(msgString, taskItem);

				repaint();
				revalidate();
				
				break;

			case REMOVE_FROM_QUEUE:
				queuePanel.remove(stringQueueMap.remove(msgString));

				repaint();
				revalidate();

				break;

			case EXECUTE:
				currentlyExecutingName.setText(msgString);

				break;

			case FINISHED_EXECUTING:
				currentlyExecutingName.setText("");

				break;

			default:
				break;
		}
	}

}
