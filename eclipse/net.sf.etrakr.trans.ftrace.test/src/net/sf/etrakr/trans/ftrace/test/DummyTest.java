package net.sf.etrakr.trans.ftrace.test;

import java.io.File;
import java.io.IOException;

import net.sf.etrakr.ftrace.core.event.IFtraceEvent;
import net.sf.etrakr.trans.ftrace.deser.FtraceDeserializer;
import net.sf.etrakr.trans.ftrace.module.FtraceMapper;
import net.sf.etrakr.trans.ftrace.module.FtraceModule;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.google.common.collect.BiMap;
import com.google.common.collect.TreeBasedTable;

public class DummyTest {

	private static final File _file = new File("data/android_systrace.txt");
	
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
	public <T> void test_1() throws JsonProcessingException, IOException {
		
		if(!_file.exists()) throw new IOException("File not exist");
		
		FtraceDeserializer deserializer = new FtraceDeserializer();
		
		FtraceModule module = new FtraceModule();
		
		module.addDeserializer(ITmfEvent.class, deserializer);
		
		FtraceMapper mapper = new FtraceMapper();
		
		mapper.registerModule(module);
		
		MappingIterator<T> it = mapper.reader(ITmfEvent.class).readValues(_file);
		
		while (it.hasNext()) {
			
			@SuppressWarnings("unused")
			T row = it.nextValue();

			if(row instanceof IFtraceEvent){
				
				IFtraceEvent ev = (IFtraceEvent) row;
				
				String source = ev.getSource();
				String ref = ev.getReference();
				long rank = ev.getRank();
				
				String event = rank +" : "+ source  +" : "+ ref;
				
				//System.out.println("event : "+ event);
			}
		}
	}

}
