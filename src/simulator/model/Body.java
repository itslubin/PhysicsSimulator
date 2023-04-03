package simulator.model;

import org.json.JSONObject;

import simulator.misc.Vector2D;

public abstract class Body {
	protected String id;
	protected String gid;
	protected double mass;
	protected Vector2D vel;
	protected Vector2D pos;
	protected Vector2D force;
	
	public Body(String id, String gid, Vector2D pos, Vector2D vel, double mass) {
		if (id == null || gid == null || id.trim().length() == 0 || gid.trim().length() == 0 || vel == null || pos == null || mass <= 0) {
			throw new IllegalArgumentException("algun parametro es null o el id/gid esta vacio o la masa no es positiva");
		}
		this.id = id;
		this.gid = gid;
		this.vel = vel;
		this.pos = pos;
		this.mass = mass;
		this.force = new Vector2D();
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getgId() {
		return this.gid;
	}
	
	public Vector2D getVelocity() {
		return this.vel;
	}
	
	public Vector2D getForce() {
		return this.force;
	}
	
	public Vector2D getPosition() {
		return this.pos;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	void addForce(Vector2D f) {
		this.force = force.plus(f);
	}
	
	void resetForce() {
		this.force = new Vector2D();
	}
	
	abstract void advance(double dt);
	
	public JSONObject getState() {
		JSONObject jo = new JSONObject();
		jo.put("id", this.id);
		jo.put("m", this.mass);
		
		jo.put("v", this.vel.asJSONArray());
		
		jo.put("p", this.pos.asJSONArray());
		
		jo.put("f", this.force.asJSONArray());
		
		return jo;
	}
	
	public String toString() {
		return getState().toString();
	}
	
	public boolean equals(Body b) {
		return id == b.id;
	}
	
}
