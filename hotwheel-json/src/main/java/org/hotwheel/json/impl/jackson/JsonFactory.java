/* Jackson JSON-processor.
 *
 * Copyright (c) 2007- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in file LICENSE, included with
 * the source code and binary code bundles.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hotwheel.json.impl.jackson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.net.URL;


/**
 * The main factory class of Jackson package, used to configure and construct
 * reader (aka parser, {@link JacksonParser}) and writer (aka generator,
 * {@link JsonGenerator}) instances.
 * <p>
 * Factory instances are thread-safe and reusable after configuration (if any).
 * Typically applications and services use only a single globally shared factory
 * instance, unless they need differently configured factories. Factory reuse is
 * important if efficiency matters; most recycling of expensive construct is
 * done on per-factory basis.
 * <p>
 * Creation of a factory instance is a light-weight operation, and since there
 * is no need for pluggable alternative implementations (as there is no
 * "standard" JSON processor API to implement), the default constructor is used
 * for constructing factory instances.
 *
 * @author Tatu Saloranta
 */
public class JsonFactory implements Versioned {
    /**
     * Name used to identify JSON format (and returned by
     * {@link #getFormatName()}
     */
    public final static String FORMAT_NAME_JSON = "JSON";

    /**
     * Bitfield (set of flags) of all factory features that are enabled by
     * default.
     */
    protected final static int DEFAULT_FACTORY_FEATURE_FLAGS = Feature
            .collectDefaults();

    /**
     * Bitfield (set of flags) of all parser features that are enabled by
     * default.
     */
    protected final static int DEFAULT_PARSER_FEATURE_FLAGS = JacksonParser.Feature
            .collectDefaults();

    /**
     * Bitfield (set of flags) of all generator features that are enabled by
     * default.
     */
    protected final static int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature
            .collectDefaults();
    /**
     * This <code>ThreadLocal</code> contains a
     * {@link java.lang.ref.SoftRerefence} to a {@link BufferRecycler} used to
     * provide a low-cost buffer recycling between reader and writer instances.
     */
    final protected static ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();

    /*
     * /********************************************************** /* Buffer,
     * symbol table management
     * /**********************************************************
     */
    /**
     * Each factory comes equipped with a shared root symbol table. It should
     * not be linked back to the original blueprint, to avoid contents from
     * leaking between factories.
     */
    protected CharsToNameCanonicalizer _rootCharSymbols = CharsToNameCanonicalizer
            .createRoot();
    /**
     * Alternative to the basic symbol table, some stream-based parsers use
     * different name canonicalization method.
     * <p>
     * should clean up this; looks messy having 2 alternatives with not very
     * clear differences.
     */
    protected BytesToNameCanonicalizer _rootByteSymbols = BytesToNameCanonicalizer
            .createRoot();
    /**
     * Object that implements conversion functionality between Java objects and
     * JSON content. For base JsonFactory implementation usually not set by
     * default, but can be explicitly set. Sub-classes (like @link
     * org.codehaus.jackson.map.MappingJsonFactory} usually provide an
     * implementation.
     */
    protected ObjectCodec _objectCodec;

    /*
     * /********************************************************** /*
     * Configuration /**********************************************************
     */
    /**
     * Currently enabled factory features.
     */
    protected int _factoryFeatures = DEFAULT_FACTORY_FEATURE_FLAGS;
    /**
     * Currently enabled parser features.
     */
    protected int _parserFeatures = DEFAULT_PARSER_FEATURE_FLAGS;
    /**
     * Currently enabled generator features.
     */
    protected int _generatorFeatures = DEFAULT_GENERATOR_FEATURE_FLAGS;
    /**
     * Definition of custom character escapes to use for generators created by
     * this factory, if any. If null, standard data format specific escapes are
     * used.
     */
    protected CharacterEscapes _characterEscapes;
    /**
     * Optional helper object that may decorate input sources, to do additional
     * processing on input during parsing.
     */
    protected InputDecorator _inputDecorator;
    /**
     * Optional helper object that may decorate output object, to do additional
     * processing on output during content generation.
     */
    protected OutputDecorator _outputDecorator;

    /**
     * Default constructor used to create factory instances. Creation of a
     * factory instance is a light-weight operation, but it is still a good idea
     * to reuse limited number of factory instances (and quite often just a
     * single instance): factories are used as context for storing some reused
     * processing objects (such as symbol tables parsers use) and this reuse
     * only works within context of a single factory instance.
     */
    public JsonFactory() {
        this(null);
    }

    /*
     * /********************************************************** /*
     * Construction /**********************************************************
     */

    public JsonFactory(ObjectCodec oc) {
        _objectCodec = oc;
    }

    /**
     * Method that returns short textual id identifying format this factory
     * supports.
     * <p>
     * Note: sub-classes should override this method; default implementation
     * will return null for all sub-classes
     */
    public String getFormatName() {
        /*
         * Somewhat nasty check: since we can't make this abstract (due to
         * backwards compatibility concerns), need to prevent format name
         * "leakage"
         */
        if (getClass() == JsonFactory.class) {
            return FORMAT_NAME_JSON;
        }
        return null;
    }

    /*
     * /********************************************************** /* Format
     * detection functionality (since 1.8)
     * /**********************************************************
     */

    public MatchStrength hasFormat(InputAccessor acc) throws IOException {
        // since we can't keep this abstract, only implement for "vanilla"
        // instance
        if (getClass() == JsonFactory.class) {
            return hasJSONFormat(acc);
        }
        return null;
    }

    protected MatchStrength hasJSONFormat(InputAccessor acc) throws IOException {
        return ByteSourceJsonBootstrapper.hasJSONFormat(acc);
    }

    // @Override
    public Version version() {
        return CoreVersion.instance.version();
    }

    /*
     * /**********************************************************
     * /* Versioned
     * /**********************************************************
     */

    /**
     * Method for enabling or disabling specified parser feature (check
     * {@link JacksonParser.Feature} for list of features)
     */
    public final JsonFactory configure(Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    /*
     * /********************************************************** /*
     * Configuration, factory features
     * /**********************************************************
     */

    /**
     * Method for enabling specified parser feature (check
     * {@link Feature} for list of features)
     */
    public JsonFactory enable(Feature f) {
        _factoryFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified parser features (check
     * {@link Feature} for list of features)
     */
    public JsonFactory disable(Feature f) {
        _factoryFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Checked whether specified parser feature is enabled.
     */
    public final boolean isEnabled(Feature f) {
        return (_factoryFeatures & f.getMask()) != 0;
    }

    /**
     * Method for enabling or disabling specified parser feature (check
     * {@link JacksonParser.Feature} for list of features)
     */
    public final JsonFactory configure(JacksonParser.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    /*
     * /********************************************************** /*
     * Configuration, parser configuration
     * /**********************************************************
     */

    /**
     * Method for enabling specified parser feature (check
     * {@link JacksonParser.Feature} for list of features)
     */
    public JsonFactory enable(JacksonParser.Feature f) {
        _parserFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified parser features (check
     * {@link JacksonParser.Feature} for list of features)
     */
    public JsonFactory disable(JacksonParser.Feature f) {
        _parserFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Checked whether specified parser feature is enabled.
     */
    public final boolean isEnabled(JacksonParser.Feature f) {
        return (_parserFeatures & f.getMask()) != 0;
    }

    /**
     * Method for getting currently configured input decorator (if any; there is
     * no default decorator).
     */
    public InputDecorator getInputDecorator() {
        return _inputDecorator;
    }

    /**
     * Method for overriding currently configured input decorator
     */
    public JsonFactory setInputDecorator(InputDecorator d) {
        _inputDecorator = d;
        return this;
    }

    /**
     * Method for enabling or disabling specified generator feature (check
     * {@link JsonGenerator.Feature} for list of features)
     */
    public final JsonFactory configure(JsonGenerator.Feature f, boolean state) {
        return state ? enable(f) : disable(f);
    }

    /*
     * /********************************************************** /*
     * Configuration, generator settings
     * /**********************************************************
     */

    /**
     * Method for enabling specified generator features (check
     * {@link JsonGenerator.Feature} for list of features)
     */
    public JsonFactory enable(JsonGenerator.Feature f) {
        _generatorFeatures |= f.getMask();
        return this;
    }

    /**
     * Method for disabling specified generator feature (check
     * {@link JsonGenerator.Feature} for list of features)
     */
    public JsonFactory disable(JsonGenerator.Feature f) {
        _generatorFeatures &= ~f.getMask();
        return this;
    }

    /**
     * Check whether specified generator feature is enabled.
     */
    public final boolean isEnabled(JsonGenerator.Feature f) {
        return (_generatorFeatures & f.getMask()) != 0;
    }

    /**
     * Method for accessing custom escapes factory uses for
     * {@link JsonGenerator}s it creates.
     */
    public CharacterEscapes getCharacterEscapes() {
        return _characterEscapes;
    }

    /**
     * Method for defining custom escapes factory uses for {@link JsonGenerator}
     * s it creates.
     */
    public JsonFactory setCharacterEscapes(CharacterEscapes esc) {
        _characterEscapes = esc;
        return this;
    }

    /**
     * Method for getting currently configured output decorator (if any; there
     * is no default decorator).
     */
    public OutputDecorator getOutputDecorator() {
        return _outputDecorator;
    }

    /**
     * Method for overriding currently configured output decorator
     */
    public JsonFactory setOutputDecorator(OutputDecorator d) {
        _outputDecorator = d;
        return this;
    }

    public ObjectCodec getCodec() {
        return _objectCodec;
    }

    /*
     * /********************************************************** /*
     * Configuration, other
     * /**********************************************************
     */

    /**
     * Method for associating a {@link ObjectCodec} (typically a
     * <code>com.fasterxml.jackson.databind.ObjectMapper</code>) with this
     * factory (and more importantly, parsers and generators it constructs).
     * This is needed to use data-binding methods of {@link JacksonParser} and
     * {@link JsonGenerator} instances.
     */
    public JsonFactory setCodec(ObjectCodec oc) {
        _objectCodec = oc;
        return this;
    }

    /**
     * Method for constructing JSON parser instance to parse contents of
     * specified file. Encoding is auto-detected from contents according to JSON
     * specification recommended mechanism.
     * <p>
     * Underlying input stream (needed for reading contents) will be
     * <b>owned</b> (and managed, i.e. closed as need be) by the parser, since
     * caller has no access to it.
     *
     * @param f File that contains JSON content to parse
     */
    @SuppressWarnings("resource")
    public JacksonParser createJsonParser(File f) throws IOException,
            JsonParseException {
        // true, since we create InputStream from File
        IOContext ctxt = _createContext(f, true);
        InputStream in = new FileInputStream(f);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            in = _inputDecorator.decorate(ctxt, in);
        }
        return _createJsonParser(in, ctxt);
    }

    /*
     * /********************************************************** /* Reader
     * factories /**********************************************************
     */

    /**
     * Method for constructing JSON parser instance to parse contents of
     * resource reference by given URL. Encoding is auto-detected from contents
     * according to JSON specification recommended mechanism.
     * <p>
     * Underlying input stream (needed for reading contents) will be
     * <b>owned</b> (and managed, i.e. closed as need be) by the parser, since
     * caller has no access to it.
     *
     * @param url URL pointing to resource that contains JSON content to parse
     */
    public JacksonParser createJsonParser(URL url) throws IOException,
            JsonParseException {
        // true, since we create InputStream from URL
        IOContext ctxt = _createContext(url, true);
        InputStream in = _optimizedStreamFromURL(url);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            in = _inputDecorator.decorate(ctxt, in);
        }
        return _createJsonParser(in, ctxt);
    }

    /**
     * Method for constructing JSON parser instance to parse the contents
     * accessed via specified input stream.
     * <p>
     * The input stream will <b>not be owned</b> by the parser, it will still be
     * managed (i.e. closed if end-of-stream is reacher, or parser close method
     * called) if (and only if)
     * {@link org.hotwheel.json.impl.jackson.JacksonParser.Feature#AUTO_CLOSE_SOURCE}
     * is enabled.
     * <p>
     * Note: no encoding argument is taken since it can always be auto-detected
     * as suggested by Json RFC.
     *
     * @param in InputStream to use for reading JSON content to parse
     */
    public JacksonParser createJsonParser(InputStream in) throws IOException,
            JsonParseException {
        IOContext ctxt = _createContext(in, false);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            in = _inputDecorator.decorate(ctxt, in);
        }
        return _createJsonParser(in, ctxt);
    }

    /**
     * Method for constructing parser for parsing the contents accessed via
     * specified Reader.
     * <p>
     * The read stream will <b>not be owned</b> by the parser, it will still be
     * managed (i.e. closed if end-of-stream is reacher, or parser close method
     * called) if (and only if)
     * {@link org.hotwheel.json.impl.jackson.JacksonParser.Feature#AUTO_CLOSE_SOURCE}
     * is enabled.
     * <p>
     *
     * @param r Reader to use for reading JSON content to parse
     */
    public JacksonParser createJsonParser(Reader r) throws IOException,
            JsonParseException {
        // false -> we do NOT own Reader (did not create it)
        IOContext ctxt = _createContext(r, false);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            r = _inputDecorator.decorate(ctxt, r);
        }
        return _createJsonParser(r, ctxt);
    }

    /**
     * Method for constructing parser for parsing the contents of given byte
     * array.
     */
    public JacksonParser createJsonParser(byte[] data) throws IOException,
            JsonParseException {
        IOContext ctxt = _createContext(data, true);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            InputStream in = _inputDecorator.decorate(ctxt, data, 0,
                    data.length);
            if (in != null) {
                return _createJsonParser(in, ctxt);
            }
        }
        return _createJsonParser(data, 0, data.length, ctxt);
    }

    /**
     * Method for constructing parser for parsing the contents of given byte
     * array.
     *
     * @param data   Buffer that contains data to parse
     * @param offset Offset of the first data byte within buffer
     * @param len    Length of contents to parse within buffer
     */
    public JacksonParser createJsonParser(byte[] data, int offset, int len)
            throws IOException, JsonParseException {
        IOContext ctxt = _createContext(data, true);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            InputStream in = _inputDecorator.decorate(ctxt, data, offset, len);
            if (in != null) {
                return _createJsonParser(in, ctxt);
            }
        }
        return _createJsonParser(data, offset, len, ctxt);
    }

    /**
     * Method for constructing parser for parsing contents of given String.
     */
    public JacksonParser createJsonParser(String content) throws IOException,
            JsonParseException {
        Reader r = new StringReader(content);
        // true -> we own the Reader (and must close); not a big deal
        IOContext ctxt = _createContext(r, true);
        // [JACKSON-512]: allow wrapping with InputDecorator
        if (_inputDecorator != null) {
            r = _inputDecorator.decorate(ctxt, r);
        }
        return _createJsonParser(r, ctxt);
    }

    /**
     * Method for constructing JSON generator for writing JSON content using
     * specified output stream. Encoding to use must be specified, and needs to
     * be one of available types (as per JSON specification).
     * <p>
     * Underlying stream <b>is NOT owned</b> by the generator constructed, so
     * that generator will NOT close the output stream when
     * {@link JsonGenerator#close} is called (unless auto-closing feature,
     * {@link org.hotwheel.json.impl.jackson.JsonGenerator.Feature#AUTO_CLOSE_TARGET}
     * is enabled). Using application needs to close it explicitly if this is
     * the case.
     * <p>
     * Note: there are formats that use fixed encoding (like most binary data
     * formats) and that ignore passed in encoding.
     *
     * @param out OutputStream to use for writing JSON content
     * @param enc Character encoding to use
     */
    public JsonGenerator createJsonGenerator(OutputStream out, JsonEncoding enc)
            throws IOException {
        // false -> we won't manage the stream unless explicitly directed to
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            // [JACKSON-512]: allow wrapping with _outputDecorator
            if (_outputDecorator != null) {
                out = _outputDecorator.decorate(ctxt, out);
            }
            return _createUTF8JsonGenerator(out, ctxt);
        }
        Writer w = _createWriter(out, enc, ctxt);
        // [JACKSON-512]: allow wrapping with _outputDecorator
        if (_outputDecorator != null) {
            w = _outputDecorator.decorate(ctxt, w);
        }
        return _createJsonGenerator(w, ctxt);
    }

    /*
     * /********************************************************** /* Generator
     * factories /**********************************************************
     */

    /**
     * Method for constructing JSON generator for writing JSON content using
     * specified Writer.
     * <p>
     * Underlying stream <b>is NOT owned</b> by the generator constructed, so
     * that generator will NOT close the Reader when {@link JsonGenerator#close}
     * is called (unless auto-closing feature,
     * {@link org.hotwheel.json.impl.jackson.JsonGenerator.Feature#AUTO_CLOSE_TARGET}
     * is enabled). Using application needs to close it explicitly.
     *
     * @param out Writer to use for writing JSON content
     */
    public JsonGenerator createJsonGenerator(Writer out) throws IOException {
        IOContext ctxt = _createContext(out, false);
        // [JACKSON-512]: allow wrapping with _outputDecorator
        if (_outputDecorator != null) {
            out = _outputDecorator.decorate(ctxt, out);
        }
        return _createJsonGenerator(out, ctxt);
    }

    /**
     * Convenience method for constructing generator that uses default encoding
     * of the format (UTF-8 for JSON and most other data formats).
     * <p>
     * Note: there are formats that use fixed encoding (like most binary data
     * formats).
     */
    public JsonGenerator createJsonGenerator(OutputStream out)
            throws IOException {
        return createJsonGenerator(out, JsonEncoding.UTF8);
    }

    /**
     * Method for constructing JSON generator for writing JSON content to
     * specified file, overwriting contents it might have (or creating it if
     * such file does not yet exist). Encoding to use must be specified, and
     * needs to be one of available types (as per JSON specification).
     * <p>
     * Underlying stream <b>is owned</b> by the generator constructed, i.e.
     * generator will handle closing of file when {@link JsonGenerator#close} is
     * called.
     *
     * @param f   File to write contents to
     * @param enc Character encoding to use
     */
    public JsonGenerator createJsonGenerator(File f, JsonEncoding enc)
            throws IOException {
        OutputStream out = new FileOutputStream(f);
        // true -> yes, we have to manage the stream since we created it
        IOContext ctxt = _createContext(out, true);
        ctxt.setEncoding(enc);
        if (enc == JsonEncoding.UTF8) {
            // [JACKSON-512]: allow wrapping with _outputDecorator
            if (_outputDecorator != null) {
                out = _outputDecorator.decorate(ctxt, out);
            }
            return _createUTF8JsonGenerator(out, ctxt);
        }
        Writer w = _createWriter(out, enc, ctxt);
        // [JACKSON-512]: allow wrapping with _outputDecorator
        if (_outputDecorator != null) {
            w = _outputDecorator.decorate(ctxt, w);
        }
        return _createJsonGenerator(w, ctxt);
    }

    /**
     * Overridable factory method that actually instantiates desired parser
     * given {@link InputStream} and context object.
     * <p>
     * This method is specifically designed to remain compatible between minor
     * versions so that sub-classes can count on it being called as expected.
     * That is, it is part of official interface from sub-class perspective,
     * although not a public method available to users of factory
     * implementations.
     */
    protected JacksonParser _createJsonParser(InputStream in, IOContext ctxt)
            throws IOException, JsonParseException {
        // As per [JACKSON-259], may want to fully disable canonicalization:
        return new ByteSourceJsonBootstrapper(ctxt, in).constructParser(
                _parserFeatures, _objectCodec, _rootByteSymbols,
                _rootCharSymbols,
                isEnabled(Feature.CANONICALIZE_FIELD_NAMES),
                isEnabled(Feature.INTERN_FIELD_NAMES));
    }

    /*
     * /********************************************************** /* Factory
     * methods used by factory for creating parser instances, /* overridable by
     * sub-classes /**********************************************************
     */

    /**
     * Overridable factory method that actually instantiates parser using given
     * {@link Reader} object for reading content.
     * <p>
     * This method is specifically designed to remain compatible between minor
     * versions so that sub-classes can count on it being called as expected.
     * That is, it is part of official interface from sub-class perspective,
     * although not a public method available to users of factory
     * implementations.
     */
    protected JacksonParser _createJsonParser(Reader r, IOContext ctxt)
            throws IOException, JsonParseException {
        return new ReaderBasedJsonParser(
                ctxt,
                _parserFeatures,
                r,
                _objectCodec,
                _rootCharSymbols
                        .makeChild(
                                isEnabled(Feature.CANONICALIZE_FIELD_NAMES),
                                isEnabled(Feature.INTERN_FIELD_NAMES)));
    }

    /**
     * Overridable factory method that actually instantiates parser using given
     * {@link Reader} object for reading content passed as raw byte array.
     * <p>
     * This method is specifically designed to remain compatible between minor
     * versions so that sub-classes can count on it being called as expected.
     * That is, it is part of official interface from sub-class perspective,
     * although not a public method available to users of factory
     * implementations.
     */
    protected JacksonParser _createJsonParser(byte[] data, int offset,
                                              int len, IOContext ctxt) throws IOException, JsonParseException {
        return new ByteSourceJsonBootstrapper(ctxt, data, offset, len)
                .constructParser(
                        _parserFeatures,
                        _objectCodec,
                        _rootByteSymbols,
                        _rootCharSymbols,
                        isEnabled(Feature.CANONICALIZE_FIELD_NAMES),
                        isEnabled(Feature.INTERN_FIELD_NAMES));
    }

    /**
     * Overridable factory method that actually instantiates generator for given
     * {@link Writer} and context object.
     * <p>
     * This method is specifically designed to remain compatible between minor
     * versions so that sub-classes can count on it being called as expected.
     * That is, it is part of official interface from sub-class perspective,
     * although not a public method available to users of factory
     * implementations.
     */
    protected JsonGenerator _createJsonGenerator(Writer out, IOContext ctxt)
            throws IOException {
        WriterBasedJsonGenerator gen = new WriterBasedJsonGenerator(ctxt,
                _generatorFeatures, _objectCodec, out);
        if (_characterEscapes != null) {
            gen.setCharacterEscapes(_characterEscapes);
        }
        return gen;
    }

    /*
     * /********************************************************** /* Factory
     * methods used by factory for creating generator instances, /* overridable
     * by sub-classes
     * /**********************************************************
     */

    /**
     * Overridable factory method that actually instantiates generator for given
     * {@link OutputStream} and context object, using UTF-8 encoding.
     * <p>
     * This method is specifically designed to remain compatible between minor
     * versions so that sub-classes can count on it being called as expected.
     * That is, it is part of official interface from sub-class perspective,
     * although not a public method available to users of factory
     * implementations.
     */
    protected JsonGenerator _createUTF8JsonGenerator(OutputStream out,
                                                     IOContext ctxt) throws IOException {
        UTF8JsonGenerator gen = new UTF8JsonGenerator(ctxt, _generatorFeatures,
                _objectCodec, out);
        if (_characterEscapes != null) {
            gen.setCharacterEscapes(_characterEscapes);
        }
        return gen;
    }

    protected Writer _createWriter(OutputStream out, JsonEncoding enc,
                                   IOContext ctxt) throws IOException {
        // note: this should not get called any more (caller checks, dispatches)
        if (enc == JsonEncoding.UTF8) { // We have optimized writer for UTF-8
            return new UTF8Writer(ctxt, out);
        }
        // not optimal, but should do unless we really care about UTF-16/32
        // encoding speed
        return new OutputStreamWriter(out, enc.getJavaName());
    }

    /**
     * Overridable factory method that actually instantiates desired context
     * object.
     */
    protected IOContext _createContext(Object srcRef, boolean resourceManaged) {
        return new IOContext(_getBufferRecycler(), srcRef, resourceManaged);
    }

    /*
     * /********************************************************** /* Internal
     * factory methods, other
     * /**********************************************************
     */

    /**
     * Method used by factory to create buffer recycler instances for parsers
     * and generators.
     * <p>
     * Note: only public to give access for <code>ObjectMapper</code>
     */
    public BufferRecycler _getBufferRecycler() {
        SoftReference<BufferRecycler> ref = _recyclerRef.get();
        BufferRecycler br = (ref == null) ? null : ref.get();

        if (br == null) {
            br = new BufferRecycler();
            _recyclerRef.set(new SoftReference<BufferRecycler>(br));
        }
        return br;
    }

    /**
     * Helper methods used for constructing an optimal stream for parsers to
     * use, when input is to be read from an URL. This helps when reading file
     * content via URL.
     */
    protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
        if ("file".equals(url.getProtocol())) {
            /*
             * Can not do this if the path refers to a network drive on windows.
             * This fixes the problem; might not be needed on all platforms
             * (NFS?), but should not matter a lot: performance penalty of extra
             * wrapping is more relevant when accessing local file system.
             */
            String host = url.getHost();
            if (host == null || host.length() == 0) {
                return new FileInputStream(url.getPath());
            }
        }
        return url.openStream();
    }

    /**
     * Enumeration that defines all on/off features that can only be changed for
     * {@link JsonFactory}.
     */
    public enum Feature {

        // // // Symbol handling (interning etc)

        /**
         * Feature that determines whether JSON object field names are to be
         * canonicalized using {@link String#intern} or not: if enabled, all
         * field names will be intern()ed (and caller can count on this being
         * true for all such names); if disabled, no intern()ing is done. There
         * may still be basic canonicalization (that is, same String will be
         * used to represent all identical object property names for a single
         * document).
         * <p>
         * Note: this setting only has effect if
         * {@link #CANONICALIZE_FIELD_NAMES} is true -- otherwise no
         * canonicalization of any sort is done.
         * <p>
         * This setting is enabled by default.
         */
        INTERN_FIELD_NAMES(true),

        /**
         * Feature that determines whether JSON object field names are to be
         * canonicalized (details of how canonicalization is done then further
         * specified by {@link #INTERN_FIELD_NAMES}).
         * <p>
         * This setting is enabled by default.
         */
        CANONICALIZE_FIELD_NAMES(true);

        /**
         * Whether feature is enabled or disabled by default.
         */
        private final boolean _defaultState;

        private Feature(boolean defaultState) {
            _defaultState = defaultState;
        }

        /**
         * Method that calculates bit set (flags) of all features that are
         * enabled by default.
         */
        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : values()) {
                if (f.enabledByDefault()) {
                    flags |= f.getMask();
                }
            }
            return flags;
        }

        public boolean enabledByDefault() {
            return _defaultState;
        }

        public boolean enabledIn(int flags) {
            return (flags & getMask()) != 0;
        }

        public int getMask() {
            return (1 << ordinal());
        }
    }
}
