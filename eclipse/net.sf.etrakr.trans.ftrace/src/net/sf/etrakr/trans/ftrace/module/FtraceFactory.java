package net.sf.etrakr.trans.ftrace.module;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import net.sf.etrakr.ftrace.core.service.IFtraceService;
import net.sf.etrakr.ftrace.core.service.impl.FtraceService;
import net.sf.etrakr.trans.ftrace.deser.FtraceParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.databind.JsonMappingException;

public class FtraceFactory extends JsonFactory {

	private static final long serialVersionUID = 1L;

	public static final String FORMAT_NAME_ATRACE = "ATRACE";
	
	private URI fileUri = URI.create("http://net.sf.notrace/"+ UUID.randomUUID());
	
	private IFtraceService ftraceService = new FtraceService();
	/*
    /**********************************************************************
    /* Factory construction, configuration
    /**********************************************************************
     */
	public FtraceFactory(JsonFactory src, ObjectCodec codec) {
		super(src, codec);
		super._objectCodec = codec;
	}

	public FtraceFactory(ObjectCodec oc) {
		super(oc);
		super._objectCodec = oc;
	}

	public FtraceFactory() {
		this(null);
	}
	@Override
	public JsonFactory copy() {
		_checkInvalidCopy(FtraceFactory.class);
        return new FtraceFactory(this, this.getCodec());
	}
	/*
    /**********************************************************
    /* Format detection functionality
    /**********************************************************
     */
	@Override
    public String getFormatName() {
        return FORMAT_NAME_ATRACE;
    }
	
	/*
    /**********************************************************
    /* Serializable overrides
    /**********************************************************
     */
    /**
     * Method that we need to override to actually make restoration go
     * through constructors etc.
     * Also: must be overridden by sub-classes as well.
     */
	@Override
	protected Object readResolve() {
		return new FtraceFactory(this, _objectCodec);
	}
	
	/*
    /******************************************************
    /* Overridden internal factory methods
    /******************************************************
     */
	@Override
	protected FtraceParser _createParser(InputStream in, IOContext ctxt)
			throws IOException {
		
		InputStreamReader inr = new InputStreamReader(in);
		
		return new FtraceParser(ftraceService, ctxt, super._objectCodec);
	}

	@Override
	protected FtraceParser _createParser(Reader r, IOContext ctxt)
			throws IOException {
		
		return new FtraceParser(ftraceService, ctxt, super._objectCodec);
	}

	@Override
	protected FtraceParser _createParser(char[] data, int offset, int len,
			IOContext ctxt, boolean recyclable) throws IOException {
		
		//CharArrayReader car = new CharArrayReader(data, offset, len);
		
		String resultString = new String(data, offset, len);
		
		byte[] b = resultString.getBytes();
		
		return new FtraceParser(ftraceService, ctxt, super._objectCodec);
	}

	@Override
	protected FtraceParser _createParser(byte[] data, int offset, int len,
			IOContext ctxt) throws IOException {
		
		ByteArrayInputStream bai = new ByteArrayInputStream(data, offset, (offset + len));
		
		InputStreamReader inr = new InputStreamReader(bai);
		
		return new FtraceParser(ftraceService, ctxt, super._objectCodec);
	}

	 /*
    /**********************************************************
    /* Overridden parser factory methods, 2.1
    /**********************************************************
     */
	@Override
	public FtraceParser createParser(File f) throws IOException,
			JsonParseException {
		
		this.fileUri = f.toURI();
		
		ftraceService.addTrace(this.fileUri);
		
		return _createParser(new FileInputStream(f), _createContext(f, true));
	}

	@Override
	public FtraceParser createParser(URL url) throws IOException,
			JsonParseException {
		
		try {
			
			this.fileUri = url.toURI();
			
			ftraceService.addTrace(this.fileUri);
			
		} catch (URISyntaxException e) {
			
			throw new JsonMappingException(e.getMessage());
			
		}
				
		return _createParser(_optimizedStreamFromURL(url), _createContext(url, true));
	}

	@Override
	public FtraceParser createParser(InputStream in) throws IOException,
			JsonParseException {
		
		/* Fix me. RH
		 * FtraceService requires to build a cache table base on file ize
		 * Reader is not support at the time */
		throw new JsonMappingException("Not support, please use File or URL instead");
		
		//return _createParser(in, _createContext(in, false));
	}

	@Override
	public FtraceParser createParser(Reader r) throws IOException,
			JsonParseException {
		
		/* Fix me. RH
		 * FtraceService requires to build a cache table base on file ize
		 * Reader is not support at the time */
		throw new JsonMappingException("Not support, please use File or URL instead");
		
		//return _createParser(r, _createContext(r, false));
	}

	@Override
	public FtraceParser createParser(byte[] data) throws IOException,
			JsonParseException {
		return _createParser(data, 0, data.length, _createContext(data, true));
	}

	@Override
	public FtraceParser createParser(byte[] data, int offset, int len)
			throws IOException, JsonParseException {
		return _createParser(data, offset, len, _createContext(data, true));
	}

	@Override
	public FtraceParser createParser(String content) throws IOException,
			JsonParseException {
		return _createParser(new StringReader(content), _createContext(content, true));
	}

	@Override
	public FtraceParser createParser(char[] content) throws IOException {
		return _createParser(content, 0, content.length, _createContext(content, true), false);
	}

	@Override
	public FtraceParser createParser(char[] content, int offset, int len)
			throws IOException {
		return _createParser(content, offset, len, _createContext(content, true), false);
	}

}
