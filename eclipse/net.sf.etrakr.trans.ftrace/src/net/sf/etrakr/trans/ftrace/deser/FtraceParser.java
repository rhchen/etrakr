package net.sf.etrakr.trans.ftrace.deser;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

import net.sf.etrakr.ftrace.core.service.IFtraceService;


import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.google.common.collect.BiMap;
import com.google.common.collect.Maps;

public class FtraceParser extends ParserMinimalBase {

	private IOContext _ioContext;
	
	private IFtraceService _service;
	
	/* Codec used for data binding when (if) requested. */
     
    protected ObjectCodec _objectCodec;
    
    private URI url = null;
    
    private BiMap<Long, Integer> rankTable = null;
    
    private long nbEvents = 0;
    
	public FtraceParser(IFtraceService service, IOContext ctxt, ObjectCodec objectCodec) {
		
		this._ioContext = ctxt;
		this._objectCodec = objectCodec;
		this._service = service;
		
		Object o = this._ioContext.getSourceReference();
		
		if(o instanceof File){
			
			this.url = ((File) o).toURI();
			
		}else{
			
			this.url = (URI) o;
		}
		
		this.rankTable = this._service.getRankTable(url);
		
		TreeMap<Long, Integer> tMap = Maps.<Long, Integer>newTreeMap();
		
		tMap.putAll(this.rankTable);
		
		nbEvents = tMap.lastKey();
	}
	
	private int rank = 0;
	
	public ITmfEvent nextEvent() throws JsonParseException{
		
		try {
			
			return this._service.getTmfEvent(this.url, rank);
			
		} catch (ExecutionException e) {
			
			throw new JsonParseException(e.getMessage(), this.getTokenLocation());
		}
	}
	
	@Override
	public JsonToken nextToken() throws IOException, JsonParseException {
		
		rank++;
		
		if(rank > nbEvents) return null;
		
		return super._currToken = JsonToken.START_OBJECT;
	}

	@Override
	protected void _handleEOF() throws JsonParseException {
		
	}

	@Override
	public String getCurrentName() throws IOException {
		return null;
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	@Override
	public JsonStreamContext getParsingContext() {
		return null;
	}

	@Override
	public void overrideCurrentName(String name) {
		
	}

	@Override
	public String getText() throws IOException {
		return null;
	}

	@Override
	public char[] getTextCharacters() throws IOException {
		return null;
	}

	@Override
	public boolean hasTextCharacters() {
		return false;
	}

	@Override
	public int getTextLength() throws IOException {
		return 0;
	}

	@Override
	public int getTextOffset() throws IOException {
		return 0;
	}

	@Override
	public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
		return null;
	}

	@Override
    public ObjectCodec getCodec() {
        return _objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        _objectCodec = c;
    }

	@Override
	public Version version() {
		return null;
	}

	@Override
	public JsonLocation getTokenLocation() {
		return null;
	}

	@Override
	public JsonLocation getCurrentLocation() {
		return null;
	}

	@Override
	public Number getNumberValue() throws IOException {
		return null;
	}

	@Override
	public NumberType getNumberType() throws IOException {
		return null;
	}

	@Override
	public int getIntValue() throws IOException {
		return 0;
	}

	@Override
	public long getLongValue() throws IOException {
		return 0;
	}

	@Override
	public BigInteger getBigIntegerValue() throws IOException {
		return null;
	}

	@Override
	public float getFloatValue() throws IOException {
		return 0;
	}

	@Override
	public double getDoubleValue() throws IOException {
		return 0;
	}

	@Override
	public BigDecimal getDecimalValue() throws IOException {
		return null;
	}

	@Override
	public Object getEmbeddedObject() throws IOException {
		return null;
	}

}
