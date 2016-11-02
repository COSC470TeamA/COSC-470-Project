//This method selects a list of items pre-defined in a text file and then compares them to the input
//If an item from the list matches the input then the input is returned as a valid category
//Otherwise the system notes that the item does not exist
import java.io.*;

public String itemSelect(search String){
    String res = "Item not found";
    File file = new File("items.txt");
    ArrayList<String> items = new ArrayList<String>();
    Scanner in = new Scanner(file);
    while (in.hasNextLine()){
        items.add(in.nextLine());
    }
    Collections.sort(items);
    for(int i=0; i<items.size(); ++i){
        if (items.get(i) == search){
          res = items.get(i);
        }
    }
    Return res;
}
