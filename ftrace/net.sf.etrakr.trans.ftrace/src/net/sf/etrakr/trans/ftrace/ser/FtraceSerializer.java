package net.sf.etrakr.trans.ftrace.ser;

import java.io.IOException;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FtraceSerializer extends StdSerializer<ITmfEvent> {

	protected FtraceSerializer(Class<ITmfEvent> t) {
		super(t);
	}

	@Override
	public void serialize(ITmfEvent value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		
		jgen.writeString(value.toString());
	}

}
