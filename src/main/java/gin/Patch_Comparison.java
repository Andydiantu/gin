package gin;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import gin.edit.Edit;
import gin.edit.Edit.EditType;
import gin.test.InternalTestRunner;
import gin.test.UnitTestResult;
import gin.test.UnitTestResultSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.HashSet;
import java.util.Set;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class Patch_Comparison implements Serializable{
    @Serial
    private static final long serialVersionUID = -3749197264292832819L;

    @Argument(alias = "f1", description = "Required: First output filename", required = true)
    protected static String csvPath1;

    @Argument(alias = "f2", description = "Required: Second output filename", required = true)
    protected static String csvPath2;



    public static void main(String[] args) {

        Patch_Comparison patch_Comparison = new Patch_Comparison();

        // Parse the command-line arguments and populate the fields
        Args.parseOrExit(patch_Comparison, args);

        Set <String> patchDic1 = producePatchDic(csvPath1);
        Set <String> patchDic2 = producePatchDic(csvPath2);

        comparePatchDic(patchDic1, patchDic2);
    }

    public static void comparePatchDic (Set<String> patchDic1, Set<String> patchDic2) {
        int patchDic1Size = patchDic1.size();
        int patchDic2Size = patchDic2.size();

        Set <String> intersection = new HashSet<>(patchDic1);
        intersection.retainAll(patchDic2);
        int intersectionSize = intersection.size();

        Set <String> difference1 = new HashSet<>(patchDic1);
        difference1.removeAll(patchDic2);
        int difference1Size = difference1.size();

        Set <String> difference2 = new HashSet<>(patchDic2);
        difference2.removeAll(patchDic1);
        int difference2Size = difference2.size();

        System.out.println("Patch dictionary 1 size: " + patchDic1Size);
        System.out.println("Patch dictionary 2 size: " + patchDic2Size);
        System.out.println("Intersection size: " + intersectionSize);
        System.out.println("Difference 1 size: " + difference1Size);
        System.out.println("Difference 2 size: " + difference2Size);

    }


    public static Set<String> producePatchDic (String csvPath) {
        Set<String> patchDic = new HashSet<>();
        Set<Integer> patchIndexDic = new HashSet<>();
        String line = "";

        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {
            // Read the header
            String[] header = reader.readNext();

            if (header != null) {
                System.out.println("Header:");
                for (String column : header) {
                    System.out.print(column + " | ");
                }
                System.out.println();
            }

            List<String[]> allRows = reader.readAll();
            

            for (String[] row : allRows) {
                String patch = row[2];  
                int patchIndex = Integer.parseInt(row[0]);

                if(patchIndexDic.contains(patchIndex)) {
                    continue;
                } else {
                    patchIndexDic.add(patchIndex);
                }
                
                System.out.println("Patch: " + patch);
                System.out.println(row[0] + " " + row[1] + " " + row[2]);

                String regex = "\"([^\"]+\\.java)\"";;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(patch);

                if (matcher.find()) {
                    String filePath = matcher.group().replace("\"", "");
                    System.out.println("Extracted file path: " + filePath);

                    String sourceAbsPath = "/home/diantu/Documents/gin/examples/jcodec/" + filePath;
                    
                    String patchedFile = producePatch(sourceAbsPath, replaceAndRemoveQuotes(patch));

                    patchDic.add(patchedFile);
                } else {
                    System.out.println("Wrong patch format");
                }


            }



            // while ((line = br.readLine()) != null) {

            

            //     String[] parts = line.split(",");
            //     // System.out.println(line);
            //     if(parts.length < 3) {
            //         continue;
            //     }
            //     String patch = parts[2];

            //     if (parts[0].equals("\"\"PatchIndex\"\"")){
            //         continue;
            //     }

            //     int patchIndex = Integer.parseInt(parts[0].replace("\"",""));

            //     if(patchIndexDic.contains(patchIndex)) {
            //         continue;
            //     } else {
            //         patchIndexDic.add(patchIndex);
            //     }
                
            //     System.out.println("Patch: " + patch);
            //     System.out.println(parts[0] + " " + parts[1] + " " + parts[2]);

            //     String regex = "(?<=\\s\"\")[^\"]+(?=\"\")";
            //     Pattern pattern = Pattern.compile(regex);
            //     Matcher matcher = pattern.matcher(patch);

            //     if (matcher.find()) {
            //         String filePath = matcher.group();
            //         System.out.println("Extracted file path: " + filePath);

            //         String sourceAbsPath = "/home/diantu/Documents/gin/examples/jcodec/" + filePath;
                    
            //         String patchedFile = producePatch(sourceAbsPath, replaceAndRemoveQuotes(patch));

            //         patchDic.add(patchedFile);
            //     } else {
            //         System.out.println("Wrong patch format");
            //     }
                
            // }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return patchDic;
    }

    public static String replaceAndRemoveQuotes(String input) {
        // Step 1: Replace all occurrences of "" with a temporary placeholder
        String replaced = input.replace("\"\"", "PLACEHOLDER");

        // Step 2: Remove all remaining single quotes (")
        String result = replaced.replace("\"", "");

        // Step 3: Replace the placeholder with a single quote (")
        result = result.replace("PLACEHOLDER", "\"");

        return result;
    }

    public static String producePatch (String sourceAbsPath, String patchDescription) {
        
        System.out.println("Source file path: " + sourceAbsPath);
        System.out.println("Patch description: " + patchDescription);


        SourceFileLine sourceFileLine = new SourceFileLine(sourceAbsPath, null);
        SourceFileTree sourceFileTree = new SourceFileTree(sourceAbsPath, null);

        Patch patch = parsePatch(patchDescription, sourceFileLine, sourceFileTree);
        String desp = patch.apply();

        return desp;
    }


    private static Patch parsePatch(String patchText, SourceFileLine sourceFileLine, SourceFileTree sourceFileTree) {

        if (patchText.equals("|")) {
            // Logger.info("No edits to be applied. Running original code.");
            return new Patch(sourceFileTree);
        }

        List<Edit> editInstances = new ArrayList<>();

        String patchTrim = patchText.trim();
        String cleanPatch = patchTrim;

        if (patchTrim.startsWith("|")) {
            cleanPatch = patchText.replaceFirst("\\|", "").trim();
        }

        String[] editStrings = cleanPatch.trim().split("\\|");

        boolean allLineEdits = true;
        boolean allStatementEdits = true;

        for (String editString : editStrings) {

            if(editString.trim().equals("")) {
                continue;
            }

            String[] tokens = editString.trim().split("\\s+");

            String editAction = tokens[0];

            System.out.println("Edit action: " + editAction);
            System.out.println("Edit string: " + editString);

            
            

            Class<?> clazz = null;

            try {
                clazz = Class.forName(editAction);
            } catch (ClassNotFoundException e) {
                Logger.error("Patch edit type unrecognised: " + editAction);
                Logger.trace(e);
                System.exit(-1);
            }

            Method parserMethod = null;
            try {
                parserMethod = clazz.getMethod("fromString", String.class);
            } catch (NoSuchMethodException e) {
                Logger.error("Patch edit type has no fromString method: " + clazz.getCanonicalName());
                Logger.trace(e);
                System.exit(-1);
            }

            Edit editInstance = null;
            try {
                editInstance = (Edit) parserMethod.invoke(null, editString.trim());
            } catch (IllegalAccessException e) {
                Logger.error("Cannot parse patch: access error invoking edit class.");
                Logger.trace(e);
                System.exit(-1);
            } catch (InvocationTargetException e) {
                Logger.error("Cannot parse patch: invocation error invoking edit class.");
                Logger.trace(e);
                System.exit(-1);
            }

            allLineEdits &= editInstance.getEditType() == EditType.LINE;
            allStatementEdits &= editInstance.getEditType() != EditType.LINE;
            editInstances.add(editInstance);

        }

        if (!allLineEdits && !allStatementEdits) {
            Logger.error("Cannot proceed: mixed line/statement edit types found in patch");
            System.exit(-1);
        }

        Patch patch = new Patch(allLineEdits ? sourceFileLine : sourceFileTree);
        for (Edit e : editInstances) {
            patch.add(e);
        }

        return patch;

    }


}