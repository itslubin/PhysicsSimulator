package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class MovingTowardsFixedPoint implements ForceLaws {
	
	private Vector2D c;
	private double g;
	
	public MovingTowardsFixedPoint(Vector2D c, double g) {
		if (c == null || g <= 0) {
			throw new IllegalArgumentException("el vector punto central c es null o la gravedad no es positiva");
		}
		else {
			this.c = c;
			this.g = g;
		}
	}

	@Override
	public void apply(List<Body> bs) {
		Vector2D dist;
		for (Body b : bs) {
			dist = c.minus(b.getPosition());
			b.addForce(dist.direction().scale(g * b.getMass()));
		}
	}
	
	public String toString() {
		return "Moving towards "+ c +" with constant acceleration "+ g;
	}
	
}
