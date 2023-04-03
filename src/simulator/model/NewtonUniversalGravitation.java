package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws {
	
	private double G;
	
	public NewtonUniversalGravitation(double G) {
		if (G <= 0) {
			throw new IllegalArgumentException("el valor de la constante G no es positiva");
		}
		else {
			this.G = G;
		}
	}
	@Override
	public void apply(List<Body> bs) {
		Vector2D f;
		double dist;
		for(Body b1 : bs) {
			for (Body b2 : bs) {
				if (b1 != b2) {
					dist = b2.getPosition().distanceTo((b1.getPosition()));
					Vector2D dir = b2.getPosition().minus(b1.getPosition()).direction();
					double fuerza = (G * b1.getMass() * b2.getMass()) / (dist*dist);
					f = dir.scale(fuerza);
					b1.addForce(f);
				}
			}
		}
	}
	
	public String toString() {
		return "Newton’s Universal Gravitation with G="+ G;
	}
	
}
