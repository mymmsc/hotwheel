/**
 * Copyright (c) 2012-2015, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hotwheel.j2ee.util.manifests;

import org.hotwheel.util.manifests.Mfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Manifests in servlet context.
 *
 * Append attributes from the web application {@code MANIFEST.MF}.
 *
 * <p>You can use this class in your own
 * {@link javax.servlet.Filter} or
 * {@link javax.servlet.ServletContextListener},
 * in order to inject {@code MANIFEST.MF} attributes to the class:
 *
 * <pre> Manifests.append(new ServletMfs(context));</pre>
 *
 * <p>The class is thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id: ddec9f1c26c4c1756289000814820fd34fbe343d $
 * @since 1.0
 */
public final class ServletMfs implements Mfs {
    private static Logger logger = LoggerFactory.getLogger(ServletMfs.class);

    /**
     * Servlet context.
     */
    private final transient ServletContext ctx;

    /**
     * Ctor.
     * @param context Context
     */
    public ServletMfs(final ServletContext context) {
        this.ctx = context;
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final URL main = this.ctx.getResource("/META-INF/MANIFEST.MF");
        final Collection<InputStream> streams = new ArrayList<InputStream>(1);
        if (main == null) {
            logger.warn("MANIFEST.MF not found in WAR package");
        } else {
            streams.add(main.openStream());
        }
        return streams;
    }

}
