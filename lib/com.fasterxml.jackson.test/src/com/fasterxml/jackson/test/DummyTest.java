package com.fasterxml.jackson.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DummyTest {

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
	public final void test_0() {
		
		try {
			
			File f = new File("data/chromeos_system_trace.json");
			 
		    JsonFactory factory = new JsonFactory();
		    
			JsonParser parser = factory.createParser(f);
		
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(parser, Object.class);
			
			String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			
			//System.out.println(str);

			/* Print to file */
			PrintWriter out = new PrintWriter("data/chromeos_system_trace_formated.json");
			out.println(str);
			out.close();
			
			Assert.assertNotNull(str);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
