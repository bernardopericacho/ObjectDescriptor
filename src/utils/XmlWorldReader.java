package utils;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XmlWorldReader {


	public XmlWorldReader(){
		
	}
	public Pair<ArrayList<Objeto>,Pair<ArrayList<String>,ArrayList<ArrayList<Objeto>>>> readFile(String path){

		try{
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("objeto");
			
			//Two types of saving the world
			ArrayList<Objeto> world =  new ArrayList<Objeto>();
			ArrayList<ArrayList<Objeto>> worldByType=  new ArrayList<ArrayList<Objeto>>();
			ArrayList<String> types = new ArrayList<String>();
						
			Objeto objeto;
			String tipo;
			Element eElement;
			NodeList allFeatures;
			String featureName;
			String[] values;
			float[] coordVector;
			Node node;
			int indexType;
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					
					eElement = (Element) nNode;
					tipo = 	eElement.getAttribute("tipo").toString().toLowerCase();
					
					if (!types.contains(tipo)){
						types.add(tipo);
						indexType = types.size()-1;
						worldByType.add(new ArrayList<Objeto>());
					}else
						indexType=types.indexOf(tipo);
					
					objeto =  new Objeto(Integer.parseInt(eElement.getAttribute("id")),eElement.getAttribute("tipo").toString().toLowerCase());
					
					allFeatures = eElement.getElementsByTagName("*");
					
					if (allFeatures!=null){

						for (int i=0;i<allFeatures.getLength();i++){
							node = allFeatures.item(i);
							featureName = node.getNodeName().toString().toLowerCase();
							if (featureName.equals("tamano")){
									values=node.getTextContent().toString().split(",");
									coordVector =  new float[3];
									for(int j=0;j<3;j++)
										coordVector[j]=Float.parseFloat(values[j]);
									objeto.setTamano(coordVector);
							}else if (featureName.equals("coordenadas")){
								values=node.getTextContent().toString().split(",");
								coordVector =  new float[3];
								for(int j=0;j<3;j++)
									coordVector[j]=Float.parseFloat(values[j]);
								objeto.setPosicion(coordVector);
							}else{
								objeto.setFeature(featureName, node.getTextContent().toString().toLowerCase());
							}
						}
							
					}
					
					world.add(objeto);
					worldByType.get(indexType).add(objeto);
					
				}
				
			}
			Pair<ArrayList<String>,ArrayList<ArrayList<Objeto>>> worldTyped = new Pair <ArrayList<String>,ArrayList<ArrayList<Objeto>>>(types, worldByType);
			return new Pair<ArrayList<Objeto>,Pair<ArrayList<String>,ArrayList<ArrayList<Objeto>>>>(world, worldTyped);
			
			
		}catch (Exception e){
			
			e.printStackTrace();
			System.out.println("Failed to read world from "+path);
			return null;
			
		}


	}
	

}
