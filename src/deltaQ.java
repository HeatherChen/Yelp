
public class deltaQ implements Comparable<deltaQ>{
	public deltaQ(){
		super();
	}
	
	public deltaQ(User i, User j, double dQ){
		this.i = i;
		this.j = j;
		this.dQ = dQ;
	}
	
	private User i;
	private User j;
	private double dQ;
	
	
	public User getI() {
		return i;
	}

	public User getJ() {
		return j;
	}

	public double getdQ() {
		return dQ;
	}

	
	@Override
	public int compareTo(deltaQ compareDQ){
		if (this.dQ > compareDQ.dQ)
			return 1;
		else if (this.dQ < compareDQ.dQ)
			return -1;
		else 
			return 0;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof deltaQ))
			return false;
		deltaQ another = (deltaQ)obj;
		if (another.i.u_id.equals(this.i.u_id) && another.j.u_id.equals(this.j.u_id) && another.dQ == this.dQ)
			return true;
		else
			return false;
	}
}
