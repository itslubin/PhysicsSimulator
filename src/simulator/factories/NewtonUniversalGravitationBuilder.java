package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder<ForceLaws>{
	public NewtonUniversalGravitationBuilder() {
		super("nlug", "Newton's law of universal gravitation");
	}

	@Override
	protected NewtonUniversalGravitation createInstance(JSONObject data) {
		double g = 6.67E-11;
		
		if (data.has("G")) {
			g = data.getDouble("G");
		}
		
		return new NewtonUniversalGravitation(g);
	}
	

	@Override
	protected void fillInData(JSONObject data) {
		data.put("G", "the gravitational constant (a number)");
	}
}
