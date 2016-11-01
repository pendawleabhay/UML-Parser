

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main {

	public static void main(String[] args) throws ParseException, IOException {
		String sourceFolder = args[0];
		String outputImage = args[1];
		StringOutput uml = new StringOutput();
			File folder = new File(sourceFolder);			

			//get list of java files
			List<File> filelist = new ArrayList<File>();
			File[] getfiles = folder.listFiles();
			for(File file : getfiles)
			{
				if(file.isFile() && file.getName().endsWith(".java"))
				{
					filelist.add(file);
				}
			}
			
			
			CompilationUnit compileUnit = new CompilationUnit();
			Top collection = new Top();
			HashMap<String, CompilationUnit> compilations = new HashMap<String, CompilationUnit>();
			
			//fill top
			for(File file : filelist)
			{
				compileUnit = JavaParser.parse(file);
				compilations.put(file.getName(), compileUnit);
				List<TypeDeclaration> list = compileUnit.getTypes();
				Iterator<TypeDeclaration> itrate = list.iterator();
				while (itrate.hasNext())
				{
					TypeDeclaration element = itrate.next();
					ClassFigure classFigure = new ClassFigure();
					if(((ClassOrInterfaceDeclaration) element).isInterface())
					{
						String className = element.getName();
						classFigure.setClassfigurename(className);
						classFigure.setInterfaceflag(true);
					}
					else
					{
						String className = element.getName();
						classFigure.setClassfigurename(className);
					}
					List<BodyDeclaration> bodylist = element.getMembers();
					for (BodyDeclaration currentbodylist : bodylist) 
					{
						if (currentbodylist instanceof FieldDeclaration ) 
						{
							if(((FieldDeclaration)currentbodylist).getType() instanceof PrimitiveType
								|| ((FieldDeclaration)currentbodylist).getType().toString().equals("String")
								|| ((FieldDeclaration)currentbodylist).getType().toString().equals("int[]"))
							{
								classFigure.getVariablelist().add((FieldDeclaration) currentbodylist);
							}
						} 
						else if (currentbodylist instanceof MethodDeclaration)
						{
							classFigure.getMethodlist().add((MethodDeclaration) currentbodylist);
						}
						else if(currentbodylist instanceof ConstructorDeclaration)
						{
							classFigure.getConstructorlist().add((ConstructorDeclaration) currentbodylist);
						}
					}
					collection.getClassfigurelist().add(classFigure);
				}
				
			}
			
			//fill classfigure
			for(File file :filelist)
			{
				compileUnit = JavaParser.parse(file);
				List<TypeDeclaration> list = compileUnit.getTypes();
				Iterator<TypeDeclaration> itrate = list.iterator();
				while (itrate.hasNext())
				{
					
					TypeDeclaration element = itrate.next();
					List<ClassOrInterfaceType> extendsList	=((ClassOrInterfaceDeclaration) element).getExtends();
					List<ClassOrInterfaceType> implementsList	=((ClassOrInterfaceDeclaration) element).getImplements();
				
					if(extendsList != null)
					{
						for (ClassOrInterfaceType extendedClassName : extendsList) 
						{
							Line line =  new Line();
							String name = element.getName();
							ClassFigure source = collection.getClassFigure(name);
							line.setStart(source);
							String str = extendedClassName.getName();
							ClassFigure destination = collection.getClassFigure(str);
							line.setEnd(destination);
							line.setType(LineEnum.EXTENDS);
							collection.getLinelist().add(line);
						}
					}
					if(implementsList!=null)
					{
						for (ClassOrInterfaceType implementClassName : implementsList) 
						{
							Line line =  new Line();
							String name = element.getName();
							ClassFigure source = collection.getClassFigure(name);
							line.setStart(source);
							String str = implementClassName.getName();
							ClassFigure destination = collection.getClassFigure(str);
							line.setEnd(destination);
							line.setType(LineEnum.IMPLEMENTS);
							collection.getLinelist().add(line);
						}
					}
					
				}
				
			}			
			
			//fill line
			for (File file : filelist) 
			{
				compileUnit = compilations.get(file.getName());
				List<TypeDeclaration> list = compileUnit.getTypes();
				for (TypeDeclaration element : list)
				{
					if(!((ClassOrInterfaceDeclaration)element).isInterface())
					{
					List<BodyDeclaration> bodydeclarationlist = element.getMembers();
					for (BodyDeclaration currentbodylist : bodydeclarationlist)
					{
						if (currentbodylist instanceof MethodDeclaration)
						{
							if(((MethodDeclaration) currentbodylist).getName().contains("main"))
							{
								Line mainline = new Line();
								mainline.setType(LineEnum.DEPENDENCY);
								String className = "Tester";
								ClassFigure start  = collection.getClassFigure(className);
								String className1 = "Component";
								ClassFigure end  = collection.getClassFigure(className1);
								mainline.setStart(start);
								mainline.setEnd(end);
								collection.getLinelist().add(mainline);
							}
							List<Parameter> parameterlist = ((MethodDeclaration) currentbodylist).getParameters();
							if(parameterlist!= null)
							{
								for (Parameter currentparameter : parameterlist) 
								{	
									if(currentparameter.getType()!=null)
									{
										if(checkReferenceType(currentparameter.getType()))
										{
											Line line = new Line();
											line.setType(LineEnum.DEPENDENCY);
											String className = element.getName();
											ClassFigure start = collection.getClassFigure(className);
											line.setStart(start);
											String str = currentparameter.getType().toString();
											ClassFigure end = collection.getClassFigure(str);
											line.setEnd(end);
											boolean status = collection.isLinePresent(start.getClassfigurename(),end.getClassfigurename(),LineEnum.DEPENDENCY);
											if(status ==false)
											{
												collection.getLinelist().add(line);
											}
										}
									}
								}
							}
						}
						else if(currentbodylist instanceof FieldDeclaration)
						{
							boolean flag = checkReferenceType(((FieldDeclaration)currentbodylist).getType());
							if(flag)
							{
								
								Line line = new Line();
								line.setType(LineEnum.ASSOCIATION);
								String className = element.getName();
								ClassFigure start = collection.getClassFigure(className);
								line.setStart(start);
								String str = ((FieldDeclaration) currentbodylist).getType().toString();
								if(str.contains("Collection"))
								{
									line.setMultiplicity("*");
									str=str.replace("Collection", "");
									str=str.replace("<", "");
									str=str.replace(">", "");
								}
								ClassFigure destination = collection.getClassFigure(str);
								line.setEnd(destination);
								boolean status = collection.isLinePresent(destination.getClassfigurename(),start.getClassfigurename(),LineEnum.ASSOCIATION);
								if(status ==false)
								{
									collection.getLinelist().add(line);
								}
							}
							
						}
						else if(currentbodylist instanceof ConstructorDeclaration)
						{

							List<Parameter> parameterlist = ((ConstructorDeclaration) currentbodylist).getParameters();
							if(parameterlist!= null)
							{
								for (Parameter currentparameter : parameterlist) 
								{	
									if(currentparameter.getType()!=null)
									{
										if(checkReferenceType(currentparameter.getType()))
										{
											Line line = new Line();
											String str = currentparameter.getType().toString();
											ClassFigure destination = collection.getClassFigure(str);
											if(destination.isInterfaceflag())
											{
												line.setType(LineEnum.DEPENDENCY);
												String className = element.getName();
												ClassFigure source = collection.getClassFigure(className);
												line.setStart(source);
												line.setEnd(destination);
												boolean status = collection.isLinePresent(source.getClassfigurename(),destination.getClassfigurename(),LineEnum.DEPENDENCY);
												if(status ==false)
												{
													collection.getLinelist().add(line);
												
												}
											}
										}
									}
								}
							}
						
						}
					}
					
					}
				}

			}
			
			Top cu = collection;
			uml.generateString(cu,folder, outputImage);
			
			
		//}
		
	}
	
	public static boolean checkReferenceType(Type type) 
	{
		if( type instanceof ReferenceType)
		{
			if( ! type.toString().equals("String"))
			{
				if( ! type.toString().equals("String[]") && (! type.toString().equals("int[]")))
				{
					return true;
				}
			}
		}
		return false;
	}
}
