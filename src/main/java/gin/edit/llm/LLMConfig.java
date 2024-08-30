package gin.edit.llm;

import org.checkerframework.checker.units.qual.s;

import dev.langchain4j.model.openai.OpenAiModelName;
import gin.edit.llm.PromptTemplate.PromptTag;

public class LLMConfig {

	/** the following are some default template prompts */
	public enum PromptType {
		SIMPLE(new PromptTemplate("Give me " + PromptTag.COUNT.withEscape() + " implementations of this:"
        		+ "```\n"
        		+ PromptTag.DESTINATION.withEscape()
        		+ "\n"
        		+ "```\n")), 
		
		MEDIUM(new PromptTemplate("Give me " + PromptTag.COUNT.withEscape() + " different Java implementations of this method body:"
        		+ "```\n"
        		+ PromptTag.DESTINATION.withEscape()
        		+ "\n"
        		+ "```\n"
        		+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
                + "Wrap all code in curly braces, if it is not already."
                + "Do not include any method or class declarations."
                + "label all code as java.")), 
		
		DETAILED(new PromptTemplate("Give me " + PromptTag.COUNT.withEscape() + " different Java implementations of this method body:"
        		+ "```\n"
        		+ PromptTag.DESTINATION.withEscape()
        		+ "\n"
        		+ "```\n"
        		+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
        		+ "In the org.jcodec.scale.BaseResampler class, the following change was helpful. I changed this:"
        		+ "```\n"
        		+ "	if (temp == null) {"
        		+ "		temp = new int[toSize.getWidth() * (fromSize.getHeight() + nTaps())];"
        		+ "		tempBuffers.set(temp);"
        		+ "	}"
        		+ "```\n"
        		+ "into this:"
        		+ "```\n"
        		+ "	if (temp == null) {"
        		+ "		if (scaleFactorX >= 0)"
        		+ "			return;"
        		+ "		temp = new int[toSize.getWidth() * (fromSize.getHeight() + nTaps())];"
        		+ "		tempBuffers.set(temp);"
        		+ "	}"
        		+ "```\n"
                + "Wrap all code in curly braces, if it is not already."
                + "Do not include any method or class declarations."
                + "label all code as java.")), 


		SMALL_CHANGE(new PromptTemplate("Give me " + PromptTag.COUNT.withEscape() + " different Java implementations of this method body:"
        		+ "```\n"
        		+ PromptTag.DESTINATION.withEscape()
        		+ "\n"
        		+ "```\n"
        		+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
        		+ "Wrap all code in curly braces, if it is not already.Do not include any method or class declarations. Label all code as java."
				+ "I will give you four examples of small changes you could try. If you have this code:"
				+ "```\n"
				+ "public void write(ByteBuffer buf) {\n"
				+ "    ByteBuffer dup = buf.duplicate();\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "    doWrite(buf);\n"
				+ "    header.setBodySize(buf.position() - dup.position() - 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    header.write(dup);\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 1: you could try copying a statement from one place to another like this\n\n"
				+ "```\n"
				+ "public void write(ByteBuffer buf) {\n"
				+ "    ByteBuffer dup = buf.duplicate();\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "    doWrite(buf);\n"
				+ "    header.setBodySize(buf.position() - dup.position() - 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    header.write(dup);\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 2: you could try deleting a statement chosen at random like this:\n\n"
				+ "```\n"
				+ "public void write(ByteBuffer buf) {\n"
				+ "    ByteBuffer dup = buf.duplicate();\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "    header.setBodySize(buf.position() - dup.position() - 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    header.write(dup);\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 3: you could try replacing one statement with another like this:\n\n"
				+ "```\n"
				+ "public void write(ByteBuffer buf) {\n"
				+ "    ByteBuffer dup = buf.duplicate();\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "    doWrite(buf);\n"
				+ "    header.setBodySize(buf.position() - dup.position() - 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 4: you could try swapping two statements like this:\n\n"
				+ "```\n"
				+ "public void write(ByteBuffer buf) {\n"
				+ "    ByteBuffer dup = buf.duplicate();\n"
				+ "    doWrite(buf);\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "    header.setBodySize(buf.position() - dup.position() - 8);\n"
				+ "    Assert.assertEquals(header.headerSize(), 8);\n"
				+ "    NIOUtils.skip(buf, 8);\n"
				+ "}\n"
				+ "```\n"
				+"In all of these examples, the statements to change are chosen at random. They do not have to be whole lines, just valid Java statements.")),

		// MASKED(new PromptTemplate("I am working on Java and require help in completing a missing line of a function, given its context. "
		// 		+ "I have the following code snippet belongs to to project" + PromptTag.PROJECT.withEscape() + ":"
		// 		+ "```\n"
		// 		+ PromptTag.DESTINATION.withEscape()
		// 		+ "\n"
		// 		+ "```\n"
		// 		+ "Please replace the <<PLACEHOLDER>> sign with " + PromptTag.COUNT.withEscape() +  " different meaningful implementations for the following code. "	
		// 		+ "Return each suggestion in a complete and separate function body. "
		// 		+ "Ensure the provided code is wrapped with triple backticks if it’s not already. "
		// 		+ "Label all code as java.")),

		MASKED(new PromptTemplate("Please replace <<PLACEHOLDER>> sign in the method below with meaningfull implementation. \n"
				+ "```\n"
				+ PromptTag.DESTINATION.withEscape()
				+ "\n"
				+ "```\n"
				+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
				+ "Wrap all code in curly braces, if it is not already. "
				+ "Do not include any method or class declarations. "
				+ "Label all code as java.")),

		MASKED_SHOT(new PromptTemplate(
				"Please replace <<PLACEHOLDER>> sign in the method below with meaningful implementation, \n"
				+ "```\n"
				+ PromptTag.DESTINATION.withEscape()
				+ "\n"
				+ "```\n"
				+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
				+ "Wrap all code in curly braces, if it is not already. "
				+ "Do not include any method or class declarations. Label all code as java.\n\n"
				+ "I will give you four examples of how you can replace the <<PLACEHOLDER>> with meaningful implementation. If you have this code:\n"
				+ "```\n"
				+ "{\n"
				+ "    int round = 1 << (logWD - 1);\n"
				+ "    if (logWD >= 1) {\n"
				+ "        // Necessary to correctly scale _in [-128, 127] range,\n"
				+ "        // i.e. x = ay / b; x,y _in [0, 255] is\n"
				+ "        // x = a * (y + 128) / b - 128; x,y _in [-128, 127]\n"
				+ "        o -= 128;\n"
				+ "        round += w << 7;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) // <<PLACEHOLDER>>\n"
				+ "        ;\n"
				+ "    } else {\n"
				+ "        // Necessary to correctly scale in [-128, 127] range\n"
				+ "        o += (w << 7) - 128;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 1: You could try adding a computation to each element like this:\n\n"
				+ "```\n"
				+ "{\n"
				+ "    int round = 1 << (logWD - 1);\n"
				+ "    if (logWD >= 1) {\n"
				+ "        o -= 128;\n"
				+ "        round += w << 7;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o + round, -128, 127);\n"
				+ "    } else {\n"
				+ "        o += (w << 7) - 128;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 2: You could try adding a condition inside the loop like this:\n\n"
				+ "```\n"
				+ "{\n"
				+ "    int round = 1 << (logWD - 1);\n"
				+ "    if (logWD >= 1) {\n"
				+ "        o -= 128;\n"
				+ "        round += w << 7;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) {\n"
				+ "            if (blk0[off] > 0) out[off] = (byte) clip(blk0[off] * w + o + round, -128, 127);\n"
				+ "            else out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "        }\n"
				+ "    } else {\n"
				+ "        o += (w << 7) - 128;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 3: You could try adding logging for debugging purposes like this:\n\n"
				+ "```\n"
				+ "{\n"
				+ "    int round = 1 << (logWD - 1);\n"
				+ "    if (logWD >= 1) {\n"
				+ "        o -= 128;\n"
				+ "        round += w << 7;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) {\n"
				+ "            System.out.println(\"Processing pixel: \" + off);\n"
				+ "            out[off] = (byte) clip(blk0[off] * w + o + round, -128, 127);\n"
				+ "        }\n"
				+ "    } else {\n"
				+ "        o += (w << 7) - 128;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 4: You could try optimizing the computation by pre-calculating values like this:\n\n"
				+ "```\n"
				+ "{\n"
				+ "    int round = 1 << (logWD - 1);\n"
				+ "    if (logWD >= 1) {\n"
				+ "        o -= 128;\n"
				+ "        round += w << 7;\n"
				+ "        int adjustedO = o + round;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + adjustedO, -128, 127);\n"
				+ "    } else {\n"
				+ "        o += (w << 7) - 128;\n"
				+ "        for (int i = 0; i < blkH; i++, off += stride - blkW) for (int j = 0; j < blkW; j++, off++) out[off] = (byte) clip(blk0[off] * w + o, -128, 127);\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "In all of these examples, the placeholder is replaced with meaningful implementations that enhance the functionality of the loop, whether by adding calculations, conditions, debugging, or optimization."
			)),
				
		MASKED_RUNTIME_OPTIMIZATION(new PromptTemplate(
				"Replace the <<PLACEHOLDER>> in the method below with an optimized implementation focusing on minimizing both time and space complexity.\n"
				+ "```\n"
				+ PromptTag.DESTINATION.withEscape()
				+ "\n"
				+ "```\n"
				+ "This code belongs to project " + PromptTag.PROJECT.withEscape() + ". "
				+ "Ensure the code is wrapped in curly braces if not already. Only include target method in response. Label all code as java.\n"
				+ "I will give you four examples of optimizations you could try. If you have this code:\n"
				+ "```\n"
				+ "public void process(int[] array) {\n"
				+ "    for (int i = 0; i < array.length; i++) {\n"
				+ "        for (int j = i + 1; j < array.length; j++) {\n"
				+ "            if (array[i] == array[j]) {\n"
				+ "                // <<PLACEHOLDER>>\n"
				+ "            }\n"
				+ "        }\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 1: You could try using a different data structure to reduce time complexity like this:\n\n"
				+ "```\n"
				+ "public void process(int[] array) {\n"
				+ "    Set<Integer> seen = new HashSet<>();\n"
				+ "    for (int num : array) {\n"
				+ "        if (seen.contains(num)) {\n"
				+ "            // Handle the duplicate\n"
				+ "        } else {\n"
				+ "            seen.add(num);\n"
				+ "        }\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 2: You could try reducing redundant computations by breaking out of loops early like this:\n\n"
				+ "```\n"
				+ "public void process(int[] array) {\n"
				+ "    for (int i = 0; i < array.length; i++) {\n"
				+ "        for (int j = i + 1; j < array.length; j++) {\n"
				+ "            if (array[i] == array[j]) {\n"
				+ "                // Handle the duplicate\n"
				+ "                break;\n"
				+ "            }\n"
				+ "        }\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 3: You could try replacing recursion with an iterative approach to save space like this:\n\n"
				+ "```\n"
				+ "public int factorial(int n) {\n"
				+ "    int result = 1;\n"
				+ "    for (int i = 2; i <= n; i++) {\n"
				+ "        result *= i;\n"
				+ "    }\n"
				+ "    return result;\n"
				+ "}\n"
				+ "```\n\n"
				+ "Example 4: You could try using in-place operations to reduce space complexity like this:\n\n"
				+ "```\n"
				+ "public void reverseArray(int[] array) {\n"
				+ "    int left = 0, right = array.length - 1;\n"
				+ "    while (left < right) {\n"
				+ "        int temp = array[left];\n"
				+ "        array[left] = array[right];\n"
				+ "        array[right] = temp;\n"
				+ "        left++;\n"
				+ "        right--;\n"
				+ "    }\n"
				+ "}\n"
				+ "```\n\n"
				+ "In all of these examples, the focus is on minimizing both time and space complexity while ensuring the code is optimized for performance. The optimizations chosen are based on the context and constraints of the problem."
			));
		
		
		
		public final PromptTemplate template;
	    private PromptType(PromptTemplate template) {
	        this.template = template;
	    }
	}
	
    // You can use "demo" api key for demonstration purposes.
    public static String openAIKey = "demo";
    
    public static String openAIModelName = OpenAiModelName.GPT_3_5_TURBO;
    
    public static String modelType="OpenAI"; // Should be param from c'tor

    public static long timeoutInSeconds = 30;
    
    // default for langchain4j
    public static double temperature = 0.7;
    
    public static PromptType defaultPromptType = PromptType.MEDIUM;
    
    public static PromptTemplate defaultPromptTemplate = null;
    
    public static PromptTemplate getDefaultPromptTemplate() {
    	return (defaultPromptTemplate != null) ? defaultPromptTemplate : defaultPromptType.template;
    }
    
    public static String projectName = "";
    
    
    
}