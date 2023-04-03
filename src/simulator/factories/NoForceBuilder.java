package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder<ForceLaws> {
	public NoForceBuilder() {
		super("nf", "No force");
	}

	@Override
	protected NoForce createInstance(JSONObject data) {
		
		return new NoForce();
	}

	@Override
	protected void fillInData(JSONObject data) {
	}
}