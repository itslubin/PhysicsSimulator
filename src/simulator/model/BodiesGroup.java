package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class BodiesGroup implements Iterable<Body>{
	
	private String id;
	private ForceLaws fl;
	private List<Body> blist;
	List<Body> _bodiesRO;
	
	public BodiesGroup(String id, ForceLaws fl) {
		if (id == null || fl == null || id.trim().length() == 0) {
			throw new IllegalArgumentException("algun parametro es null o el identificador esta vacio");
		}
		else {
			this.id = id;
			this.fl = fl;
			this.blist = new ArrayList<Body>();
			this._bodiesRO = Collections.unmodifiableList(blist);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setForceLaws(ForceLaws fl) {
		if (fl == null) {
			throw new IllegalArgumentException("la ForceLaws es null");
		}
		else {
			this.fl = fl;
		}
	}
	
	public void addBody(Body b) {
		if (b == null) {
			throw new IllegalArgumentException("el Body es null");
		}
		else if (blist.contains(b)) {
			throw new IllegalArgumentException("el Body ya existe");
		}
		
		blist.add(b);
	}
	
	public void advance(double dt) {
		if (dt <= 0) {

			throw new IllegalArgumentException("el tiempo no es positivo");

		}
		
		for(Body b : blist) {
			b.resetForce();
		}
		
		fl.apply(blist);
		
		for(Body b : blist) {
			b.advance(dt);
		}
	}
	
	public JSONObject getState() {
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		JSONArray ja = new JSONArray();
		for (Body b : blist) {
			ja.put(b.getState());
		}
		jo.put("bodies", ja);
		
		return jo;
	}
	
	public String toString() {
		return getState().toString();
	}
	
	public String getForceLawsInfo() {
		return fl.toString();
	}

	@Override
	public Iterator<Body> iterator() {
		return _bodiesRO.iterator();
	}
	
	
}
