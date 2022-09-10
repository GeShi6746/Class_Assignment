import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class DaycareManagement {
    public static void main(String[] args) throws IOException {
        String choice = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("To admit new children into the daycare, please enter 0.\n" +
                "To remove a child from the daycare, enter 1.\n");
        if (scan.hasNextLine()) {
            choice = scan.nextLine();
        }
        if (Integer.parseInt(choice) == 0) {
            admit();
        } else if (Integer.parseInt(choice) == 1) {
            remove();
        } else {
            System.out.println("Invalid instruction!\n");
        }
        scan.close();
    }

    public static void admit() throws IOException {
        File records = new File("records.txt");
        ArrayList<Integer> idNumbers=new ArrayList<>();
        if(records.exists()){
            idNumbers=previousRecord();
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String child;
        int countLine=0;
        while((child = in.readLine())!=null){
            countLine++;
            String[] temp;
            String delimiter = ",";
            temp = child.split(delimiter);
            File results = new File("results.txt");
            if(!results.exists()){
                try{
                    results.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results,true)));
            boolean successAdmit=true;
            if(temp.length<1||temp[0]==null||temp[0].length()<=0){
                out.write("Missing name at line"+ countLine+".");
                successAdmit=false;
            }
            if(temp.length<2||temp[1] == null || temp[1].length() <= 0){
                out.write("Missing id number at line"+ countLine+".");
                successAdmit=false;
            }
            if(temp.length<3){
                out.write("Missing age at line"+ countLine+".");
                successAdmit=false;
            }
            if(successAdmit){
                boolean isValid=isValidName(temp[0]);
                if((!isValid)||(temp[0].length()<2)||(temp[0].length()>20)){
                    out.write("Invalid name at line"+ countLine+".");
                    successAdmit=false;
                }
                int id=Integer.parseInt(temp[1]);
                if((id<100)||(id>999)){
                    out.write("Invalid id number at line"+ countLine+".");
                    successAdmit=false;
                }
                int age=Integer.parseInt(temp[2]);
                if((age<2)||(age>60)){
                    out.write("Invalid age at line"+ countLine+".");
                    successAdmit=false;
                }
            }
            if(successAdmit){
                int id=Integer.parseInt(temp[1]);
                idNumbers.add(id);
                HashSet<Integer> uniqueId=new HashSet<>(idNumbers);
                if(idNumbers.size()!=uniqueId.size()){
                    if(!idNumbers.isEmpty()){
                        idNumbers.remove(idNumbers.size() - 1);
                    }
                    out.write("Id number is not unique at line"+ countLine+".");
                    successAdmit=false;
                }
            }
            if(successAdmit){
                out.write("Line"+ countLine + " has been added successfully.");
                if(!records.exists()){
                    try{
                        records.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                BufferedWriter output=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(records,true)));
                for(int i=0; i< temp.length; i++){
                    if(i<2){
                        output.write(temp[i]+",");
                    }else {
                        output.write(temp[i]);
                    }
                }
                output.write("\r\n");
                output.flush();
                output.close();
            }
            out.write("\r\n");
            out.flush();
            out.close();
        }
        in.close();
        scan.close();
    }
    public static boolean isValidName(String str) {
        char[] chars = str.toCharArray();
        boolean isValid;
        for (char aChar : chars) {
            isValid = (aChar >= 'a' && aChar <= 'z') || (aChar >= 'A' && aChar <= 'Z');
            if (!isValid) {
                return false;
            }
        }
        return true;
    }
    public static ArrayList<Integer> previousRecord() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("records.txt")));
        String child;
        ArrayList<Integer> idNumbers=new ArrayList<>();
        while((child = in.readLine())!=null){
            String[] temp;
            String delimiter = ",";
            temp = child.split(delimiter);
            int id=Integer.parseInt(temp[1]);
            idNumbers.add(id);
        }
        in.close();
        return idNumbers;
    }

    public static void remove() throws IOException {
        File records = new File("records.txt");
        if(!records.exists()){
            System.out.println("There are no existing records.");
            return;
        }
        String removeId="";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the id number you want to remove:\n");
        if (scan.hasNextLine()) {
            removeId = scan.nextLine();
        }
        if(Integer.parseInt(removeId)<100||Integer.parseInt(removeId)>999){
            System.out.println("Please enter valid id number.");
            return;
        }
        String child;
        boolean hasdelete=false;
        StringBuilder bf=new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(records)));
        while ((child=in.readLine())!=null){
            child=child.trim();
            if(!child.contains(removeId)){
                bf.append(child).append("\r\n");
            }else {
                hasdelete=true;
                System.out.println("Child "+removeId+" has removed successfully.");
            }
        }
        if(!hasdelete){
            System.out.println("Failed to remove "+removeId+".");
        }
        in.close();
        BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(records)));
        out.write(bf.toString());
        out.flush();
        out.close();
    }
}
