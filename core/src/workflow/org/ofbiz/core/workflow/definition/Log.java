/* $Id$ */
/* $Source$ */

/*
 * $Log$
 */

package org.ofbiz.core.workflow.definition;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.io.PrintStream;
import java.io.PipedOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;

/** 
 * <p>Log - Initializes Log4j with standard settings.
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author <a href='mailto:ofb@traxel.com'>Robert Bushman</a>
 * @created Sat Nov 10 19:22:42 MST 2001
 * @version 0.0
 */
public abstract class Log {
    
    /**
     * <b>Edit this in the source code for easiest use by a large team.</b>
     * For example, you could set this to
     * "http://projectserver.company.com/etc/log4j.client.properties",
     * and post a default configuration file on your team's web server.
     * Then, all your team members would have to do to insure log
     * initialization would be to call, "Log.init()". 
     */
    public static final String DEFAULT_CONFIG_URL = null;
    
    // -------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------
    
    public static final String ROOT_CATEGORY_NAME = "log4j.rootCategory";
    
    public static final String[] DEFAULT_CONFIG_DIRECTORIES = {
        ".", "etc", "conf",
    };
    public static final String[] DEFAULT_DEPTHS = {
        ".", "..", "../..",
    };
    public static final
        String DEFAULT_CONFIG_FILE_NAME = "log4j.properties";
    
    public static final String LEVEL_DEBUG = "Debug";
    public static final String LEVEL_INFO = "Info";
    public static final String LEVEL_WARN = "Warn";
    public static final String LEVEL_ERROR = "Error";
    public static final String LEVEL_FATAL = "Fatal";
    
    /** Indicates all log messages should be sent to the bit bucket. */
    public static final int FALLBACK_CONFIG_NULL = 0;
    /** Indicates all log messages should be sent to the command line. */
    public static final int FALLBACK_CONFIG_CLI = 1;
    
    /** <b>Edit this in source code for easiest use by a large team.</b> */
    public static final int DEFAULT_FALLBACK_CONFIG = FALLBACK_CONFIG_CLI;
    
    // -------------------------------------------------------------
    // STATIC PARAMETERS AND ACCESSORS
    // -------------------------------------------------------------
    
    // PARAMETERS
    private static boolean _initialized = false;
    private static URL _configUrl = null;
    private static int _fallbackConfig = DEFAULT_FALLBACK_CONFIG;
    
    // GETTERS
    public static boolean isInitialized() { return( getInitialized() ); }
    public static boolean getInitialized() { return( _initialized ); }
    public static URL getConfigUrl() { return( _configUrl ); }
    public static int getFallbackConfig() { return( _fallbackConfig ); }
    
    // SETTERS
    protected static void setInitialized( boolean initialized ) {
        _initialized = initialized;
    }
    /**
     * IMHO, you should alter DEFAULT_CONFIG_URL in the source of
     * this file and <b>never call this method</b>, except when doing
     * a reinitialization at runtime to debug a flakey process.
     */
    public static void setConfigUrl( URL url ) {
        _configUrl = url;
    }
    /**
     * IMHO, you should alter DEFAULT_CONFIG_URL in the source of
     * this file and <b>never call this method</b>, except when doing
     * a reinitialization at runtime to debug a flakey process.
     */
    public static void setFallbackConfig( int fallback ) {
        _fallbackConfig = fallback;
    } 
    
    // --------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------
    
    private Log() {}
    
    // --------------------------------------------------------
    // PUBLIC API
    // --------------------------------------------------------
    
    /**
     * <b>If not initialized</b>, initializes from the set
     * configuration URL.
     */ 
    public static void init() {
        if( getInitialized() ) { return; }
        synchronized( Log.class ) {
            // Check again.
            if( getInitialized() ) { return; }
            forceInit();
        }
    }
    
    /** Forces initialization from the set configuration URL. */
    public synchronized static void forceInit() {
        reinitialize( getBestConfigUrl() );
    }
    
    /**
     * <b>Call reinitialize() when you're done.</b>
     * Forces reinitialization from the given URL, but
     * <b>does not alter</b> the set configuration URL.
     * Thus, calling
     * <pre><tt>
     *   Log.reinitialize( url );
     *   ... do suspect stuff ...
     *   Log.reinitialize();
     * </tt></pre>
     * will use temporary logging for the suspect section.
     */
    public synchronized static void reinitialize( URL url ) {
        setInitialized( false );
        final PrintStream err = System.err;
        try { System.setErr( new PrintStream( new PipedOutputStream() ) ); }
        catch( Exception e ) {}
        
        try {
            // Initialize
            if( url != null ) {
                try {
                    PropertyConfigurator.resetConfiguration();
                    PropertyConfigurator.configure( url );
                    setInitialized( true );
                } catch( Exception e ) {
                    fallbackInit();
                }
            } else {
                fallbackInit();
            }
        } finally {
            // Restore System.err
            try { System.setErr( err ); }
            catch( Exception e ) {}
        }
    }
    
    // ---------------------------------------------------------
    // INTERNAL API
    // ---------------------------------------------------------
    
    protected static URL getBestConfigUrl() {
        final String cliUrlString =
            System.getProperty( "log4j.configuration" );
        final URL setUrl = getConfigUrl();
        
        if( cliUrlString != null ) {
            try {
                final URL url = new URL( cliUrlString );
                url.openConnection();
                return( url );
            } catch( Exception e ) {}
        }
        if( setUrl != null ) {
            try {
                setUrl.openConnection();
                return( setUrl );
            } catch( Exception e ) {}
        }
        for( int j = 0; j < DEFAULT_DEPTHS.length; j++ ) {
            final String depth = DEFAULT_DEPTHS[j];
            for( int i = 0; i < DEFAULT_CONFIG_DIRECTORIES.length; i++ ) {
                final String dir = DEFAULT_CONFIG_DIRECTORIES[i];
                final String fileName = 
                    depth + "/" + dir + "/" + DEFAULT_CONFIG_FILE_NAME;
                try {
                    final File file = new File( fileName );
                    if( file.isFile() ) { return( file.toURL() ); }
                } catch( Exception e ) {}
            }
        }
        
        return( null );
    }
    
    protected static void fallbackInit() {
        if( getFallbackConfig() == FALLBACK_CONFIG_NULL ) {
            nullInit();
        } else {
            cliInit();
        }
    }
    
    protected static void cliInit() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        setInitialized( true );
    }
    
    protected static void nullInit() {
        BasicConfigurator.resetConfiguration();
        final Appender nullAppender = new AppenderSkeleton() {
                public void append( LoggingEvent event ) {}
                public void doAppend( LoggingEvent event ) {}
                public void close() {}
                public boolean requiresLayout() { return( false ); }
            };
        nullAppender.setName( "NULL_APPENDER" );
        Category.getRoot().removeAllAppenders();
        Category.getRoot().setPriority( Priority.FATAL );
        Category.getRoot().addAppender( nullAppender );
        setInitialized( true );
    }
}
