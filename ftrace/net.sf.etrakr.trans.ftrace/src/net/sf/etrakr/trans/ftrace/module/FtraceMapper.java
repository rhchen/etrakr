package net.sf.etrakr.trans.ftrace.module;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

public class FtraceMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public FtraceMapper() {
		
		this(new FtraceFactory());
		
		this.getFactory().setCodec(this);
	}

	public FtraceMapper(JsonFactory jf, DefaultSerializerProvider sp,
			DefaultDeserializationContext dc) {
		super(jf, sp, dc);
		
		this.getFactory().setCodec(this);
	}

	public FtraceMapper(JsonFactory jf) {
		super(jf);
	}

	public FtraceMapper(ObjectMapper src) {
		super(src);
	}

}
