


import java.util.ArrayList;
import java.util.List;

public class Top {

	private List<ClassFigure> classfigurelist;
	private List<Line> linelist;
	
	public Top()
	{
		this.classfigurelist = new ArrayList<ClassFigure>(0);
		this.linelist = new ArrayList<Line>(0);
	}

	public List<ClassFigure> getClassfigurelist() {
		return classfigurelist;
	}

	public void setClassfigurelist(List<ClassFigure> classFigurelist) {
		this.classfigurelist = classFigurelist;
	}

	public List<Line> getLinelist() {
		return linelist;
	}

	public void setLinelist(List<Line> line) {
		this.linelist = line;
	}
	
	public ClassFigure getClassFigure(String s)
	{
		for (ClassFigure currentclassFigure : classfigurelist) 
		{
			if(currentclassFigure.getClassfigurename().equals(s))
			{
				return currentclassFigure;
			}
		}
		return null;
	}
	
	public boolean isLinePresent(String source, String destination, LineEnum type)
	{
		for (Line currentline : linelist)
		{
			if(currentline.getStart().getClassfigurename().equals(source) 
				&& currentline.getEnd().getClassfigurename().equals(destination)
				&& currentline.getType().equals(type))
			{
				return true;
			}
		}
		
		return false;
		
	}
	
}
