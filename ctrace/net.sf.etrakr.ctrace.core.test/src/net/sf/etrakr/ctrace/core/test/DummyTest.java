/**
 * 
 */
package net.sf.etrakr.ctrace.core.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.etrakr.ctrace.core.event.TraceEvent;
import net.sf.etrakr.ctrace.core.event.Zoo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.internal.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

/**
 * @author admin
 *
 */
public class DummyTest {

	private static Runtime runtime = Runtime.getRuntime();
	
	private static int mb = 1024*1024;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_0() {
		
		String path = "data/flow.json";
		test(path);
	}
	
	@Test
	public void test_1() {
		
		String path = "data/perf_sampling_trace_with_trace_events.json";
		test(path);
	}
	
	@Test
	public void test_2() {
		
		String path = "data/thread_time_visualisation.json";
		test(path);
	}
	
	@Test
	public void test_3() {
		
		String path = "data/huge_trace.json";
		test(path);
	}
	
	private static void test(String path) {
		
		 
	    try {
	    	
	    	//String path = "data/perf_sampling_trace_with_trace_events.json";
	    	//String path = "C:\\tmp\\trace-viewer-master\\trace-viewer-master\\test_data\\theverge_trace.json";
	    	//FileInputStream is = new FileInputStream("data/huge_trace.json");
	    	FileInputStream is = new FileInputStream(path);
	    	
	    	printVMState("Delta 0", 0);
	    	
	    	Configuration.setDefaults(new Configuration.Defaults() {

	    	    private final JsonProvider jsonProvider = new JacksonJsonProvider();

	    	    @Override
	    	    public JsonProvider jsonProvider() {
	    	        return jsonProvider;
	    	    }

	    	    @Override
	    	    public MappingProvider mappingProvider() {
	    	    	ObjectMapper mapper = new ObjectMapper();
	    	    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    	    	mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
	    	        return new JacksonMappingProvider(mapper);
	    	    }

	    	    @Override
	    	    public Set<Option> options() {
	    	        return EnumSet.noneOf(Option.class);
	    	    }
	    	});
	    	
	    	Configuration conf = Configuration.defaultConfiguration();
	    	
	    	conf = conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
	    	//conf = conf.addOptions(Option.ALWAYS_RETURN_LIST);
	    	
	    	long start = System.currentTimeMillis();
	    	
	    	Object document = conf.jsonProvider().parse(is, "UTF-8");
	    	
	    	long delta = System.currentTimeMillis() - start;
			printVMState("Delta 1", delta);
			start = System.currentTimeMillis();
			
	    	Zoo zoo = JsonPath.parse(document).read("$", Zoo.class);
	    	
	    	delta = System.currentTimeMillis() - start;
	    	printVMState("Delta 2", delta);
			start = System.currentTimeMillis();
			
	    	ImmutableList<TraceEvent> list = ImmutableList.<TraceEvent>builder().addAll(zoo.traceEvents).build();
	    	
	    	List<List<TraceEvent>> pList = Lists.partition(list, 10000);
	    	
	    	delta = System.currentTimeMillis() - start;
	    	printVMState("Delta 3", delta);
			start = System.currentTimeMillis();
			
			is = new FileInputStream(path);
			
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
				
				jp.nextToken();// move to value, or START_OBJECT/START_ARRAY
				String fieldname = jp.getCurrentName();
				
				if(!"traceEvents".equalsIgnoreCase(fieldname)) continue;
				
				while (jp.nextToken() != JsonToken.END_ARRAY){
					
					jp.nextToken(); // move to value
					
					TraceEvent event = mm.readValue(jp, TraceEvent.class);
					
					if(event != null) jbuilder.add(event);

				}
				
			}
			
			delta = System.currentTimeMillis() - start;
	    	printVMState("Delta 4", delta);
			start = System.currentTimeMillis();
			
			ImmutableList<TraceEvent> jlist = jbuilder.build();
			
			List<List<TraceEvent>> subList = Lists.partition(jlist, 10000);
			
			for(List<TraceEvent> sub1 : subList){
				
				for(TraceEvent eve : sub1){
					
					long tts = eve.ts;
				}
				
			}
			
			delta = System.currentTimeMillis() - start;
	    	printVMState("Delta 5", delta);
			start = System.currentTimeMillis();
			is = new FileInputStream(path);
			
	    	ObjectMapper mapper = new ObjectMapper();
	    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    	
	    	Zoo zoo2 = mapper.readValue(is, Zoo.class);
			Iterator<TraceEvent> it = zoo2.traceEvents.iterator();
			
			delta = System.currentTimeMillis() - start;
			printVMState("Delta 6", delta);
			start = System.currentTimeMillis();
			
			ImmutableList<TraceEvent> list2 = ImmutableList.<TraceEvent>builder().addAll(it).build();
			
			List<List<TraceEvent>> pList2 = Lists.partition(list2, 10000);
			
			for(List<TraceEvent> sub1 : pList2){
				
				for(TraceEvent eve : sub1){
					
					long tts = eve.ts;
				}
				
			}
			
			delta = System.currentTimeMillis() - start;
			printVMState("Delta 7", delta);
			start = System.currentTimeMillis();
			
			while(it.hasNext()){
				
				TraceEvent event = it.next();
				long tts = event.ts;
			}
			
			delta = System.currentTimeMillis() - start;
			printVMState("Delta 8", delta);
			
			//System.out.println(mapper.writeValueAsString(zoo));  
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
