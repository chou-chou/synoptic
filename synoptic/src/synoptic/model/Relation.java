package synoptic.model;

/**
 * Represents named and anonymous relations
 * 
 * @author timjv
 *
 */
public class Relation {
	
	public static final String ANONYMOUS = "";
	
	private String relation;
	private String name;
	private boolean isClosure;
	
	public Relation(String relation) {
		this(ANONYMOUS, relation, false);
	}
	
	public Relation(String relation, boolean isClosure) {
		this(ANONYMOUS, relation, isClosure);
	}
	
	public Relation(String name, String relation, boolean isClosure) {
		this.name = name;
		this.relation = relation;
		this.isClosure = isClosure;
	}
	
	public boolean isAnonymous() {
		return name.equals(ANONYMOUS);
	}
	
	public boolean isClosure() {
		return isClosure;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRelation() {
		return relation;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Relation)) {
			return false;
		}
		Relation oRelation = (Relation) o;
		return name.equals(oRelation.getName()) && 
				relation.equals(oRelation.getRelation()) &&
				isClosure == oRelation.isClosure();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + relation.hashCode();
		result = prime * result + name.hashCode();
		if (isClosure) {
			result = prime * result + prime;
		}
		return result;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if (!name.equals(ANONYMOUS)) {
			result += " : ";
		}
		
		result += relation;
		
		if (isClosure()) {
			result += "*";
		}
		
		return result;
	}
}
