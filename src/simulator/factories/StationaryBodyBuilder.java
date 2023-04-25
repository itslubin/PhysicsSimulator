package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.StationaryBody;

public class StationaryBodyBuilder extends Builder<Body>{

	public StationaryBodyBuilder() {
		super("st_body", "Stationary Body");
		
	}

	@Override
	protected StationaryBody createInstance(JSONObject data) {
		if (!data.has("id") || !data.has("gid") || !data.has("p") || !data.has("m")) {
			throw new IllegalArgumentException("el JSONObject data no tiene la clave de id, gid, p o m");
		}
		
		String id = data.getString("id");
		String gid = data.getString("gid");
		JSONArray p = data.getJSONArray("p");
		
		if (p.length() != 2) throw new IllegalArgumentException("la posicion no es valida");
		
		double m = data.getDouble("m");
		
		return new StationaryBody(id, gid, new Vector2D(p.getDouble(0), p.getDouble(1)), m);
	}
	
}
