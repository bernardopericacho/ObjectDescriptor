import java.util.ArrayList;

import utils.Objeto;
import utils.Pair;
import utils.XmlWorldReader;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XmlWorldReader xmlReader = new XmlWorldReader();
		Pair<ArrayList<Objeto>,Pair<ArrayList<String>,ArrayList<ArrayList<Objeto>>>> world = xmlReader.readFile("mundos/mundo3.xml");
		Descriptor descriptor = new Descriptor(world);
		descriptor.describeWorld();
	}

}
