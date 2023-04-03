package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

@SuppressWarnings("serial")
class StatusBar extends JPanel implements SimulatorObserver {
	
	
	private JLabel _time;
	private JLabel _num_groups;

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		// Crear una etiqueta de tiempo y añadirla al panel
		add(new JLabel("Time: "));
		
		_time = new JLabel("0.0");
		
		add(_time);
		
		JSeparator s1 = new JSeparator(JSeparator.VERTICAL);
		s1.setPreferredSize(new Dimension(10, 20));
		this.add(s1);
		
		// Crear la etiqueta de número de grupos y añadirla al panel
		add(new JLabel("Groups: "));
		
		_num_groups = new JLabel("0");
		
		add(_num_groups);
		
		JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);
		
		 
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		_time.setText(Double.toString(time));
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_time.setText("0.0");
		_num_groups.setText("0");
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		_time.setText(Double.toString(time));
		_num_groups.setText(Integer.toString(groups.size()));
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_num_groups.setText(Integer.toString(groups.size())); // DUDA
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {

	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {

	}
}
