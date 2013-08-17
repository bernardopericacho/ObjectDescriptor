import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import utils.Objeto;
import utils.Pair;


public class Descriptor {

	private ArrayList<Objeto> world;
	private ArrayList<String> objectTypes;
	private ArrayList<ArrayList<Objeto>> worldByType;
	File descriptionsSteps;
	boolean debugMode;
				
	Descriptor(Pair<ArrayList<Objeto>,Pair<ArrayList<String>,ArrayList<ArrayList<Objeto>>>> worldDefinition){
		world = worldDefinition.getFirst();
		objectTypes = worldDefinition.getSecond().getFirst();
		worldByType = worldDefinition.getSecond().getSecond();

		descriptionsSteps =  new File("/home/bernardo/Escritorio/descripciones.txt");
		//"/home/bernardo/Escritorio/descripciones.txt");
		PrintStream stream = null;
		try {
			stream = new PrintStream(descriptionsSteps);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		debugMode = true;
		System.setOut(stream);

	}

	public void describeWorld(){

		generateBasicDescriptions();
		generatePositionalBasedDescriptions();
		generateAbsoluteDescriptions();
		generateComparativeDescriptions();
		eraseNonUnivocalDescriptions();
		lexicalizeDescriptions();
		//printAllDescriptions();
	}


	//1st step of the algorithm
	private void generateBasicDescriptions(){

		if (debugMode)
			System.out.println("**************DESCRIPCIONES BASICAS******************");

		for (Objeto obj : world){
			powerset(obj); //generamos todas las combinaciones posibles de los atributos

			if (debugMode){
				System.out.println(obj.getBasicDescriptions().toString());
				System.out.println();
			}
		}
		if (debugMode)
			System.out.println("********************************");

	}

	//2nd step of the algorithm
	private void generatePositionalBasedDescriptions(){

		setAllPositionalRelations();// generamos primero todas las posiciones de los objetos
		// obj.encima contiene los id de los que tiene encima
		if (debugMode)
			System.out.println("**************DESCRIPCIONES POSICIONALES******************");
		for (Objeto obj : world){
			if (debugMode){
				System.out.println("Relaciones de los demás objetos con el "+obj.getTipo()+" con id "+obj.getId());
				System.out.println();
			}
			for (int pos=Objeto.POSICION.DELANTE.ordinal();pos<=Objeto.POSICION.DEBAJO.ordinal();pos++){
				ArrayList<Integer> ids = obj.getObjects(Objeto.POSICION.values()[pos]);

				for (int id : ids){
					Objeto objeto = this.findObjetoFromId(id);

					boolean strict = false;

					switch (Objeto.POSICION.values()[pos]){
					
					case DELANTE:
						strict = objeto.isFrontOf(obj.getPosicion(), true, obj.getTamano());
						break;
					case DETRAS:
						strict = objeto.isBackOf(obj.getPosicion(), true, obj.getTamano());
						break;
					case ENCIMA:
						strict = objeto.isAboveOf(obj.getPosicion(), true, obj.getTamano());
						break;
					case DEBAJO:
						strict = objeto.isBelowOf(obj.getPosicion(), true, obj.getTamano());
						break;
					case IZQUIERDA:
						strict = objeto.isLeftOf(obj.getPosicion(), true, obj.getTamano());
						break;
					case DERECHA:
						strict = objeto.isRightOf(obj.getPosicion(), true, obj.getTamano());
						break;
					}
					
					
					if (strict){
						Set<Set<String>> previousDes = objeto.getBasicDescriptions();

						ArrayList<String> description;
						ArrayList<String> finalDesc;

						for (Set<String> set : previousDes){
							description = new ArrayList<String>(set);

							for(Set<String> set2: obj.getBasicDescriptions()){

								finalDesc = new ArrayList<String>(description);
								finalDesc.add(Objeto.POSICION.values()[pos].toString().toLowerCase()+"_p");
								finalDesc.addAll(set2);
								if (debugMode)
									System.out.println(finalDesc.toString());
								objeto.addPositionalBasedDescription(finalDesc, Objeto.POSICION.values()[pos]);
							}
						}
					}

				}
			}
			if (debugMode)
				System.out.println();	
		}
		if (debugMode)
			System.out.println("********************************");

	}



	//3rd step of the algorithm
	private void generateAbsoluteDescriptions(){
		if (debugMode)
			System.out.println("**************DESCRIPCIONES ABSOLUTAS******************");


		ArrayList<Objeto> objectsByType;
		String type;
		Objeto objeto = null;
		Objeto objetoGrande = null;
		Objeto objetoPequeno = null;
		ArrayList<Objeto> objetosPorPosicion = null;
		ArrayList<Set<String>> basicDescriptionsAlreadyMeasured = new ArrayList<Set<String>>();
		for (int i=0;i<worldByType.size();i++){

			objectsByType = worldByType.get(i);
			type = objectTypes.get(i);
			//mas de un objeto del mismo tipo			
			if (objectsByType.size()>1){

				basicDescriptionsAlreadyMeasured.clear();

				for (int index=0;index<objectsByType.size();index++){ //para todos los objetos

					for (Set<String> descripcion : objectsByType.get(index).getBasicDescriptions()){ //para todas las descripciones 

						if (!basicDescriptionsAlreadyMeasured.contains(descripcion)){ //si no hemos utilizado ya esa descripcion
							basicDescriptionsAlreadyMeasured.add(descripcion);
							objetoGrande = objectsByType.get(index);
							objetoPequeno = objetoGrande;
							objetosPorPosicion =  new ArrayList<Objeto>();
							for (int r=0;r<6;r++)
								objetosPorPosicion.add(objetoGrande);

							for (int compare=index+1;compare<objectsByType.size();compare++){ //comparar con el resto de objetos
								objeto = objectsByType.get(compare);

								for (Set<String> descriptionToCompare : objeto.getBasicDescriptions()){ //para cada descripcion de los objetos


									if (descripcion.equals(descriptionToCompare)){

										if (objeto.getVolumen()>objetoGrande.getVolumen())
											objetoGrande=objeto;
										if (objeto.getVolumen()<objetoPequeno.getVolumen())
											objetoPequeno=objeto;
										if (objetosPorPosicion.get(Objeto.POSICION.ENCIMA.ordinal()).isBelowOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.ENCIMA.ordinal(), objeto);

										if (objetosPorPosicion.get(Objeto.POSICION.DEBAJO.ordinal()).isAboveOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.DEBAJO.ordinal(), objeto);

										if (objetosPorPosicion.get(Objeto.POSICION.DERECHA.ordinal()).isLeftOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.DERECHA.ordinal(), objeto);

										if (objetosPorPosicion.get(Objeto.POSICION.IZQUIERDA.ordinal()).isRightOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.IZQUIERDA.ordinal(), objeto);

										if (objetosPorPosicion.get(Objeto.POSICION.DELANTE.ordinal()).isBackOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.DELANTE.ordinal(), objeto);

										if (objetosPorPosicion.get(Objeto.POSICION.DETRAS.ordinal()).isFrontOf(objeto.getPosicion(),false,null))
											objetosPorPosicion.set(Objeto.POSICION.DETRAS.ordinal(), objeto);

										break;
									}


								}



							}				

							if (objetoGrande.getId()!=objetoPequeno.getId()){ //si son iguales son varios del mismo tamano
								Set<String> descripGrande =  new LinkedHashSet<String>(descripcion);
								//descripGrande.add("mas");
								descripGrande.add("grande");
								Set<String> descripPeque =  new LinkedHashSet<String>(descripcion);
								//descripPeque.add("mas");
								descripPeque.add("pequeno");
								if (debugMode){
									System.out.println(type+" ID "+ objetoGrande.getId()+"--> Descripcion: "+ descripGrande.toString());
									System.out.println(type+" ID "+ objetoPequeno.getId()+"--> Descripcion: "+ descripPeque.toString());
								}
								objetoGrande.addAbsoluteDescription(descripGrande);
								objetoPequeno.addAbsoluteDescription(descripPeque);


							}
							Set<String> descripPos;
							for (int pos=Objeto.POSICION.DELANTE.ordinal();pos<=Objeto.POSICION.DEBAJO.ordinal();pos = pos+2){//si son iguales son varios misma posicion
								if (objetosPorPosicion.get(pos).getId()!=objetosPorPosicion.get(pos+1).getId()){
									descripPos = new LinkedHashSet<String>(descripcion);
									//descripPos.add("mas");
									descripPos.add(Objeto.POSICION.values()[pos].toString().toLowerCase());
									if (debugMode)
										System.out.println(type+" ID "+ objetosPorPosicion.get(pos).getId()+"--> Descripcion: "+ descripPos.toString());
									objetosPorPosicion.get(pos).addAbsoluteDescription(descripPos);

									descripPos = new LinkedHashSet<String>(descripcion);
									//descripPos.add("mas");
									descripPos.add(Objeto.POSICION.values()[pos+1].toString().toLowerCase());
									if (debugMode)
										System.out.println(type+" ID "+ objetosPorPosicion.get(pos+1).getId()+"--> Descripcion: "+ descripPos.toString());
									objetosPorPosicion.get(pos+1).addAbsoluteDescription(descripPos);

								}
							}


						}					


					}

				}

			}

		}
		if (debugMode)
			System.out.println("********************************");
	}

	//4th step of the algorithm
	private void generateComparativeDescriptions(){

		//comparativas posicionales: Ej: cubo mas grande que el cono rojo delante del cubo azul
		int index=0;
		Objeto objToCompare;
		final Set<String> grande = new LinkedHashSet<String>();
		grande.add("mayor");
		//grande.add("que");
		final Set<String> pequeno = new LinkedHashSet<String>();
		pequeno.add("menor");
		//pequeno.add("que");
		final Set<String> izq = new LinkedHashSet<String>();
		//izq.add("mas");
		izq.add("izquierda_c");
		//izq.add("que");
		final Set<String> der = new LinkedHashSet<String>();
		//der.add("mas");
		der.add("derecha_c");
		//der.add("que");
		final Set<String> encima = new LinkedHashSet<String>();
		//encima.add("mas");
		encima.add("arriba_c");
		//encima.add("que");
		final Set<String> debajo = new LinkedHashSet<String>();
		//debajo.add("mas");
		debajo.add("abajo_c");
		//debajo.add("que");
		final Set<String> delante = new LinkedHashSet<String>();
		//delante.add("mas");
		delante.add("delante_c");
		//delante.add("que");
		final Set<String> detras = new LinkedHashSet<String>();
		//detras.add("mas");
		detras.add("detras_c");
		//detras.add("que");

		if (debugMode)
			System.out.println("**************DESCRIPCIONES COMPARATIVAS******************");
		for (Objeto obj : world){

			for (int i=index+1;i<world.size();i++){
				objToCompare = world.get(i);

				if (obj.getVolumen()>objToCompare.getVolumen()){
					setComparativeDescriptions(obj,grande,objToCompare);
					setComparativeDescriptions(objToCompare,pequeno,obj);
				}

				else if (obj.getVolumen()<objToCompare.getVolumen()){
					setComparativeDescriptions(objToCompare,grande,obj);
					setComparativeDescriptions(obj,pequeno,objToCompare);
				}
				if (obj.isAboveOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(obj,encima,objToCompare);
					setComparativeDescriptions(objToCompare,debajo,obj);
				}

				else if (obj.isBelowOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(objToCompare,encima,obj);
					setComparativeDescriptions(obj,debajo,objToCompare);
				}
				if (obj.isLeftOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(obj,izq,objToCompare);
					setComparativeDescriptions(objToCompare,der,obj);
				}

				else if (obj.isRightOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(objToCompare,izq,obj);
					setComparativeDescriptions(obj,der,objToCompare);
				}
				if (obj.isFrontOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(obj,delante,objToCompare);
					setComparativeDescriptions(objToCompare,detras,obj);
				}

				else if (obj.isBackOf(objToCompare.getPosicion(),false,null)){
					setComparativeDescriptions(objToCompare,delante,obj);
					setComparativeDescriptions(obj,detras,objToCompare);
				}


			}		
			index++;
		}
		if (debugMode)
			System.out.println("********************************");
	}
	private void setComparativeDescriptions(Objeto obj,Set<String> comparation,Objeto objToCompare){

		ArrayList<String> aux = new ArrayList<String>();
		for (Set<String> d : obj.getBasicDescriptions()){

			for (Set<String> de : objToCompare.getBasicDescriptions()){
				aux = new ArrayList<String>();
				aux.addAll(d);
				aux.addAll(comparation);
				aux.addAll(de);
				if(!de.containsAll(d) && !d.containsAll(de)){
					obj.addComparativeBasedDescription(aux);
					if (debugMode)
						System.out.println(aux.toString());
				}
			}

			for (Set<String> de : objToCompare.getAbsoluteDescriptions()){
				aux = new ArrayList<String>();
				aux.addAll(d);
				aux.addAll(comparation);
				aux.addAll(de);
				obj.addComparativeBasedDescription(aux);
				if (debugMode)
					System.out.println(aux.toString());
			}

			for (int i=Objeto.POSICION.DELANTE.ordinal();i<=Objeto.POSICION.DEBAJO.ordinal();i++){
				for (ArrayList<String> de : objToCompare.getPositionalBasedDescriptions(Objeto.POSICION.values()[i])){
					aux = new ArrayList<String>();
					aux.addAll(d);
					aux.addAll(comparation);
					aux.addAll(de);
					if((!de.containsAll(d)) && (!d.containsAll(de))){
						obj.addComparativeBasedDescription(aux);
						if (debugMode)
							System.out.println(aux.toString());
					}					
				}
			}


		}

		/*for (int i=Objeto.POSICION.DELANTE.ordinal();i<=Objeto.POSICION.DEBAJO.ordinal();i++){
			for (ArrayList<String> d : obj.getPositionalBasedDescriptions(Objeto.POSICION.values()[i])){

				for (Set<String> de : objToCompare.getBasicDescriptions()){
					aux.clear();
					aux.addAll(d);
					aux.addAll(comparation);
					aux.addAll(de);
					obj.addComparativeBasedDescription(aux);
				}

				for (Set<String> de : objToCompare.getAbsoluteDescriptions()){
					aux.clear();
					aux.addAll(d);
					aux.addAll(comparation);
					aux.addAll(de);
					obj.addComparativeBasedDescription(aux);
				}

				for (int j=Objeto.POSICION.DELANTE.ordinal();j<=Objeto.POSICION.DEBAJO.ordinal();j++){
					for (ArrayList<String> de : objToCompare.getPositionalBasedDescriptions(Objeto.POSICION.values()[j])){
						aux.clear();
						aux.addAll(d);
						aux.addAll(comparation);
						aux.addAll(de);
						obj.addComparativeBasedDescription(aux);
					}
				}


			}
		}*/

	}




	//5th step of the algorithm
	private void eraseNonUnivocalDescriptions(){

		//First of all, save all NonUnivocalDescriptions generated
		for (Objeto obj: world){
			obj.saveNonUnivocalDescriptions();
		}



		if (debugMode)
			System.out.println("**************DESCRIPCIONES UNIVOCAS******************");	
		int index;
		ArrayList<Objeto> objetos = new  ArrayList<Objeto>();
		for (ArrayList<Objeto> objectsByType : worldByType){

			index=0;
			for (Objeto obj : objectsByType){
				objetos.clear();
				for (int i=index+1;i<objectsByType.size();i++){
					objetos.add(objectsByType.get(i));
				}
				deleteSameDescriptions(obj,objetos);
				index++;				
			}

		}
		if (debugMode){
			for (Objeto obj: world){

				System.out.print("BASICAS ");
				System.out.println(obj.getBasicDescriptions().toString());

				for (int i=Objeto.POSICION.DELANTE.ordinal();i<=Objeto.POSICION.DEBAJO.ordinal();i++){
					System.out.print(Objeto.POSICION.values()[i]+ " ");
					System.out.println(obj.getPositionalBasedDescriptions(Objeto.POSICION.values()[i]).toString());
				}
				System.out.print("COMPARATIVAS ");
				System.out.println(obj.getComparativeBasedDescriptions().toString());
				System.out.print("ABSOLUTAS ");
				System.out.println(obj.getAbsoluteDescriptions().toString());
				System.out.println();
				System.out.println();
				System.out.println();
			}
		}
		if (debugMode)
			System.out.println("********************************");
	}


	//6th step of the algorithm
	private void lexicalizeDescriptions(){


		for (Objeto obj: world){
			obj.lexicalize();
		}


	}

	private void powerset(Objeto obj){
		ArrayList<String> values = obj.getAllValues();
		Set<Set<String>> ps =  new LinkedHashSet<Set<String>>();
		LinkedHashSet<String> tipo = new LinkedHashSet<String>();
		tipo.add(obj.getTipo());
		ps.add(tipo);   // add the empty set

		// for every item in the original list
		for (String value : values) {
			Set<Set<String>> newPs = new LinkedHashSet<Set<String>>();

			for (Set<String> subset : ps) {
				// copy all of the current powerset's subsets
				newPs.add(subset);

				// plus the subsets appended with the current item
				Set<String> newSubset = new LinkedHashSet<String>(subset);
				newSubset.add(value);
				newPs.add(newSubset);
			}

			// powerset is now powerset of list.subList(0, list.indexOf(item)+1)
			ps = newPs;
		}
		obj.setBasicDescriptions(ps);
	}

	private void deleteSameDescriptions(Objeto obj, ArrayList<Objeto> objectsToCompare){
		Set<Set<String>> descriptionsToErase;
		Set<Set<String>> descriptionsToEraseInComparedObject;
		ArrayList<ArrayList<String>> dsToErase;
		ArrayList<ArrayList<String>> dsToEraseInComparedObject;
		ArrayList<Set<String>> desc;
		ArrayList<Set<String>> descToCompare;
		Set<ArrayList<String>> ds;
		Set<ArrayList<String>> dsToCompare;
		ArrayList<ArrayList<String>> deToCompare;
		ArrayList<ArrayList<String>> de;
		Set<Set<String>> des;
		Set<Set<String>> desToCompare;


		//basic Descriptions
		descriptionsToErase = new LinkedHashSet<Set<String>>();
		descriptionsToEraseInComparedObject = new LinkedHashSet<Set<String>>();
		des = obj.getBasicDescriptions();
		for (Objeto objToCompare : objectsToCompare){
			desToCompare = objToCompare.getBasicDescriptions();
			for (Set<String> d : des){
				if (desToCompare.contains(d)){
					descriptionsToErase.add(d);
					descriptionsToEraseInComparedObject.add(d);
				}
			}
			desToCompare.removeAll(descriptionsToEraseInComparedObject);
			descriptionsToEraseInComparedObject.clear();
		}
		des.removeAll(descriptionsToErase);

		//positional Descriptions
		dsToErase =  new ArrayList<ArrayList<String>>();
		dsToEraseInComparedObject=  new ArrayList<ArrayList<String>>();
		for (int i=Objeto.POSICION.DELANTE.ordinal();i<=Objeto.POSICION.DEBAJO.ordinal();i++){
			ds = obj.getPositionalBasedDescriptions(Objeto.POSICION.values()[i]);
			dsToErase.clear();
			for (Objeto objToCompare : objectsToCompare){
				dsToCompare = objToCompare.getPositionalBasedDescriptions(Objeto.POSICION.values()[i]);
				for (ArrayList<String> d : ds){
					if (dsToCompare.contains(d)){
						dsToErase.add(d);
						dsToEraseInComparedObject.add(d);
					}
				}
				dsToCompare.removeAll(dsToEraseInComparedObject);
				dsToEraseInComparedObject.clear();
			}
			ds.removeAll(dsToErase);
		}
		
		//comparative descriptions
		dsToErase.clear();
		dsToEraseInComparedObject.clear();
		de = obj.getComparativeBasedDescriptions();
		for (Objeto objToCompare : objectsToCompare){
			desToCompare = objToCompare.getBasicDescriptions();
			for (ArrayList<String> d : de){
				if (desToCompare.contains(d)){
					dsToErase.add(d);
					dsToEraseInComparedObject.add(d);
				}
			}
			desToCompare.removeAll(descriptionsToEraseInComparedObject);
			dsToEraseInComparedObject.clear();
		}
		de.removeAll(dsToErase);
		
		//absolute descriptions ya son univocas
		/*descriptionsToErase.clear();
		descToCompare = objToCompare.getAbsoluteDescriptions();
		desc = obj.getAbsoluteDescriptions();
		for (Set<String> d : desc){
			if (descToCompare.contains(d))
				descriptionsToErase.add(d);
		}
		desc.removeAll(descriptionsToErase);
		descToCompare.removeAll(descriptionsToErase);*/
	}

	private void setAllPositionalRelations(){

		int index=0;
		for (Objeto obj : world){

			for (int i = index+1;i<world.size();i++ ){


				//update the 2 objets but opposite relation
				Objeto ob = world.get(i);

				if (obj.isAboveOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.ENCIMA);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.DEBAJO);
				}else if (obj.isBelowOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.DEBAJO);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.ENCIMA);
				}

				if (obj.isRightOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.DERECHA);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.IZQUIERDA);
				}else if (obj.isLeftOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.IZQUIERDA);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.DERECHA);
				}

				if (obj.isFrontOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.DELANTE);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.DETRAS);
				}else if (obj.isBackOf(ob.getPosicion(),false,null)){
					ob.addPositionalObject(obj.getId(),Objeto.POSICION.DETRAS);
					obj.addPositionalObject(ob.getId(),Objeto.POSICION.DELANTE);
				}
			}
			index++;
		}

	}


	public Objeto findObjetoFromId(int id){

		for (Objeto obj : world){
			if (obj.getId()==id)
				return obj;
		}

		return null;
	}

	public void printAllDescriptions(){
		for (Objeto o : world)
			o.printDescriptions();
	}	

	/**
	 * 
	 * @param descripcion
	 * @return
	 */
	private ArrayList<Objeto> getObjectFromUnivocalDescription(ArrayList<String> descripcion){
		ArrayList<Objeto> objectsReferenced = new ArrayList<Objeto>();

		for (Objeto obj: world){
			if (obj.hasUnivocalDescription(descripcion)){
				objectsReferenced.add(obj);
				break;
			}

		}

		return objectsReferenced;
	}

	private ArrayList<Objeto> getObjectsWithSameDescription(ArrayList<String> descripcion){
		ArrayList<Objeto> objectsReferenced = new ArrayList<Objeto>();
		ArrayList<ArrayList<String>> descriptions;

		for (Objeto obj: world){

			descriptions = obj.getAllNonUnivocalDescriptions();

			for (ArrayList<String> d : descriptions){
				if (d.equals(descripcion)){
					objectsReferenced.add(obj);
					break;
				}
			}

		}

		return objectsReferenced;
	}

	public Pair<Boolean,ArrayList<Objeto>> getObjectFromDescription(ArrayList<String> descripcion){

		boolean univocal = false;
		ArrayList<Objeto> objects = getObjectFromUnivocalDescription(descripcion);

		if (objects.isEmpty()){ // not univocal or does not exist 
			objects = getObjectsWithSameDescription(descripcion);
		}else
			univocal = true;

		return new Pair<Boolean,ArrayList<Objeto>>(univocal,objects);
	}

}
