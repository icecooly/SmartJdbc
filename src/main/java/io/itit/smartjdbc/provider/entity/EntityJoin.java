package io.itit.smartjdbc.provider.entity;

/**
 * 
 * @author skydu
 *
 */
public class EntityJoin {

	public Join join;
	
	public Entity entity;
	
	public EntityJoin(Join join, Entity entity) {
		this.join=join;
		this.entity=entity;
	}
	
}
