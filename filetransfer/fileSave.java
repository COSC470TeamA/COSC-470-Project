import java.io.*;

public void fileSave(saveThis item){
               
  String values[] = { saveThis.getDesc(),
  saveThis.getPrice(), saveThis.getCat()};

      
	File file = new File("testfile.txt");

	if (!file.exists()) {
			file.createNewFile();

	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
  for (i=0;i<=2;i++){
	bw.write(values[i]);
  }
	bw.close();
}
