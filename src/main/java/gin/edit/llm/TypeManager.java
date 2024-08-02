package gin.edit.llm;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.stmt.*;

public class TypeManager {
    private String currentType;
    private int currentCount;
    private List<String> typeList;
    private int threshold = 40;

    public TypeManager(String fileName) {
        typeList = new ArrayList<>();
        readFile(fileName);
    }

    public TypeManager(){
        this("/home/diantu/IdeaProjects/TestOllama4jLLm/src/main/java/org/example/example.txt");
    }

    private void readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("CurrentType: ")) {
                    currentType = line.substring(13).trim();
                } else if (line.startsWith("CurrentCount: ")) {
                    currentCount = Integer.parseInt(line.substring(14).trim());
                } else if (line.startsWith("TypeList: ")) {
                    String[] types = line.substring(10).trim().split(", ");
                    for (String type : types) {
                        typeList.add(type);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentType() {
        return currentType;
    }

    public  <T extends Statement> Class<T>  getCurrentClass() {
        return typeToClass(this.currentType);
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
        saveToFile();
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void addCurrentCount() {
        if(this.currentCount >= this.threshold){
            this.setCurrentType(this.getNextType());
            this.currentCount = 0;
        } else {
            this.currentCount += 1;
        }
        saveToFile();  
    }

    public String getNextType() {

        int curr_index = typeList.indexOf(this.currentType);

        if(curr_index <= typeList.size()){
            return typeList.get(++curr_index);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Statement> Class<T> typeToClass(String type) {
        if (type.equals("AssertStmt")) {
            return (Class<T>) AssertStmt.class;
        } else if (type.equals("BlockStmt")) {
            return (Class<T>) BlockStmt.class;
        } else if (type.equals("BreakStmt")) {
            return (Class<T>) BreakStmt.class;
        } else if (type.equals("ContinueStmt")) {
            return (Class<T>) ContinueStmt.class;
        } else if (type.equals("DoStmt")) {
            return (Class<T>) DoStmt.class;
        } else if (type.equals("ExpressionStmt")) {
            return (Class<T>) ExpressionStmt.class;
        } else if (type.equals("ForEachStmt")) {
            return (Class<T>) ForEachStmt.class;
        } else if (type.equals("ForStmt")) {
            return (Class<T>) ForStmt.class;
        } else if (type.equals("IfStmt")) {
            return (Class<T>) IfStmt.class;
        } else if (type.equals("LabeledStmt")) {
            return (Class<T>) LabeledStmt.class;
        } else if (type.equals("ReturnStmt")) {
            return (Class<T>) ReturnStmt.class;
        } else if (type.equals("SwitchStmt")) {
            return (Class<T>) SwitchStmt.class;
        } else if (type.equals("SynchronizedStmt")) {
            return (Class<T>) SynchronizedStmt.class;
        } else if (type.equals("ThrowStmt")) {
            return (Class<T>) ThrowStmt.class;
        } else if (type.equals("TryStmt")) {
            return (Class<T>) TryStmt.class;
        } else if (type.equals("WhileStmt")) {
            return (Class<T>) WhileStmt.class;
        } else if (type.equals("YieldStmt")) {
            return (Class<T>) YieldStmt.class;
        } else {
            return null;
        }
    }

    public void saveToFile() {
        String fileName = "/home/diantu/IdeaProjects/TestOllama4jLLm/src/main/java/org/example/example.txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("CurrentType: " + currentType + "\n");
            writer.write("CurrentCount: " + currentCount + "\n");
            writer.write("TypeList: " + String.join(", ", typeList) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
