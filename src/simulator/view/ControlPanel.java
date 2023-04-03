package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

@SuppressWarnings("serial")
class ControlPanel extends JPanel implements SimulatorObserver {
	private Controller _ctrl;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	private JButton _runButton;
	private JButton _loadButton;
	private JButton _stopButton;
	private JButton _physicsButton;
	private JButton _viewerButton;
	private JSpinner _steps;
	private JTextField _deltatime;
	private ForceLawsDialog _fl_dialog;

// TODO añade más atributos aquí …
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		// registrar this como observador
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		// crear los diferentes botones/atributos y añadirlos a _toolaBar.
		
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten
		
		
		// crear el selector de ficheros
		_fc = new JFileChooser();
				
		// Load Button
		
		_loadButton = new JButton();
		_loadButton.setToolTipText("Load an input file into the simulator");
		_loadButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_loadButton.addActionListener((e) -> {
			int returnVal = _fc.showOpenDialog(Utils.getWindow(this));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = _fc.getSelectedFile();
				try {
		            InputStream inputStream = new FileInputStream(file);
		            _ctrl.reset();
		            _ctrl.loadData(inputStream);

		        } catch (Exception ex) {
		        	System.out.println("Error loading file: " + ex.getMessage());
		        }
				
			}
			
		});
		_toolaBar.add(_loadButton);
		_toolaBar.addSeparator();
		
		// Physics button
		_physicsButton = new JButton();
		_physicsButton.setToolTipText("Select force laws for groups");
		_physicsButton.setIcon(new ImageIcon("resources/icons/physics.png"));
		_physicsButton.addActionListener((e) -> {
			
			if (_fl_dialog == null)
				_fl_dialog = new ForceLawsDialog((JFrame) Utils.getWindow(ControlPanel.this), _ctrl);
			
			_fl_dialog.open();
			
		});
		_toolaBar.add(_physicsButton);
		
		
		// Viewer button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Open viewer window");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener((e) -> new ViewerWindow((JFrame) Utils.getWindow(ControlPanel.this), _ctrl));
		_toolaBar.add(_viewerButton);
		
		// Run button
		_toolaBar.addSeparator();
		_runButton = new JButton();
		_runButton.setToolTipText("Run the simulator");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_stopped = false;
				
				_loadButton.setEnabled(_stopped);
				_physicsButton.setEnabled(_stopped);
				_viewerButton.setEnabled(_stopped);
				_runButton.setEnabled(_stopped);
				_quitButton.setEnabled(_stopped);
				
				
				_ctrl.setDeltaTime(Double.parseDouble(_deltatime.getText()));
				run_sim(Integer.parseInt(_steps.getValue().toString()));
			}
			
		});
		_toolaBar.add(_runButton);
		
		// Stop button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop the simulator");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener((e) -> _stopped = true);
		_toolaBar.add(_stopButton);
		
		_toolaBar.add( new JLabel("Steps: "));
		_steps = new JSpinner();
		_steps.setMinimumSize(new Dimension(60, 40));
		_steps.setMaximumSize(new Dimension(60, 40));
		_steps.setValue(10000);
		_steps.setToolTipText("Real time (seconds) correponding to a step");
		_steps.setToolTipText("Simulation steps to run: 1-10000");
		
		_toolaBar.add(_steps);
		
		_toolaBar.add( new JLabel("Delta-Time: "));
		_deltatime = new JTextField("2500.0");
		_deltatime.setMinimumSize(new Dimension(60, 40));
		_deltatime.setMaximumSize(new Dimension(60, 40));
		_toolaBar.add(_deltatime);
		
		
		
		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Exit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> Utils.quit(this));
		_toolaBar.add(_quitButton);
		
		
		
	}
	
	private void run_sim(int n) {
		
		
		if (n > 0 && !_stopped) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				Utils.showErrorMsg("Ha habido un error en la ejecucion");
				// activar todos los botones
				_stopped = true;
				
				_loadButton.setEnabled(_stopped);
				_physicsButton.setEnabled(_stopped);
				_viewerButton.setEnabled(_stopped);
				_runButton.setEnabled(_stopped);
				_quitButton.setEnabled(_stopped);
				
				return;
			}
			SwingUtilities.invokeLater(() -> run_sim(n - 1));
		} else {
			// activar todos los botones
			_stopped = true;
			
			_loadButton.setEnabled(_stopped);
			_physicsButton.setEnabled(_stopped);
			_viewerButton.setEnabled(_stopped);
			_runButton.setEnabled(_stopped);
			_quitButton.setEnabled(_stopped);
			
		}
	}
	

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {

	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
		// TODO Auto-generated method stub

	}
}
