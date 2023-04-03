package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowardsFixedPointBuilder extends Builder<ForceLaws> {
	public MovingTowardsFixedPointBuilder() {
		super("mtfp", "Moving towards a fixed point");
	}

	@Override
	protected MovingTowardsFixedPoint createInstance(JSONObject data) { // "c" y "g" son opcionales
		
		Vector2D c = new Vector2D();
		double g = 9.81;
		
		// Si nos dan la "g" (gravedad)
		if (data.has("g")) {
			g = data.getDouble("g");
		}
		
		// Si nos dan la "c" (punto central)
		if (data.has("c")) {
			JSONArray ja = data.getJSONArray("c");
			if (ja.length() != 2) throw new IllegalArgumentException("la posicion no es valida");
			c = new Vector2D(ja.getDouble(0), ja.getDouble(1));
		}
		
		return new MovingTowardsFixedPoint(c, g);
		
	}
	

	@Override
	protected void fillInData(JSONObject data) {
		data.put("c", "the point towards which bodies move (e.g., [100.0,50.0])").put("g", "the length of the acceleration vector (a number)");
	}
}
