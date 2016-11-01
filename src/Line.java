

public class Line {

	private ClassFigure start;
	private ClassFigure end;
	private Enum type;
	private String multiplicity;
	
	public Enum getType() {
		return type;
	}
	public void setType(Enum type) {
		this.type = type;
	}
	public String getMultiplicity() {
		return multiplicity;
	}
	public void setMultiplicity(String s) {
		this.multiplicity = s;
	}
	public ClassFigure getStart() {
		return start;
	}
	public void setStart(ClassFigure c) {
		this.start = c;
	}
	public ClassFigure getEnd() {
		return end;
	}
	public void setEnd(ClassFigure c) {
		this.end = c;
	} 
}
