package org.mymmsc.j2ee.annotation;

import java.lang.annotation.*;

/**
 * Created by wangfeng on 2016/10/29.
 * @since 6.10.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {

    /**
     * The primary mapping expressed by this annotation.
     * <p>In a Servlet environment: the path mapping URIs (e.g. "/myPath.do").
     * Ant-style path patterns are also supported (e.g. "/myPath/*.do").
     * At the method level, relative paths (e.g. "edit.do") are supported
     * within the primary mapping expressed at the type level.
     * Path mapping URIs may contain placeholders (e.g. "/${connect}")
     * <p>In a Portlet environment: the mapped portlet modes
     * (i.e. "EDIT", "VIEW", "HELP" or any custom modes).
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings inherit
     * this primary mapping, narrowing it for a specific handler method.
     */
    //String[] value() default {};
    String value();

    /**
     * The HTTP request methods to map to, narrowing the primary mapping:
     * GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings inherit
     * this HTTP method restriction (i.e. the type-level restriction
     * gets checked before the handler method is even resolved).
     * <p>Supported for Servlet environments as well as Portlet 2.0 environments.
     */
    RequestMethod[] method() default {};

    /**
     * The parameters of the mapped request, narrowing the primary mapping.
     * <p>Same format for any environment: a sequence of "myParam=myValue" style
     * expressions, with a request only mapped if each such parameter is found
     * to have the given value. Expressions can be negated by using the "!=" operator,
     * as in "myParam!=myValue". "myParam" style expressions are also supported,
     * with such parameters having to be present in the request (allowed to have
     * any value). Finally, "!myParam" style expressions indicate that the
     * specified parameter is <i>not</i> supposed to be present in the request.
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings inherit
     * this parameter restriction (i.e. the type-level restriction
     * gets checked before the handler method is even resolved).
     * <p>In a Servlet environment, parameter mappings are considered as restrictions
     * that are enforced at the type level. The primary path mapping (i.e. the
     * specified URI value) still has to uniquely identify the target handler, with
     * parameter mappings simply expressing preconditions for invoking the handler.
     * <p>In a Portlet environment, parameters are taken into account as mapping
     * differentiators, i.e. the primary portlet mode mapping plus the parameter
     * conditions uniquely identify the target handler. Different handlers may be
     * mapped onto the same portlet mode, as long as their parameter mappings differ.
     */
    String[] params() default {};

    /**
     * The headers of the mapped request, narrowing the primary mapping.
     * <p>Same format for any environment: a sequence of "My-Header=myValue" style
     * expressions, with a request only mapped if each such header is found
     * to have the given value. Expressions can be negated by using the "!=" operator,
     * as in "My-Header!=myValue". "My-Header" style expressions are also supported,
     * with such headers having to be present in the request (allowed to have
     * any value). Finally, "!My-Header" style expressions indicate that the
     * specified header is <i>not</i> supposed to be present in the request.
     * <p>Also supports media type wildcards (*), for headers such as Accept
     * and Content-Type. For instance,
     * <pre class="code">
     * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
     * </pre>
     * will match requests with a Content-Type of "text/html", "text/plain", etc.
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings inherit
     * this header restriction (i.e. the type-level restriction
     * gets checked before the handler method is even resolved).
     * <p>Maps against HttpServletRequest headers in a Servlet environment,
     * and against PortletRequest properties in a Portlet 2.0 environment.
     */
    String[] headers() default {};

    /**
     * The consumable media types of the mapped request, narrowing the primary mapping.
     * <p>The format is a single media type or a sequence of media types,
     * with a request only mapped if the {@code Content-Type} matches one of these media types.
     * Examples:
     * <pre class="code">
     * consumes = "text/plain"
     * consumes = {"text/plain", "application/*"}
     * </pre>
     * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
     * all requests with a {@code Content-Type} other than "text/plain".
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings override
     * this consumes restriction.
     * @see javax.servlet.http.HttpServletRequest#getContentType()
     */
    String[] consumes() default {};

    /**
     * The producible media types of the mapped request, narrowing the primary mapping.
     * <p>The format is a single media type or a sequence of media types,
     * with a request only mapped if the {@code Accept} matches one of these media types.
     * Examples:
     * <pre class="code">
     * produces = "text/plain"
     * produces = {"text/plain", "application/*"}
     * </pre>
     * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
     * all requests with a {@code Accept} other than "text/plain".
     * <p><b>Supported at the type level as well as at the method level!</b>
     * When used at the type level, all method-level mappings override
     * this produces restriction.
     */
    String[] produces() default {};
}
