import java.io.*;

public class fileWriter {
    File file;

    public fileWriter(String filename){
        try{
            this.file = new File("C:\\Users\\JoshF\\Documents\\GitHub\\Networks\\measurements\\"+filename); //Pls dont delete this line edit the one below, I hate this file ty <3
            //this.file = new File("C:\\Users\\JoshF\\Documents\\GitHub\\Networks\\ measurements\\"+filename);
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write("");
            fileWriter.close();
        }catch (IOException e) {
            System.out.println("ERROR: FileWriter: Could not open file to write to.");
            e.printStackTrace();
            System.exit(0);
        }
    }



    public void writeLine(String line) throws IOException {

        FileWriter fileWriter = new FileWriter(this.file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(line);
        bufferedWriter.newLine();
        bufferedWriter.close();

    }


    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Program Files\\GitHub\\Networks\\ measurements\\receiver.txt");
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.write("Hello World");
        fileWriter.close();
    }

}
