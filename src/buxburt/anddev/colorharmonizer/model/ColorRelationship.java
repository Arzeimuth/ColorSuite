package buxburt.anddev.colorharmonizer.model;

/**
 * A stub for a color relationship chooser.
 * TODO: Work on the CustomView for this class.
 * @author Brandon Burton
 * Jul 2, 2012
 *
 */
public class ColorRelationship {

	public int icon;
	public String relation;
	public int minimumQuantity;
	public int listPosition;
	
	/**
	 * Accepts the resource ID, relation name, miniumum quantity and position (id) in the list
	 * @param icon
	 * @param relation
	 * @param minimumQuantity
	 * @param listPosition
	 */
	public ColorRelationship(int icon, String relation, int minimumQuantity,
			int listPosition) {
		super();
		this.icon = icon;
		this.relation = relation;
		this.minimumQuantity = minimumQuantity;
		this.listPosition = listPosition;
	}
	
	
	
}
