/**
 * @(#)TemplatorCache.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.hotwheel.context.template;

import java.io.IOException;
import java.util.HashMap;

/**
 * A cache manager for MiniTemplator objects.
 * This class is used to cache MiniTemplator objects in memory, so that
 * each template file is only read and parsed once.
 */
public class TemplatorCache {

    private HashMap<String, Templator> cache; // buffered MiniTemplator objects

    /**
     * Creates a new MiniTemplatorCache object.
     */
    public TemplatorCache() {
        cache = new HashMap<String, Templator>();
    }

    private static String generateCacheKey(TemplateSpecification templateSpec) {
        StringBuilder key = new StringBuilder(128);
        if (templateSpec.templateText != null) {
            key.append(templateSpec.templateText);
        } else if (templateSpec.templateFileName != null) {
            key.append(templateSpec.templateFileName);
        } else {
            throw new IllegalArgumentException(
                    "No templateFileName or templateText specified.");
        }
        if (templateSpec.conditionFlags != null) {
            for (String flag : templateSpec.conditionFlags) {
                key.append('|');
                key.append(flag.toUpperCase());
            }
        }
        return key.toString();
    }

    public synchronized Templator get(TemplateSpecification templateSpec)
            throws IOException, TemplateSyntaxException {
        String key = generateCacheKey(templateSpec);
        Templator mt = cache.get(key);
        if (mt == null) {
            mt = new Templator(templateSpec);
            cache.put(key, mt);
        }
        return mt.cloneReset();
    }

    /**
     * Clears the cache.
     */
    public synchronized void clear() {
        cache.clear();
    }

}
