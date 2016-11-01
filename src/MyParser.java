import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

public class MyParser {
	public static void main(String[] args) throws Exception {
		
		File folder = new File("..\\input\\uml-parser-test-4");
		File[] files = folder.listFiles();
		Top topobj = new Top();
		HashMap<String, CompilationUnit> compilations = new HashMap<String, CompilationUnit>();
		
		for (File currentfile : files) {
			if (currentfile.getName().endsWith(".java")) {
				FileInputStream in = new FileInputStream(currentfile);
				CompilationUnit compilationunit;
				compilationunit = JavaParser.parse(in);
				compilations.put(currentfile.getName(), compilationunit);
				List<TypeDeclaration> classes = compilationunit.getTypes();
				
				//set classfigure list
				for (TypeDeclaration currentclass : classes) {
					Classfigure classfigureobj = new Classfigure();

					List<BodyDeclaration> body = currentclass.getMembers();
					for (BodyDeclaration currentbody : body) {
						if (currentbody instanceof FieldDeclaration) {
							classfigureobj.getVariable_names().add((FieldDeclaration) currentbody);
						} else if (currentbody instanceof MethodDeclaration) {
							classfigureobj.getMethod_names().add((MethodDeclaration) currentbody);
						} else if (currentbody instanceof ConstructorDeclaration) {
							classfigureobj.getConstruct_names().add((ConstructorDeclaration) currentbody);
						}

					}
					classfigureobj.setClassname(currentclass.getName());
					if ((boolean) ((ClassOrInterfaceDeclaration) currentclass).isInterface()) {
						classfigureobj.setInterface_flag(true);
					}
					topobj.getclassfigurelist().add(classfigureobj);

				}
				in.close();
			}
		}
		createline(compilations, files, topobj);
		StringGenerator gn = new StringGenerator();
		gn.outputstring(topobj);

	}

	private static void createline( HashMap<String,CompilationUnit> compilations, File[] files, Top st) {
		for (File file : files) {
			if (file.getName().endsWith(".java")) {
				CompilationUnit cu = compilations.get(file.getName());
				List<TypeDeclaration> types = cu.getTypes();
				
				
				for (TypeDeclaration val : types) {
					
					// generalization
					List<ClassOrInterfaceType> extendslist = ((ClassOrInterfaceDeclaration) val).getExtends();
					if (extendslist != null) {
						for (ClassOrInterfaceType ext : extendslist) {
							Line lineobj = new Line();
							Classfigure destination = st.getclassfigure(ext.getName());
						//	System.out.println(val.getName());
							Classfigure source = st.getclassfigure(val.getName());
							// System.out.println(destination);
							lineobj.setSource(source);
							lineobj.setDestination(destination);
							lineobj.setRm(Relationenum.GENERALIZATION);
							st.getlinelist().add(lineobj);

						}
					}
					
					//realization
					List<ClassOrInterfaceType> implementslist = ((ClassOrInterfaceDeclaration) val).getImplements();
					if (implementslist != null) {
						for (ClassOrInterfaceType ext : implementslist) {
							Line rel = new Line();
							Classfigure destination = st.getclassfigure(ext.getName());
							Classfigure source = st.getclassfigure(val.getName());
							rel.setSource(source);
							rel.setDestination(destination);
							rel.setRm(Relationenum.REALIZATION);
							st.getlinelist().add(rel);

						}
						
					}
					
					//dependency
					List<BodyDeclaration> members = val.getMembers();
					for (BodyDeclaration  memb : members)
					{
						if ( memb instanceof MethodDeclaration)
						{
						List<Parameter> parameters =((MethodDeclaration) memb).getParameters();
						if (parameters != null)
						{
							for (Parameter parameter : parameters)
							{
							 if(isRefType(parameter.getType()))
							 {
								 Line rel = new Line();
								 String name = parameter.getType().toString();
								 if(name.contains("Collection"))
								 {
										name = name.replace("Collection", "");
										name = name.replace("<", "");
										name = name.replace(">", "");
								}
									Classfigure destination = st.getclassfigure(name);
									Classfigure source = st.getclassfigure(val.getName());
									if(!st.islinepresent(source.getClassname(), destination.getClassname(), Relationenum.DEPENDENCY)){
										rel.setSource(source);
										rel.setDestination(destination);
										rel.setRm(Relationenum.DEPENDENCY);
										st.getlinelist().add(rel); 
									}
									
							       // System.out.println(source);
									//System.out.println(destination);
								 
							 }
							}
						}
						}
						
						
						//Association
						if(memb instanceof FieldDeclaration)
						{
							if(memb != null)
							{
								if(isRefType(((FieldDeclaration) memb).getType()))
								{
									Line rel=new Line();
									String name = ((FieldDeclaration) memb).getType().toString();
									 if(name.contains("Collection"))
									 {
											name = name.replace("Collection", "");
											name = name.replace("<", "");
											name = name.replace(">", "");
											rel.setMultiplicity("*");
									}
									Classfigure destination = st.getclassfigure(name);
									Classfigure source = st.getclassfigure(val.getName());
									if(!st.islinepresent(source.getClassname(), destination.getClassname(), Relationenum.ASSOCIATION)){
										rel.setSource(source);
										rel.setDestination(destination);
										rel.setRm(Relationenum.ASSOCIATION);
										st.getlinelist().add(rel); 
									}
								}
							}
						}
						
					}
					
				} 
				
			}
		}
		
}
	
	private static boolean isRefType(Type type)
	{
	if ((type instanceof ReferenceType) && !(type.toString().equals("String")) && !(type.toString().equals("String []")) && !(type.toString().equals("int []")))
	{
		return true;
	}
      return false;
	}
}



