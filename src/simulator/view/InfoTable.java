package simulator.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class InfoTable extends JPanel {
	String _title;
	TableModel _tableModel;

	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
	    // cambiar el layout del panel a BorderLayout()
		this.setLayout(new BorderLayout());
		
		// añadir un borde con título al JPanel, con el texto _title
		TitledBorder borde = BorderFactory.createTitledBorder(_title);
		this.setBorder(borde);
		
		// añadir un JTable (con barra de desplazamiento vertical) que use _tableModel
		JTable tabla = new JTable(_tableModel);
	    JScrollPane scrollPane = new JScrollPane(tabla);
	    this.add(scrollPane, BorderLayout.CENTER);
		
	}
}
