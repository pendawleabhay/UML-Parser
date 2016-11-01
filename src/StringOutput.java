

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class StringOutput {
		
	public void generateString(Top cu, File folder, String outputImageName) throws IOException
	{
		
		String output = "@startuml\n";
		List<ClassFigure> classFigures = cu.getClassfigurelist();
		Iterator<ClassFigure> iterator = classFigures.iterator();
		
		//print class
		while(iterator.hasNext())
		{
			ClassFigure currentclassFigure = iterator.next();
			List<MethodDeclaration> methodlist = currentclassFigure.getMethodlist();

			if(currentclassFigure.isInterfaceflag())
			{
				output = output + "interface "+currentclassFigure.getClassfigurename()+" << interface >> {\n";
			}
			else
			{
				output = output + "class "+currentclassFigure.getClassfigurename()+" {\n";
			}
			
			List<FieldDeclaration> variablelist = currentclassFigure.getVariablelist();
			List<MethodDeclaration> gettersetterlist = new ArrayList<MethodDeclaration>();
			
		
			
			
			//convrting getter setter variablt to public
			for (MethodDeclaration method : methodlist)
			{
				for(FieldDeclaration variable : variablelist)
				{
					String getter = "get"+variable.getVariables().get(0).toString();
					String setter = "set"+variable.getVariables().get(0).toString();
					if( (method.getName().equalsIgnoreCase(setter) || method.getName().equalsIgnoreCase(getter)))
					{
						gettersetterlist.add(method);
						variable.setModifiers(1);
					}
				}
			}
			
			for (MethodDeclaration temp : gettersetterlist) {
				methodlist.remove(temp);
			}
			
			
			//print constructor
			List<ConstructorDeclaration>constructorList = currentclassFigure.getConstructorlist();
			for (ConstructorDeclaration constructor : constructorList) 
			{
				List<Parameter>parameters = constructor.getParameters();
				if(parameters!= null)
				{	
					for (Parameter parameter : parameters) 
					{
						if(parameter.getType()!=null)
						{
							output += "+"+constructor.getName()+"("+
									parameter.getId().toString()+" : "+parameter.getType()+")\n";
						}
					}
				}
				else
				{
					output += "+"+constructor.getName()+"()\n";

				}
			}
			
			//print methods
			for (MethodDeclaration method : methodlist)
			{
				int modifiernumber = method.getModifiers();
				String modifiername = Modifier.toString(modifiernumber);
				if(modifiername.equals("public"))
				{
					List<Parameter>parameters = method.getParameters();
					if(parameters!= null)
					{	
						for (Parameter parameter : parameters) 
						{
							if(parameter.getType()!=null)
							{
								output = output + "+" + method.getName() + "(" + parameter.getId().toString()+" : "+parameter.getType() +")" + " : " + method.getType() + "\n";
							}
						}
			
					}
					
					else
					{
						output = output + "+" + method.getName() + "()" + " : " + method.getType() + "\n";
					}
					
				}
				
				
				else if(modifiernumber !=1 && modifiernumber != 1025 && modifiernumber == 9)
				{
					List<Parameter> parameterlist=method.getParameters();
					if(parameterlist != null )
					{
						for (Parameter currentparameter : parameterlist)
						{
							if(currentparameter.getType()!=null)
							{
								output += "{static}+" + method.getName()+ "(" +	currentparameter.getId().toString() + " : " + currentparameter.getType() + ")" +":"+ method.getType() + "\n";
							}
						}
					}
					
				}
				else if(modifiernumber == 1025)
				{
					output = output + "+" + method.getName() + "()" + " : " + method.getType() + "\n";
				}
				
			}//
			
			//print variables
			for (FieldDeclaration currentvariable : variablelist)
			{
				int modifiernumber = currentvariable.getModifiers();
				String modifiername = Modifier.toString(modifiernumber);
				if(modifiername.equals("public"))
				{
					output = output + "+"+currentvariable.getVariables()+ " : "+currentvariable.getType()+"\n";
				}
				else if(modifiername.equals("private"))
				{
					output = output + "-"+currentvariable.getVariables()+ " : "+currentvariable.getType()+"\n";
				}	
				
			}
			
			output +="}\n";
		}
		
		
		
		//print lines
		Iterator<Line> iterator1 = cu.getLinelist().iterator();
		while(iterator1.hasNext())
		{
			Line line = iterator1.next();
			
			if(line.getType() != null)
			{
			
				if(line.getType().toString().equals("EXTENDS"))
				{
					output += line.getEnd().getClassfigurename()+" <|-- "+line.getStart().getClassfigurename()+"\n";
				}
				else if(line.getType().toString().equals("IMPLEMENTS"))
				{
					output += line.getEnd().getClassfigurename()+" <|.. "+line.getStart().getClassfigurename()+"\n";
				}
				else if(line.getType().toString().equals("ASSOCIATION"))
				{
					if(line.getMultiplicity() !=null)
					{
						output += line.getEnd().getClassfigurename()+" \""+line.getMultiplicity()+"\""+" -- "+"\"1\" "+line.getStart().getClassfigurename()+"\n";
					}
					else
					{
						output += line.getEnd().getClassfigurename()+" -- "+line.getStart().getClassfigurename()+"\n";
					}
				}
				else if(line.getType().toString().equals("DEPENDENCY"))
				{
					if(line.getMultiplicity() !=null)
					{
						output += line.getStart().getClassfigurename() +" ..> "+line.getEnd().getClassfigurename()+"\n";
					}
					else
					{
						output += line.getStart().getClassfigurename() +" ..> "+line.getEnd().getClassfigurename()+"\n";

					}
				}
			}
			
		}
		output += "@enduml";
		//System.out.println(output);
		SourceStringReader ssr =  new SourceStringReader(output);
		FileOutputStream fos = new FileOutputStream(outputImageName + ".png");
		ssr.generateImage(fos,  new FileFormatOption(FileFormat.PNG, false));
	}
}
	
	