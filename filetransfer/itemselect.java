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
