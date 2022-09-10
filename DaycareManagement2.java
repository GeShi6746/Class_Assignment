import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class DaycareManagement {
    public static void main(String[] args) throws IOException {
        String choice = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("""
                Please enter 0 to add new children.
                Please enter 1 to remove children.
                Please enter 2 to add new teachers.
                Please enter 3 to remove teachers.
                Please enter 4 to assign teachers.
                Please enter 5 to de-assign teachers.
                """);
        if (scan.hasNextLine()) {
            choice = scan.nextLine();
        }
        if (Integer.parseInt(choice) == 0) {
            addChildren();
        } else if (Integer.parseInt(choice) == 1) {
            removeChildren();
        } else if (Integer.parseInt(choice) == 2){
            addTeachers();
        } else if (Integer.parseInt(choice) == 3){
            removeTeachers();
        } else if (Integer.parseInt(choice) == 4){
            assignTeachers();
        } else if (Integer.parseInt(choice) == 5) {
            deassignTeachers();
        } else {
            System.out.println("Invalid instruction!\n");
        }
        scan.close();
    }
    private static void deassignTeachers() throws IOException {
        File records = new File("teacherRecords.txt");
        File preAssignedInfo = new File("assignedInfo.txt");
        if(!records.exists()||!preAssignedInfo.exists()){
            System.out.println("There are no existing records.");
            return;
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String removeId;
        int countLine=0;
        while((removeId = inputReader.readLine())!=null) {
            countLine++;
            File results = new File("deassignTeacherResults.txt");
            if(!results.exists()){
                try{
                    results.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter outputWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results,true)));
            if(Integer.parseInt(removeId)<1000||Integer.parseInt(removeId)>9999){
                outputWriter.write("Please enter valid id number at line "+countLine+".\n");
            } else {
                String teacher;
                boolean hasdeassign=false;
                boolean signal=true;
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(records)));
                while ((teacher=in.readLine())!=null){
                    teacher=teacher.trim();
                    if(teacher.contains(removeId)){
                        Room room;
                        int roomNum=searchRoomNum(Integer.parseInt(removeId));
                        room=getAssignedInfo(roomNum);
                        if(roomNum==0){
                            outputWriter.write("Teacher "+removeId+" de-assigned failure at line "+countLine+", because he/she has not been assigned a room.\n");
                            signal=false;
                        }else if(!room.canRemoveTeacher(roomNum)) {
                            outputWriter.write("Teacher "+removeId+" de-assigned failure at line "+countLine+", because room " +roomNum+" needs sufficient teachers.\n");
                            signal=false;
                        } else {
                            hasdeassign=true;
                            outputWriter.write("Teacher "+removeId+" has de-assigned successfully at line "+countLine+".\n");
                            room.removeTeacher(Integer.parseInt(removeId));
                            writeAssignedInfo(searchRoomNum(Integer.parseInt(removeId)),room);
                        }
                    }
                }
                if(signal && !hasdeassign){
                    outputWriter.write("Failed to de-assign teacher "+removeId+" at line"+countLine+", because he/she is not in the records.\n");
                }
                in.close();
            }
            outputWriter.flush();
            outputWriter.close();
        }
        inputReader.close();
        scan.close();
    }
    private static void addChildren() throws IOException {
        File records = new File("childrenRecords.txt");
        ArrayList<Integer> idNumbers=new ArrayList<>();
        if(records.exists()){
            idNumbers=previousChildrenRecord();
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
            File results = new File("addChildrenResults.txt");
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
                Room room;
                int roomNum=getRoomNum(Integer.parseInt(temp[2]));
                room=getAssignedInfo(roomNum);
                if(!room.canAddChildren(roomNum)) {
                    out.write("Adding failed at line "+ countLine+", because room "+roomNum+" did not have enough space or teachers .");
                } else {
                    out.write("Line"+ countLine + " has been added successfully.");
                    room.addChildren(Integer.parseInt(temp[1]));
                    writeAssignedInfo(roomNum, room);
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
            }
            out.write("\r\n");
            out.flush();
            out.close();
        }
        in.close();
        scan.close();
    }
    private static boolean isValidName(String str) {
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
    private static int getRoomNum(int age){
        if(age<13){
            return 1;
        } else if(age<25){
            return 2;
        } else if(age<49){
            return 3;
        } else {
            return 4;
        }
    }
    private static ArrayList<Integer> previousChildrenRecord() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("childrenRecords.txt")));
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
    private static void removeChildren() throws IOException {
        File records = new File("childrenRecords.txt");
        if(!records.exists()){
            System.out.println("There are no existing records.");
            return;
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String removeId;
        int countLine=0;
        while((removeId = inputReader.readLine())!=null) {
            countLine++;
            File results = new File("removeChildrenResults.txt");
            if(!results.exists()){
                try{
                    results.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter outputWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results,true)));
            if(Integer.parseInt(removeId)<100||Integer.parseInt(removeId)>999){
                outputWriter.write("Please enter valid id number at line "+countLine+".\n");
                outputWriter.flush();
            } else {
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
                        outputWriter.write("Child "+removeId+" has removed successfully at line "+countLine+".\n");
                        Room room;
                        int roomNum=searchRoomNum(Integer.parseInt(removeId));
                        room=getAssignedInfo(roomNum);
                        room.removeChildren(Integer.parseInt(removeId));
                        writeAssignedInfo(roomNum, room);
                    }
                }
                if(!hasdelete){
                    outputWriter.write("Failed to remove child "+removeId+" at line"+countLine+".\n");
                }
                outputWriter.flush();
                in.close();
                BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(records)));
                out.write(bf.toString());
                out.flush();
                out.close();
            }
            outputWriter.close();
        }
        inputReader.close();
        scan.close();
    }
    private static int searchRoomNum(int id) throws IOException {
        int roomNum = 0;
        File preAssignInfo =new File("assignedInfo.txt");
        if(!preAssignInfo.exists()){
            return roomNum;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(preAssignInfo)));
        String ids;
        while(roomNum<4){
            roomNum++;
            ids = in.readLine();
            if (!ids.equals("")){
                String[] temp;
                String delimiter = ",";
                temp = ids.split(delimiter);
                if(temp.length==1){
                    if (Integer.parseInt(ids) == id) {
                        return roomNum;
                    }
                } else {
                    for (String s : temp) {
                        if (Integer.parseInt(s) == id) {
                            return roomNum;
                        }
                    }
                }
            }
        }
        in.close();
        return 0;
    }
    private static void addTeachers() throws IOException {
        File records = new File("teacherRecords.txt");
        ArrayList<Integer> idNumbers=new ArrayList<>();
        if(records.exists()){
            idNumbers=previousTeacherRecord();
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String teacher;
        int countLine=0;
        while((teacher = in.readLine())!=null){
            countLine++;
            String[] temp;
            String delimiter = ",";
            temp = teacher.split(delimiter);
            File results = new File("addTeacherResults.txt");
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
            if(successAdmit){
                boolean isValid=isValidName(temp[0]);
                if(!isValid){
                    out.write("Invalid name at line"+ countLine+".");
                    successAdmit=false;
                }
                int id=Integer.parseInt(temp[1]);
                if((id<1000)||(id>9999)){
                    out.write("Invalid id number at line"+ countLine+".");
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
                    if(i<1){
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
    private static ArrayList<Integer> previousTeacherRecord() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("teacherRecords.txt")));
        String teacher;
        ArrayList<Integer> idNumbers=new ArrayList<>();
        while((teacher = in.readLine())!=null){
            String[] temp;
            String delimiter = ",";
            temp = teacher.split(delimiter);
            int id=Integer.parseInt(temp[1]);
            idNumbers.add(id);
        }
        in.close();
        return idNumbers;
    }
    private static void removeTeachers() throws IOException {
        File records = new File("teacherRecords.txt");
        if(!records.exists()){
            System.out.println("There are no existing records.");
            return;
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String removeId;
        int countLine=0;
        while((removeId = inputReader.readLine())!=null) {
            countLine++;
            File results = new File("removeTeacherResults.txt");
            if(!results.exists()){
                try{
                    results.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter outputWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results,true)));
            if(Integer.parseInt(removeId)<1000||Integer.parseInt(removeId)>9999){
                outputWriter.write("Please enter valid id number at line "+countLine+".\n");
                outputWriter.flush();
            } else {
                String teacher;
                boolean hasdelete=false;
                boolean signal=true;
                StringBuilder bf=new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(records)));

                while ((teacher=in.readLine())!=null){
                    teacher=teacher.trim();
                    if(!teacher.contains(removeId)){
                        bf.append(teacher).append("\r\n");
                    } else {
                        Room room;
                        int roomNum=searchRoomNum(Integer.parseInt(removeId));
                        room=getAssignedInfo(roomNum);
                        if(!room.canRemoveTeacher(roomNum)) {
                            outputWriter.write("Teacher "+removeId+" removed failure at line "+countLine+", because room " +roomNum+" needs sufficient teachers.\n");
                            signal=false;
                            bf.append(teacher).append("\r\n");
                        } else {
                            hasdelete=true;
                            outputWriter.write("Teacher "+removeId+" has removed successfully at line "+countLine+".\n");
                            room.removeTeacher(Integer.parseInt(removeId));
                            writeAssignedInfo(searchRoomNum(Integer.parseInt(removeId)),room);
                        }
                    }
                }
                if(signal && !hasdelete){
                    outputWriter.write("Failed to remove teacher "+removeId+" at line"+countLine+".\n");
                }
                in.close();
                outputWriter.flush();
                BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(records)));
                out.write(bf.toString());
                out.flush();
                out.close();
            }
            outputWriter.close();
        }
        inputReader.close();
        scan.close();
    }
    private static void assignTeachers() throws IOException {
        File records = new File("teacherRecords.txt");
        ArrayList<Integer> idNumbers;
        if(records.exists()){
            idNumbers=previousTeacherRecord();
        } else {
            System.out.println("There are no existing teacher records.");
            return;
        }
        String filePath = "";
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter file address:\n");
        if (scan.hasNextLine()) {
            filePath = scan.nextLine();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String ids;
        int countLine=0;
        while((ids = in.readLine())!=null) {
            countLine++;
            String[] temp;
            String delimiter = ",";
            temp = ids.split(delimiter);
            File results = new File("assignTeacherResults.txt");
            if(!results.exists()){
                try{
                    results.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(results,true)));
            boolean successAssign=true;
            if(temp.length<1||temp[0]==null||temp[0].length()<=0){
                out.write("Missing id number at line"+ countLine+".");
                successAssign=false;
            }
            if(temp.length<2||temp[1] == null || temp[1].length() <= 0){
                out.write("Missing room number at line"+ countLine+".");
                successAssign=false;
            }
            if(successAssign){
                int id=Integer.parseInt(temp[0]);
                if((id<1000)||(id>9999)){
                    out.write("Invalid id number at line"+ countLine+".");
                    successAssign=false;
                }
                int roomNum=Integer.parseInt(temp[1]);
                if((roomNum<1)||(roomNum>4)){
                    out.write("Invalid room number at line"+ countLine+".");
                    successAssign=false;
                }
            }
            if(successAssign){
                int id=Integer.parseInt(temp[0]);
                idNumbers.add(id);
                HashSet<Integer> uniqueId=new HashSet<>(idNumbers);
                if(idNumbers.size()==uniqueId.size()){
                    out.write("Id number has not been added to records at line"+ countLine+".");
                    successAssign=false;
                }
                if(!idNumbers.isEmpty()){
                    idNumbers.remove(idNumbers.size() - 1);
                }
            }
            if(successAssign) {
                int preRoom = searchRoomNum(Integer.parseInt(temp[0]));
                if (preRoom > 0) {
                    if (preRoom == Integer.parseInt(temp[1])) {
                        out.write("Teacher " + temp[0] + " has assigned in room " + temp[1] + " at line" + countLine + ".");
                        successAssign=false;
                    }
                }
            }
            if(successAssign) {
                Room room1=getAssignedInfo(1);
                Room room2=getAssignedInfo(2);
                Room room3=getAssignedInfo(3);
                Room room4=getAssignedInfo(4);
                out.write("Line"+ countLine + " has been assigned successfully.");
                int preRoom = searchRoomNum(Integer.parseInt(temp[0]));
                switch (preRoom) {
                    case 1 -> {
                        room1.removeTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(1, room1);
                    }
                    case 2 -> {
                        room2.removeTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(2, room2);
                    }
                    case 3 -> {
                        room3.removeTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(3, room3);
                    }
                    case 4 -> {
                        room4.removeTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(4, room4);
                    }
                }
                switch (Integer.parseInt(temp[1])) {
                    case 1 -> {
                        room1.addTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(1, room1);
                    }
                    case 2 -> {
                        room2.addTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(2, room2);
                    }
                    case 3 -> {
                        room3.addTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(3, room3);
                    }
                    case 4 -> {
                        room4.addTeacher(Integer.parseInt(temp[0]));
                        writeAssignedInfo(4, room4);
                    }
                }
            }
            out.write("\r\n");
            out.flush();
            out.close();
        }
        in.close();
        scan.close();
    }
    private static Room getAssignedInfo(int roomNum) throws IOException {
        Room room=new Room();
        File preAssignInfo =new File("assignedInfo.txt");
        if(!preAssignInfo.exists()){
            return room;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(preAssignInfo)));
        String ids;
        int countLine = 0;
        while(countLine<4){
            countLine++;
            ids = in.readLine();
            if (countLine==roomNum){
                if (!ids.equals("")){
                    String[] temp;
                    String delimiter = ",";
                    temp = ids.split(delimiter);
                    if(temp.length==1){
                        if (Integer.parseInt(ids) < 1000) {
                            room.addChildren(Integer.parseInt(ids));
                        } else if (Integer.parseInt(ids) > 999) {
                            room.addTeacher(Integer.parseInt(ids));
                        }
                    } else {
                        for (String s : temp) {
                            if (Integer.parseInt(s) < 1000) {
                                room.addChildren(Integer.parseInt(s));
                            } else if (Integer.parseInt(s) > 999) {
                                room.addTeacher(Integer.parseInt(s));
                            }
                        }
                    }
                }
            }
        }
        in.close();
        return room;
    }
    private static class Room {
        private final HashSet<Integer> teachers;
        private final HashSet<Integer> children;
        private Room() {
            this.teachers = new HashSet<>();
            this.children = new HashSet<>();
        }
        private int getTeachersNumber() {
            return teachers.size();
        }
        private int getChildrenNumber() {
            return children.size();
        }
        private void addTeacher(int idNumber) {
            teachers.add(idNumber);
        }
        private void addChildren(int idNumber) {
            children.add(idNumber);
        }
        private boolean canAddChildren(int roomNum) {
            boolean canAdd=true;
            if (roomNum<3){
                if(getChildrenNumber()>3 || getTeachersNumber()<1){
                    canAdd=false;
                }
            } else {
                if(getChildrenNumber()>5 || (getTeachersNumber()==1 && getChildrenNumber()>=4) || getTeachersNumber()==0){
                    canAdd=false;
                }
            }
            return canAdd;
        }
        private boolean canRemoveTeacher(int roomNum) {
            boolean canRemove=true;
            if (roomNum<3){
                if(getChildrenNumber()>0 && getTeachersNumber()<2){
                    canRemove=false;
                }
            } else {
                if((getChildrenNumber()>0 && getTeachersNumber()<2) || (getChildrenNumber()>4 && getTeachersNumber()<=2)){
                    canRemove=false;
                }
            }
            return canRemove;
        }
        private void removeChildren(int idNumber) {
            children.remove(idNumber);
        }
        private void removeTeacher(int idNumber) {
            teachers.remove(idNumber);
        }
        public String toString(){
            HashSet<Integer> all = new HashSet<>();
            all.addAll(teachers);
            all.addAll(children);
            StringBuilder allArray = new StringBuilder();
            for (Integer a:all) {
                allArray.append(a).append(",");
            }
            if(allArray.length()>0){
                allArray = new StringBuilder(allArray.substring(0, allArray.length() - 1));
            }
            return String.valueOf(allArray);
        }
    }
    private static void writeAssignedInfo(int roomNum, Room room) throws IOException {
        File preAssignInfo =new File("assignedInfo.txt");
        if(!preAssignInfo.exists()){
            try{
                preAssignInfo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(preAssignInfo)));
        StringBuilder buf = new StringBuilder();
        int countLine=0;
        String ids;
        while(countLine<4) {
            countLine++;
            if (countLine == roomNum) {
                buf.append(room.toString());
                in.readLine();
            } else {
                if((ids=in.readLine()) != null){
                    buf.append(ids);
                }
            }
            buf.append(System.getProperty("line.separator"));
        }
        PrintWriter pw = new PrintWriter(new FileOutputStream(preAssignInfo));
        pw.write(buf.toString().toCharArray());
        pw.flush();
        pw.close();
        in.close();
    }
}