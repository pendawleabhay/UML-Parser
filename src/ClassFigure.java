

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ClassFigure {
	private String classfigurename;
	private List<FieldDeclaration> variablelist;
	private List<MethodDeclaration> methodlist;
	private List<ConstructorDeclaration> constructorlist;
	private boolean interfaceflag;
	/*private int modifier;
	
	
	public int getModifier() {
		return modifier;
	}

	public void setModifier(int modifier) {
		this.modifier = modifier;
	}
	
*/

	public ClassFigure(){
		this.variablelist = new ArrayList<FieldDeclaration>(0);
		this.methodlist = new ArrayList<MethodDeclaration>(0);
		this.constructorlist = new ArrayList<ConstructorDeclaration>(0);
	}
	
	public boolean isInterfaceflag() {
		return interfaceflag;
	}

	public void setInterfaceflag(boolean b) {
		this.interfaceflag = b;
	}	
	
	public List<ConstructorDeclaration> getConstructorlist() {
		return constructorlist;
	}

	public void setConstructorlist(List<ConstructorDeclaration> constructorlist) {
		this.constructorlist = constructorlist;
	}

	public String getClassfigurename() {
		return classfigurename;
	}
	public void setClassfigurename(String s) {
		this.classfigurename = s;
	}
	public List<FieldDeclaration> getVariablelist() {
		return variablelist;
	}
	public void setVariablelist(List<FieldDeclaration> variablelist) {
		this.variablelist = variablelist;
	}
	public List<MethodDeclaration> getMethodlist() {
		return methodlist;
	}
	public void setMethodlist(List<MethodDeclaration> methodlist) {
		this.methodlist = methodlist;
	}
}
