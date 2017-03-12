package org.hotwheel.json.impl.jackson;


/**
 * Helper class used for finding and caching version information
 * for the core bundle.
 * NOTE: although defined as public, should NOT be accessed directly
 * from outside core bundle itself.
 */
public class CoreVersion extends VersionUtil {
    public final static CoreVersion instance = new CoreVersion();
}
