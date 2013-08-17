package utils;
import java.util.HashMap;




public class Lexicalizator {

	public enum GENERO {FEMENINO,MASCULINO,NA};
	public enum DESCRIPTION_TYPE{BASIC,ABSOLUTE,POSITIONAL,COMPARATIVE};

	private HashMap<String, Pair<String, GENERO>> objetos;
	private HashMap<Pair<String,DESCRIPTION_TYPE>, String[]> words;

	private static Lexicalizator lexicalizator = null;

	protected Lexicalizator(){

		objetos =  new HashMap<String,Pair<String,GENERO>>();
		words = new HashMap<Pair<String,DESCRIPTION_TYPE>,String[]>();


		insertObjects();
		insertColors();
		insertAbsolutes();
		insertPositionals();
		insertComparatives();



	}

	static public Lexicalizator getInstance(){
		if (lexicalizator==null)
			lexicalizator = new Lexicalizator();
		return lexicalizator;

	}

	private void insertObjects(){

		objetos.put("cubo", new Pair<String,GENERO>("el cubo",GENERO.MASCULINO));
		objetos.put("cono", new Pair<String,GENERO>("el cono",GENERO.MASCULINO));
		objetos.put("esfera", new Pair<String,GENERO>("la esfera",GENERO.FEMENINO));
		objetos.put("piramide", new Pair<String,GENERO>("la piramide",GENERO.FEMENINO));
		objetos.put("cilindro", new Pair<String,GENERO>("el cilindro",GENERO.MASCULINO));

	}

	private void insertColors(){

		words.put(new Pair<String,DESCRIPTION_TYPE>("rojo",DESCRIPTION_TYPE.BASIC), new String[]{"roja","rojo"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("verde",DESCRIPTION_TYPE.BASIC),new String[]{"verde","verde"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("azul",DESCRIPTION_TYPE.BASIC), new String[]{"azul","azul"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("amarillo",DESCRIPTION_TYPE.BASIC), new String[]{"amarilla","amarillo"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("blanco",DESCRIPTION_TYPE.BASIC), new String[]{"blanca","blanco"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("negro",DESCRIPTION_TYPE.BASIC), new String[]{"negra","negro"});

	}
	
	private void insertAbsolutes(){
		words.put(new Pair<String,DESCRIPTION_TYPE>("pequeno",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"más pequeña","más pequeño"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("grande",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"más grande","más grande"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("derecha",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más a la derecha","que está más a la derecha"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("izquierda",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más a la izquierda","que está más a la izquierda"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("encima",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más arriba","que está más arriba"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("debajo",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más abajo","que está más abajo"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("delante",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más delante","que está más delante"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("detras",DESCRIPTION_TYPE.ABSOLUTE), new String[]{"que está más detrás","que está más detrás"});

	}
	
	private void insertPositionals(){
		words.put(new Pair<String,DESCRIPTION_TYPE>("derecha_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está a la derecha de","que está a la derecha de"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("izquierda_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está a la izquierda de","que está a la izquierda de"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("encima_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está encima de","que está encima de"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("debajo_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está debajo de","que está debajo de"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("delante_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está delante de","que está delante de"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("detras_p",DESCRIPTION_TYPE.POSITIONAL), new String[]{"que está detrás de","que está detrás de"});
	}
	
	
	private void insertComparatives(){
		words.put(new Pair<String,DESCRIPTION_TYPE>("menor",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"menor que","menor que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("mayor",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"mayor que","mayor que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("abajo_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más abajo que","más abajo que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("arriba_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más arriba que","más arriba que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("delante_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más adelante que","más adelante que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("detras_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más detrás que","más detrás que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("izquierda_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más a la izquierda que","más a la izquierda que"});
		words.put(new Pair<String,DESCRIPTION_TYPE>("derecha_c",DESCRIPTION_TYPE.COMPARATIVE), new String[]{"más a la derecha que","más a la derecha que"});
		
	}
	
	public Pair<String,GENERO> lexicalizeObject(String object){
		
		if (objetos.containsKey(object))
			return objetos.get(object.toLowerCase());
		else 
			return new Pair<String,GENERO>(object,GENERO.MASCULINO);
	}
	
	public String lexicalizeWord(Pair<String,DESCRIPTION_TYPE> word , GENERO gender){
			return getLexicalizedWordByDescriptionPriority(word)[gender.ordinal()];
	}
	
	private String[] getLexicalizedWordByDescriptionPriority(Pair<String,DESCRIPTION_TYPE> word){
		
		switch(word.getSecond()){
		
		
		case COMPARATIVE:
			if (words.containsKey(word))
				return words.get(word);
			else
				word.setSecond(DESCRIPTION_TYPE.POSITIONAL);
		
		
		case POSITIONAL:
			if (words.containsKey(word))
				return words.get(word);
			else
				word.setSecond(DESCRIPTION_TYPE.ABSOLUTE);
		
		
		case ABSOLUTE:
			if (words.containsKey(word))
				return words.get(word);
			else
				word.setSecond(DESCRIPTION_TYPE.BASIC);
		
		case BASIC:
					
			if (words.containsKey(word))
				return words.get(word);
					
		}
		
		return new String[] {word.getFirst(),word.getFirst()}; //no la ha encontrado
		
	}
	
	public boolean isObject(String object){
		return objetos.containsKey(object.toLowerCase());
	}
	
	public boolean isComparator(String object){
		return objetos.containsKey(object.toLowerCase());
	}







}
