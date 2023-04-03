package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;

public class Controller {

	private PhysicsSimulator ps;
	private Factory<Body> fb;
	private Factory<ForceLaws> ffl;

	public Controller(PhysicsSimulator ps, Factory<Body> fb, Factory<ForceLaws> ffl) {
		this.ps = ps;
		this.fb = fb;
		this.ffl = ffl;
	}

	public void loadData(InputStream in) {
		JSONObject jsonInput = new JSONObject(new JSONTokener(in));

		if (jsonInput.has("groups")) {
			JSONArray jgroup = jsonInput.getJSONArray("groups");

			for (Object s : jgroup) {
				ps.addGroup(s.toString());
			}
		}

		else {
			throw new IllegalArgumentException("El input no tiene groups");
		}

		if (jsonInput.has("laws")) {
			JSONArray jlaws = jsonInput.getJSONArray("laws");
			for (Object o : jlaws) {
				ps.setForceLaws(((JSONObject) o).get("id").toString(),
						ffl.createInstance(((JSONObject) o).getJSONObject("laws")));
			}
		}
		
		if(jsonInput.has("bodies")) {
			JSONArray jbodies = jsonInput.getJSONArray("bodies");

			for (Object o : jbodies) {
				ps.addBody(fb.createInstance(((JSONObject) o)));
			}
		}
		
		else {
			throw new IllegalArgumentException("El input no tiene bodies");
		}
		
	}

	public void run(int n, OutputStream out) {
		PrintStream p = new PrintStream(out);
		p.println("{");
		p.println("\"states\": [");
		// run the sumulation n steps, etc.
		for (int i = 0; i < n; ++i) {
			p.println(ps.toString());
			p.print(",");
			ps.advance();
		}
		p.println(ps.toString());

		p.println("]");
		p.println("}");
	}
	
	
	public void reset() {
		ps.reset();
	}
	
	public void setDeltaTime(double dt) {
		ps.setDeltaTime(dt);
	}
	
	public void addObserver(SimulatorObserver o) {
		ps.addObserver(o);
	}
	
	public void removeObserver(SimulatorObserver o) {
		ps.removeObserver(o);
	}
	
	public List<JSONObject> getForceLawsInfo() {
		return ffl.getInfo();
	}
	
	public void setForcesLaws(String gId, JSONObject info) {
		ps.setForceLaws(gId, ffl.createInstance(info));
	}
	
	public void run(int n) {
		for(int i=0; i<n; i++) ps.advance();
	}
}
