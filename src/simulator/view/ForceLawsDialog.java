package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

@SuppressWarnings("serial")

class ForceLawsDialog extends JDialog implements SimulatorObserver {
	private DefaultComboBoxModel<String> _lawsModel;
	private DefaultComboBoxModel<String> _groupsModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _forceLawsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status;
	private int _selectedLawsIndex;

// en caso de ser necesario, añadir los atributos aquí…
	ForceLawsDialog(Frame parent, Controller ctrl) {
		super(parent, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Force Laws Selection");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		// _forceLawsInfo se usará para establecer la información en la tabla
		_forceLawsInfo = _ctrl.getForceLawsInfo();
		
		JLabel text = new JLabel("<html><p>Select a force law and provide values for the parametes in the Value column (default values are used for parametes with no value).</p></html>");
		text.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(text);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		_dataTableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column) {
				// hacer editable solo la columna 1
				return column == 1;
			}
		};
		
		_dataTableModel.setColumnIdentifiers(_headers);
		
		_lawsModel = new DefaultComboBoxModel<>();
		
		// añadir la descripción de todas las leyes de fuerza a _lawsModel
		
		for (JSONObject jo : _forceLawsInfo) {
			_lawsModel.addElement(jo.getString("desc"));
		}
		
		updateTableModel(0);
		JTable dataTable = new JTable(_dataTableModel) {
			private static final long serialVersionUID = 1L;

			// we override prepareRenderer to resize columns to fit to content
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		JScrollPane tabelScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(tabelScroll);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		// crear un combobox que use _lawsModel y añadirlo al panel
		
		JLabel fl = new JLabel("Force Law: ");
		
		
		JComboBox<String> laws = new JComboBox<>(_lawsModel);
		
		laws.addActionListener((e) -> {
			updateTableModel(laws.getSelectedIndex());
		});
		
		JPanel first = new JPanel();
		first.setLayout(new BoxLayout(first, BoxLayout.X_AXIS));
		first.add(fl);
		first.add(laws);
		
		_groupsModel = new DefaultComboBoxModel<>();
		
		// crear un combobox que use _groupsModel y añadirlo al panel
		
		JLabel g = new JLabel("Group: ");

		first.add(Box.createRigidArea(new Dimension(10, 0)));
		
		JComboBox<String> groups = new JComboBox<>(_groupsModel);
		
		first.add(g);
		first.add(groups);
		
		first.setAlignmentX(CENTER_ALIGNMENT);
		
		mainPanel.add(first);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		// crear los botones OK y Cancel y añadirlos al panel
		
		JPanel second = new JPanel();
		second.setLayout(new BoxLayout(second, BoxLayout.X_AXIS));
		
		JButton cancel = new JButton("Cancel");
		
		
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_status = 0;
				setVisible(false);
			}
		});
		
		JButton ok = new JButton("OK");
		
		ok.addActionListener((e) -> {
			// Creamos un JSONObject a partir de la tabla
			JSONObject jo = new JSONObject();
			for (int i = 0; i < _dataTableModel.getRowCount(); ++i) {
				
				String data = _dataTableModel.getValueAt(i, 1).toString();
				
				if (!data.isEmpty()) {
					try {
			            JSONArray jsonArray = new JSONArray(data);
			            jo.put((String) _dataTableModel.getValueAt(i, 0),jsonArray);
			        } catch (Exception ex) {
			        	jo.put((String) _dataTableModel.getValueAt(i, 0), Double.parseDouble(data));
			        }
				}
			}
			
			JSONObject jo1 = new JSONObject();
			
			jo1.put("data", jo);
			jo1.put("type", _forceLawsInfo.get(_selectedLawsIndex).getString("type"));
			
			try {
				_ctrl.setForcesLaws( (String) _groupsModel.getSelectedItem(), jo1);
				_status = 1;
				setVisible(false);
				
			} catch (Exception ex) {
				Utils.showErrorMsg("El valor introducido no es valido");
			}
			
			
		});
		
		
		second.add(cancel);
		second.add(Box.createRigidArea(new Dimension(20, 0)));
		second.add(ok);
		
		second.setAlignmentX(CENTER_ALIGNMENT);
		
		mainPanel.add(second);
		
		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		
		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}
	
	private void updateTableModel(int _dataIdx) {
		JSONObject info = _forceLawsInfo.get(_dataIdx);
		JSONObject data = info.getJSONObject("data");
		Iterator<String> it = data.keys();
		_dataTableModel.setNumRows(data.length());
		
		_selectedLawsIndex = _dataIdx;
		
		int i = 0;
		while(it.hasNext()) {
			String s = it.next();
			_dataTableModel.setValueAt(s, i, 0);
			_dataTableModel.setValueAt("", i, 1) ;
			_dataTableModel.setValueAt(data.getString(s), i, 2);
			i++;
		}
	}

	public int open() {
		if (_groupsModel.getSize() == 0)
			return _status;
		
		// Establecer la posición de la ventana de diálogo de tal manera que se abra en el centro de la ventana principal
		setLocationRelativeTo(getParent());
		pack();
		setVisible(true);
		return _status;
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_groupsModel.removeAllElements();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		for (BodiesGroup bg : groups.values()) {
			_groupsModel.addElement(bg.getId());
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		_groupsModel.addElement(g.getId());
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
