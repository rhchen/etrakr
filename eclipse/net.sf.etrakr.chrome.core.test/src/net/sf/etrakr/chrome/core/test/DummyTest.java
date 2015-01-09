/**
 * 
 */
package net.sf.etrakr.chrome.core.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.etrakr.chrome.core.event.TraceEvent;
import net.sf.etrakr.chrome.core.event.Zoo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	public void test() {
		
		 
	    try {
	    	
	    	FileInputStream is = new FileInputStream("data/huge_trace.json");
	    	
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
	    	
	    	Object document = conf.jsonProvider().parse(is, "UTF-8");
	    	
	    	Zoo zoo = JsonPath.parse(document).read("$", Zoo.class);
	    	
	    	ObjectMapper mapper = new ObjectMapper();
	    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Zoo zoo2 = mapper.readValue(new File("data/huge_trace.json"), Zoo.class);
			
			Iterator<TraceEvent> it = zoo.traceEvents.iterator();
			
			long start = System.currentTimeMillis();
			
			while(it.hasNext()){
				
				TraceEvent event = it.next();
			}
			
			long delta = System.currentTimeMillis() - start;
			
			System.out.println("delta : "+ delta);
			
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

}
