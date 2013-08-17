package utils;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import utils.Lexicalizator.DESCRIPTION_TYPE;
import utils.Lexicalizator.GENERO;

public class Objeto {


	private int id=0;
	private String tipo;

	//posicion y tamaño se tratan de manera diferente
	private float[] posicion;
	private float[] tamano;
	private float volumen;

	//demás características: en arrays y no en hashmap debido a que hay que recorrerlas muchas veces
	private ArrayList<String> keys;
	private ArrayList<String> values;

	//descripciones por etapas
	private Set<Set<String>> basicDescriptions; //1º parte algoritmo
	private ArrayList<Set<ArrayList<String>>> positionalBasedDescriptions; //2º parte algoritmo
	private ArrayList<Set<String>> absoluteDescriptions; 	//3º parte algoritmo
	public ArrayList<String> getLexicalizedBasicDescriptions() {
		return lexicalizedBasicDescriptions;
	}

	public void setLexicalizedBasicDescriptions(
			ArrayList<String> lexicalizedBasicDescriptions) {
		this.lexicalizedBasicDescriptions = lexicalizedBasicDescriptions;
	}

	public ArrayList<ArrayList<String>> getLexicalizedPositionalDescriptions() {
		return lexicalizedPositionalDescriptions;
	}

	public void setLexicalizedPositionalDescriptions(
			ArrayList<ArrayList<String>> lexicalizedPositionalDescriptions) {
		this.lexicalizedPositionalDescriptions = lexicalizedPositionalDescriptions;
	}

	public ArrayList<String> getLexicalizedAbsoluteDescriptions() {
		return lexicalizedAbsoluteDescriptions;
	}

	public void setLexicalizedAbsoluteDescriptions(
			ArrayList<String> lexicalizedAbsoluteDescriptions) {
		this.lexicalizedAbsoluteDescriptions = lexicalizedAbsoluteDescriptions;
	}

	public ArrayList<String> getLexicalizedComparativeDescriptions() {
		return lexicalizedComparativeDescriptions;
	}

	public void setLexicalizedComparativeDescriptions(
			ArrayList<String> lexicalizedComparativeDescriptions) {
		this.lexicalizedComparativeDescriptions = lexicalizedComparativeDescriptions;
	}

	private ArrayList<ArrayList<String>> comparativeBasedDescriptions; //4º parte algoritmo


	//descripciones lexicalizadas
	private ArrayList<String> lexicalizedBasicDescriptions; 
	private ArrayList<ArrayList<String>> lexicalizedPositionalDescriptions; 
	private ArrayList<String> lexicalizedAbsoluteDescriptions; 	
	private ArrayList<String> lexicalizedComparativeDescriptions; 

	private ArrayList<ArrayList<String>> allNonUnivocalDescriptions;


	public enum POSICION {DELANTE,DETRAS,IZQUIERDA,DERECHA,ENCIMA,DEBAJO};

	//localizacion del objeto con respecto a los demás
	private ArrayList<ArrayList<Integer>> posiciones;

	public Objeto(int id,String tipo){
		this.id = id;
		this.tipo = tipo;
		//x,y,x
		posicion = new float[3];
		//ancho,alto,fondo
		tamano= new float[3];
		volumen=0;
		keys= new ArrayList<String>();
		values = new ArrayList<String>();

		//descriptions
		basicDescriptions = new LinkedHashSet<Set<String>>();

		positionalBasedDescriptions=  new ArrayList<Set<ArrayList<String>>>();
		for (int i=POSICION.DELANTE.ordinal();i<=POSICION.DEBAJO.ordinal();i++)
			positionalBasedDescriptions.add(new LinkedHashSet<ArrayList<String>>());
		for (Set<ArrayList<String>> despos : positionalBasedDescriptions)
			despos = new LinkedHashSet<ArrayList<String>>();

		comparativeBasedDescriptions = new ArrayList<ArrayList<String>>();
		absoluteDescriptions = new ArrayList<Set<String>>();


		//lexicalization
		lexicalizedBasicDescriptions = new ArrayList<String>(); 
		lexicalizedPositionalDescriptions = new ArrayList<ArrayList<String>>(); 
		for (int i=POSICION.DELANTE.ordinal();i<=POSICION.DEBAJO.ordinal();i++)
			lexicalizedPositionalDescriptions.add(new ArrayList<String>());

		lexicalizedAbsoluteDescriptions = new ArrayList<String>(); 	
		lexicalizedComparativeDescriptions = new ArrayList<String>();

		//non univocal
		allNonUnivocalDescriptions = new ArrayList<ArrayList<String>>();

		//position
		posiciones=  new ArrayList<ArrayList<Integer>>();
		for (int i=POSICION.DELANTE.ordinal();i<=POSICION.DEBAJO.ordinal();i++)
			posiciones.add(new ArrayList<Integer>());
		/*for (ArrayList<Integer> posicion : posiciones)
			posicion = new ArrayList<Integer>();*/
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String type) {
		tipo = type;
	}

	public float[] getPosicion() {
		return posicion;
	}

	public void setPosicion(float[] position) {
		posicion =  position;
	}

	public float[] getTamano() {
		return tamano;
	}

	public void setTamano(float[] size) {
		tamano =  size;

		if (tipo.equals("cubo"))
			volumen=tamano[0]*tamano[1]*tamano[2];
		else if (tipo.equals("cono"))
			volumen=(float) ((Math.PI*Math.pow(tamano[0]/2,2))*tamano[1])/3;
		else if (tipo.equals("esfera"))
			volumen=(float) (4*Math.pow(tamano[0],3)*Math.PI/3);
		else if (tipo.equals("piramide"))
			volumen=(tamano[0]*tamano[2]*tamano[1])/3;
		else if (tipo.equals("cilindro"))
			volumen=(float) (Math.PI*Math.pow(tamano[0], 2)*tamano[1]);
	}

	public float getVolumen(){
		return volumen;
	}

	public void setBasicDescriptions(Set<Set<String>> descrip){
		basicDescriptions= descrip;
	}

	public void setPositionalBasedDescriptions(LinkedHashSet<ArrayList<String>> descrip,POSICION p){
		Set<ArrayList<String>> pointerToCurrentDescr = positionalBasedDescriptions.get(p.ordinal());
		pointerToCurrentDescr = descrip;
	}

	public void setComparativeBasedDescriptions(ArrayList<ArrayList<String>> descrip){
		comparativeBasedDescriptions= descrip;
	}

	public void setAbsoluteDescriptions(ArrayList<Set<String>> descrip){
		absoluteDescriptions= descrip;
	}

	public String getFeature(String key) {
		int index =  indexByKey(key);
		if (index!=-1)
			return values.get(index);
		else
			return null;

	}

	public ArrayList<String> getAllValues() {
		return values;
	}

	public void setFeature(String feature,String value) {
		keys.add(feature);
		values.add(value);
	}

	public Set<Set<String>> getBasicDescriptions(){
		return basicDescriptions;
	}

	public Set<ArrayList<String>> getPositionalBasedDescriptions(POSICION p){
		return positionalBasedDescriptions.get(p.ordinal());
	}

	public ArrayList<ArrayList<String>> getComparativeBasedDescriptions(){
		return comparativeBasedDescriptions;
	}

	public ArrayList<Set<String>> getAbsoluteDescriptions(){
		return absoluteDescriptions;
	}

	public ArrayList<ArrayList<String>> getAllNonUnivocalDescriptions(){
		return allNonUnivocalDescriptions;
	}

	public void addNonPositionalBasedDescription(Set<String> description){
		basicDescriptions.add(description);
	}

	public void addPositionalBasedDescription(ArrayList<String> description,POSICION p){
		positionalBasedDescriptions.get(p.ordinal()).add(description);
	}

	public boolean addComparativeBasedDescription(ArrayList<String> description){
		return comparativeBasedDescriptions.add(description);
	}

	public void addAbsoluteDescription(Set<String> description){
		absoluteDescriptions.add(description);
	}

	public void lexicalize(){
		String lexDesc;
		Pair<String,GENERO> currentObject = null;

		//Basic Descriptions
		for (Set<String> d: basicDescriptions){
			lexDesc= "";

			for (String word: d){
				if (Lexicalizator.getInstance().isObject(word)){
					currentObject = Lexicalizator.getInstance().lexicalizeObject(word);
					lexDesc+=currentObject.getFirst()+" ";
				}else
					lexDesc+=Lexicalizator.getInstance().lexicalizeWord(new Pair<String,DESCRIPTION_TYPE>(word,DESCRIPTION_TYPE.BASIC), currentObject.getSecond()) + " ";

			}
			lexDesc = lexDesc.replaceAll("de el", "del");
			lexicalizedBasicDescriptions.add(lexDesc);
			System.out.println(lexDesc);
		}


		//Absolute Descriptions
		for (Set<String> d: absoluteDescriptions){
			lexDesc= "";

			for (String word: d){
				if (Lexicalizator.getInstance().isObject(word)){
					currentObject = Lexicalizator.getInstance().lexicalizeObject(word);
					lexDesc+=currentObject.getFirst()+" ";
				}else
					lexDesc+=Lexicalizator.getInstance().lexicalizeWord(new Pair<String,DESCRIPTION_TYPE>(word,DESCRIPTION_TYPE.ABSOLUTE), currentObject.getSecond()) + " ";

			}
			lexDesc = lexDesc.replaceAll("de el", "del");
			lexicalizedAbsoluteDescriptions.add(lexDesc);
			System.out.println(lexDesc);
		}
		//Positionals Descriptions
		for (Set<ArrayList<String>> allDescByOnePos: positionalBasedDescriptions){

			for (ArrayList<String> d : allDescByOnePos){
				lexDesc= "";
				for (String word: d){
					if (Lexicalizator.getInstance().isObject(word)){
						currentObject = Lexicalizator.getInstance().lexicalizeObject(word);
						lexDesc+=currentObject.getFirst()+" ";
					}else
						lexDesc+=Lexicalizator.getInstance().lexicalizeWord(new Pair<String,DESCRIPTION_TYPE>(word,DESCRIPTION_TYPE.POSITIONAL), currentObject.getSecond()) + " ";
				}
				lexDesc = lexDesc.replaceAll("de el", "del");
				lexicalizedAbsoluteDescriptions.add(lexDesc);
				System.out.println(lexDesc);

			}
		}
		//Comparative Descriptions
		for (ArrayList<String> d: comparativeBasedDescriptions){
			lexDesc= "";

			for (String word: d){
				if (Lexicalizator.getInstance().isObject(word)){
					currentObject = Lexicalizator.getInstance().lexicalizeObject(word);
					lexDesc+=currentObject.getFirst()+" ";
				}else
					lexDesc+=Lexicalizator.getInstance().lexicalizeWord(new Pair<String,DESCRIPTION_TYPE>(word,DESCRIPTION_TYPE.COMPARATIVE), currentObject.getSecond()) + " ";

			}
			lexDesc = lexDesc.replaceAll("de el", "del");
			lexicalizedComparativeDescriptions.add(lexDesc);
			System.out.println(lexDesc);
		}


	}

	private void contractWords(){

	}

	public boolean addNonUnivocalDescription(ArrayList<String> description){
		return allNonUnivocalDescriptions.add(description);
	}

	public ArrayList<Integer> getObjects(POSICION p){
		return posiciones.get(p.ordinal());
	}

	public void addPositionalObject( int id , POSICION p){
		posiciones.get(p.ordinal()).add(id);
	}

	public boolean isRightOf(float[] position, boolean sctrict,float[] tam){
		if (!sctrict)		
			return posicion[0]>position[0];
		else{
			Point p1 = new Point();
			p1.setLocation(position[2] + tam[2]/2, position[1]+tam[1]/2);
			Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[2],(int)tam[1]);

			Point p = new Point();
			p.setLocation(posicion[2] + tamano[2]/2, posicion[1]+tamano[1]/2);
			Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[2],(int)tamano[1]);

			return (r.intersects(rec) && posicion[0]>position[0]);
		}
	}

	public boolean isLeftOf(float[] position, boolean sctrict,float[] tam){
		if (!sctrict)
			return posicion[0]<position[0];
		else{
			Point p1 = new Point();
			p1.setLocation(position[2] + tam[2]/2, position[1]+tam[1]/2);
			Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[2],(int)tam[1]);

			Point p = new Point();
			p.setLocation(posicion[2] + tamano[2]/2, posicion[1]+tamano[1]/2);
			Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[2],(int)tamano[1]);

			return (r.intersects(rec) && posicion[0]<position[0]);
		}
	}

	public boolean isAboveOf(float[] position, boolean sctrict,float[] tam){

		if (!sctrict)
			return posicion[1]>position[1];
			else{
				Point p1 = new Point();
				p1.setLocation(position[0] - tam[0]/2, position[2]-tam[2]/2);
				Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[0],(int)tam[2]);

				Point p = new Point();
				p.setLocation(posicion[0] - tamano[0]/2, posicion[2]-tamano[2]/2);
				Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[0],(int)tamano[2]);

				return (r.intersects(rec) && posicion[1]>position[1]);
			}
	}

	public boolean isBelowOf(float[] position, boolean sctrict,float[] tam){
		if (!sctrict)
			return posicion[1]<position[1];
		else{
			Point p1 = new Point();
			p1.setLocation(position[0] - tam[0]/2, position[2]-tam[2]/2);
			Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[0],(int)tam[2]);

			Point p = new Point();
			p.setLocation(posicion[0] - tamano[0]/2, posicion[2]-tamano[2]/2);
			Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[0],(int)tamano[2]);

			return (r.intersects(rec) && posicion[1]<position[1]);
		}
	}

	public boolean isFrontOf(float[] position, boolean sctrict,float[] tam){
		if (!sctrict)
			return posicion[2]>position[2];
		else{
			Point p1 = new Point();
			p1.setLocation(position[0] - tam[0]/2, position[1]+tam[1]/2);
			Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[0],(int)tam[1]);

			Point p = new Point();
			p.setLocation(posicion[0] - tamano[0]/2, posicion[1]+tamano[1]/2);
			Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[0],(int)tamano[1]);

			return (r.intersects(rec) && posicion[2]>position[2]);
		}
	}
	public boolean isBackOf(float[] position, boolean sctrict,float[] tam){
		if (!sctrict)
			return posicion[2]<position[2];
		else{
			Point p1 = new Point();
			p1.setLocation(position[0] - tam[0]/2, position[1]+tam[1]/2);
			Rectangle r = new Rectangle(p1.x,p1.y,(int)tam[0],(int)tam[1]);

			Point p = new Point();
			p.setLocation(posicion[0] - tamano[0]/2, posicion[1]+tamano[1]/2);
			Rectangle rec = new Rectangle(p.x,p.y,(int)tamano[0],(int)tamano[1]);

			return (r.intersects(rec) && posicion[2]<position[2]);
		}
	}

	private int indexByKey(String key){
		int i=0;

		for (String k : keys){
			if (k.equals(key))
				return i;
			i++;
		}


		return -1;
	}

	public boolean hasUnivocalDescription(ArrayList<String> descripcion){

		Set<String> desc = new LinkedHashSet<String>(descripcion);
		for (Set<String> d : basicDescriptions){
			if (d.equals(desc))
				return true;
		}
		
		for (Set<ArrayList<String>> allDescByOnePos: positionalBasedDescriptions){

			for (ArrayList<String> d : allDescByOnePos){

				if (d.equals(descripcion))
					return true;

			}
		}

		for (ArrayList<String> d : comparativeBasedDescriptions){
			if (d.equals(descripcion))
				return true;
		}

		for (Set<String> d : absoluteDescriptions){
			if (d.equals(desc))
				return true;
		}


		return false;
	}

	public void printDescriptions(){
		System.out.println();
		System.out.println("DESCRIPCIONES DEL OBJETO "+tipo+" CON ID "+id);

		System.out.println("Descripciones basicas");
		for (Set<String> set : basicDescriptions)
			System.out.println(set.toString());

		System.out.println();
		System.out.println("Descripciones posicionales");
		for (Set<ArrayList<String>> set : positionalBasedDescriptions)
			for (ArrayList<String> des : set)
				System.out.println(des.toString());

		System.out.println();
		System.out.println("Descripciones absolutas");
		for (Set<String> set : absoluteDescriptions)
			System.out.println(set.toString());

		System.out.println("Descripciones comparativas");
		for (ArrayList<String> set : comparativeBasedDescriptions)
			System.out.println(set.toString());

		System.out.println("*********************************************");
		System.out.println();
	}

	public void saveNonUnivocalDescriptions(){

		for (Set<String> d : basicDescriptions){
			allNonUnivocalDescriptions.add(new ArrayList<String>(d));
		}

		for (Set<ArrayList<String>> allDescByOnePos: positionalBasedDescriptions){

			for (ArrayList<String> d : allDescByOnePos){

				allNonUnivocalDescriptions.add(new ArrayList<String>(d));

			}
		}

		for (ArrayList<String> d : comparativeBasedDescriptions){
			allNonUnivocalDescriptions.add(new ArrayList<String>(d));
		}

		for (Set<String> d : absoluteDescriptions){
			allNonUnivocalDescriptions.add(new ArrayList<String>(d));
		}



	}

}
