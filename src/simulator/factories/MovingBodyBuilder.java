package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.MovingBody;

public class MovingBodyBuilder extends Builder<Body> {
	
	public MovingBodyBuilder() {
		super("mv_body", "Moving body");
	}

	@Override
	protected MovingBody createInstance(JSONObject data) {
		if (!data.has("id") || !data.has("gid") || !data.has("p") || !data.has("v") || !data.has("m")) {
			throw new IllegalArgumentException("el JSONObject data no tiene la clave de id, gid, p, v o m");
		}
		
		String id = data.getString("id");
		String gid = data.getString("gid");
		JSONArray p = data.getJSONArray("p");
		JSONArray v = data.getJSONArray("v");
		
		if (p.length() != 2 || v.length() != 2) throw new IllegalArgumentException("la posicion no es valida");
		
		double m = data.getDouble("m");
		
		return new MovingBody(id, gid, new Vector2D(p.getDouble(0), p.getDouble(1)), new Vector2D(v.getDouble(0), v.getDouble(1)), m);
	}
	
}
