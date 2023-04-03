package simulator.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

@SuppressWarnings("serial")
class GroupsTableModel extends AbstractTableModel implements SimulatorObserver {
	
	
	String[] _header = { "Id", "Force Laws", "Bodies" };
	List<BodiesGroup> _groups;

	GroupsTableModel(Controller ctrl) {
		_groups = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _groups.size();
	}

	@Override
	public int getColumnCount() {
		return _header.length;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
	    return _header[columnIndex];
	}

	

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) { // darle valores de cada celda de tabla
		switch (columnIndex) {
		case 0:
			return _groups.get(rowIndex).getId();
		case 1:
			return _groups.get(rowIndex).getForceLawsInfo();
		case 2:
			StringBuilder s = new StringBuilder();
			for (Body b : _groups.get(rowIndex)) {
				s.append(b.getId() + " ");
			}
			return s;
		default:
			break;
		}
		return null;
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		fireTableDataChanged();
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_groups.clear();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		for (BodiesGroup bg : groups.values()) {
			_groups.add(bg);
		}
		fireTableDataChanged();
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_groups.add(g);
		fireTableDataChanged();
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		fireTableDataChanged();
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {
		fireTableDataChanged();
	}
}
