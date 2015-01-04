package net.sf.etrakr.trans.ftrace.deser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FtraceDeserializer extends StdDeserializer<ITmfEvent> {

	private static final long serialVersionUID = 1L;

	private Map<String, Class<? extends ITmfEvent>> registry = new HashMap<String, Class<? extends ITmfEvent>>();  
	
	public FtraceDeserializer() {
		super(ITmfEvent.class);
	}

	@Override
	public ITmfEvent deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		//ObjectNode root = (ObjectNode) mapper.readTree(jp);  
		
		//return mapper.reader().readValue(jp);
		
		FtraceParser p = (FtraceParser) jp;
		
		return p.nextEvent();
		
	}

}
