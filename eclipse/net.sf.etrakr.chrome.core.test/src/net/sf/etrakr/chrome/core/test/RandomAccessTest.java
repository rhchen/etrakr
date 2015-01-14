package net.sf.etrakr.chrome.core.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import net.sf.etrakr.chrome.core.event.TraceEvent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

public class RandomAccessTest {

	private static Runtime runtime = Runtime.getRuntime();
	
	private static int mb = 1024*1024;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		String path = "data/flow.json";

		try {
			
			FileInputStream is = new FileInputStream(path);
			FileInputStream isSub = new FileInputStream(path);
			FileChannel fileChannel = isSub.getChannel();
			
			printVMState("Delta 0", 0);
			
	    	long start = System.currentTimeMillis();
	    	
			Builder<TraceEvent> jbuilder = ImmutableList.<TraceEvent>builder();
					
			ObjectMapper mm = new ObjectMapper();
			mm.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mm.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
			
	    	JsonParser jp = mm.getFactory().createParser(is);
	    	jp.configure(JsonParser.Feature.ALLOW_COMMENTS,true);
	    	jp.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS,true);
	    	jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);
	    	jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);
	    	
			jp.nextToken();// will return JsonToken.START_OBJECT
			
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				
				JsonToken token = jp.nextToken();// move to value, or START_OBJECT/START_ARRAY
				
				String fieldname = jp.getCurrentName();
				
				if(!"traceEvents".equalsIgnoreCase(fieldname)) continue;
				
				long startPos = 0;
				long endPos = 0;
				
				int eventDepth = 0;
				
				while (token != JsonToken.END_ARRAY){
					
					token = jp.nextToken(); // move to value
					
					switch(token){
					
						case START_OBJECT :
							
							if(eventDepth == 0){
								startPos = jp.getCurrentLocation().getByteOffset();
							}
							
							eventDepth ++;
							
							break;
							
						case END_OBJECT :
							
							eventDepth --;
							
							if(eventDepth == 0){
								
								endPos = jp.getCurrentLocation().getByteOffset();
								
								//System.out.println("recored : start - "+ startPos +" : end - "+ endPos);
								
								long startPosLoad = startPos - 1;
								
								long bufferSize = endPos - startPosLoad;
								
								byte[] buffer = new byte[(int) bufferSize];
								
								//FileInputStream fis = new FileInputStream(fileUri.getPath());

								
								
								MappedByteBuffer mmb = fileChannel.map(FileChannel.MapMode.READ_ONLY, startPosLoad, bufferSize);

								mmb.get(buffer);
								
								//fis.close();
								
								
								
								String tmp = new String(buffer);
								
								//System.out.println("tmp : "+ tmp);
								JsonParser jp2 = mm.getFactory().createParser(buffer);
								
								TraceEvent event = jp2.readValueAs(TraceEvent.class);
								
								jbuilder.add(event);
							}

							break;
							
						default :
							break;
						
					}

				}
				
			}
			
			fileChannel.close();
			
			ImmutableList<TraceEvent> lit = jbuilder.build();
			
			System.out.println("lit "+ lit.size());
			List<List<TraceEvent>> pList2 = Lists.partition(lit, 10000);
			
			for(List<TraceEvent> sub1 : pList2){
				
				for(TraceEvent eve : sub1){
					
					long tts = eve.ts;
				}
				
			}
			
			long delta = System.currentTimeMillis() - start;
	    	printVMState("Delta 2", delta);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
		
		String str = "";
		Assert.assertNotNull(str);
		
	}
	
	private static void printVMState(String name, long delta){

        System.out.println("##### Heap utilization statistics [MB] #####");
        
        System.out.println(name + " : "+ delta); 
        
        //Print used memory
        System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / mb);
 
        //Print free memory
        System.out.println("Free Memory:"
            + runtime.freeMemory() / mb);
         
        //Print total available memory
        //System.out.println("Total Memory:" + runtime.totalMemory() / mb);
 
        //Print Maximum available memory
        //System.out.println("Max Memory:" + runtime.maxMemory() / mb);
		
	}

}
