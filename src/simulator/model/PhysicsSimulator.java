package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicsSimulator implements Observable<SimulatorObserver> {
	
	Map<String, BodiesGroup> map;
	List<SimulatorObserver> list;
	private Map<String, BodiesGroup> _groupsRO;
	double dt;
	double time = 0;
	ForceLaws fl;
	
	public PhysicsSimulator(ForceLaws fl, double dt) {
		if (dt <= 0 || fl == null) {
			throw new IllegalArgumentException("el tiempo dt a avanzar no es positivo o la ForceLaws es null");
		}
		this.map = new HashMap<String,BodiesGroup>();
		this.list = new ArrayList<>();
		this._groupsRO = Collections.unmodifiableMap(map);
		this.dt = dt;
		this.fl = fl;
		this.time = 0;
	}
	
	public void reset() {
		map.clear();
		time = 0;
		for (SimulatorObserver so : list) {
			so.onReset(_groupsRO, time, dt);
		}
	}
	
	public void setDeltaTime(double dt) {
		if (dt < 0) {
			throw new IllegalArgumentException("el tiempo dt a avanzar no es positivo");
		}
		
		this.dt = dt;
		
		for (SimulatorObserver so : list) {
			so.onDeltaTimeChanged(dt);
		}
	}
	
	public void advance() { // Al ser los objetos punteros se puede coger los valores directamente y modificarlos

		for (BodiesGroup bg : map.values()) {
			bg.advance(dt);
		}
		this.time += dt;
		
		for (SimulatorObserver so : list) {
			so.onAdvance(_groupsRO, time);
		}
	}
	
	public void addGroup(String id) {
		
		if (map.containsKey(id)) throw new IllegalArgumentException("ya existe el identificador del BodyGroup");
		BodiesGroup bg = new BodiesGroup(id, fl);
		map.put(id, bg);
		
		for (SimulatorObserver so : list) {
			so.onGroupAdded(_groupsRO, bg);
		}
	}
	
	public void addBody(Body b) {
		if (!map.containsKey(b.getgId())) {
			throw new IllegalArgumentException("la gid del Body no existe (en el mapa)");
		}
		
		map.get(b.getgId()).addBody(b);
		
		for (SimulatorObserver so : list) {
			so.onBodyAdded(_groupsRO, b);
		}
	}
	
	public void setForceLaws(String id, ForceLaws fl) {
		if (!map.containsKey(id)) {
			throw new IllegalArgumentException("la id del BodyGroup no existe (en el mapa)");
		}
		map.get(id).setForceLaws(fl);
		for (SimulatorObserver so : list) {
			so.onForceLawsChanged(map.get(id));
		}
	}
	
	public JSONObject getState() {
		JSONObject jo = new JSONObject();
		jo.put("time", time);
		
		JSONArray ja = new JSONArray();
		
		for (BodiesGroup bg : map.values()) {
			ja.put(bg.getState());
		}
		
		jo.put("groups", ja);
		return jo;
	}
	
	public String toString() {
		return getState().toString();
	}

	@Override
	public void addObserver(SimulatorObserver o) {
		if (list.contains(o)) {
			throw new IllegalArgumentException("el observador ya esta en la lista");
		}
		
		list.add(o);
		o.onRegister(_groupsRO, time, dt);
		
	}

	@Override
	public void removeObserver(SimulatorObserver o) {
		list.remove(o);
	}
}
