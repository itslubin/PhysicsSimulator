package simulator.model;

import simulator.misc.Vector2D;

public class MovingBody extends Body{

	public MovingBody(String id, String gid, Vector2D pos, Vector2D vel, double mass) {
		super(id, gid, pos, vel, mass);
	}

	@Override
	void advance(double dt) {
		Vector2D ac;
		
		if (mass == 0) {
			ac = new Vector2D();
		}
		
		else {
			ac = force.scale(1.0/mass);
		}
		
		this.pos = pos.plus(vel.scale(dt)).plus(ac.scale((dt*dt)/2));
		this.vel = vel.plus(ac.scale(dt));
	}

}
