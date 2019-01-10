/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.redant.core.router;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Router that contains information about both route matching orders and
 * HTTP request methods.
 *
 * <p>Routes are devided into 3 sections: "first", "last", and "other".
 * Routes in "first" are matched first, then in "other", then in "last".
 *
 * <h3>Create router</h3>
 *
 * <p>Route targets can be any type. In the below example, targets are classes:
 *
 * <pre>
 * {@code
 * Router<Class> router = new Router<Class>()
 *   .GET      ("/articles",     IndexHandler.class)
 *   .GET      ("/articles/:id", ShowHandler.class)
 *   .POST     ("/articles",     CreateHandler.class)
 *   .GET      ("/download/:*",  DownloadHandler.class)  // ":*" must be the last token
 *   .GET_FIRST("/articles/new", NewHandler.class);      // This will be matched first
 * }
 * </pre>
 *
 * <p>Slashes at both ends are ignored. These are the same:
 *
 * <pre>
 * {@code
 * router.GET("articles",   IndexHandler.class);
 * router.GET("/articles",  IndexHandler.class);
 * router.GET("/articles/", IndexHandler.class);
 * }
 * </pre>
 *
 * <p>You can remove routes by target or by path pattern:
 *
 * <pre>
 * {@code
 * router.removeTarget(IndexHandler.class);
 * router.removePathPattern("/articles");
 * }
 * </pre>
 *
 * <h3>Match with request method and URI</h3>
 *
 * <p>Use {@link #route(HttpMethod, String)}.
 *
 * <p>From the {@link RouteResult} you can extract params embedded in
 * the path and from the query part of the request URI.
 *
 * <h3>404 Not Found target</h3>
 *
 * <p>Use {@link #notFound(Object)}. It will be used as the target
 * when there's no match.
 *
 * <pre>
 * {@code
 * router.notFound(My404Handler.class);
 * }
 * </pre>
 *
 * <h3>Create reverse route</h3>
 *
 * <p>Use {@code #uri}:
 *
 * <pre>
 * {@code
 * router.uri(HttpMethod.GET, IndexHandler.class);
 * // Returns "/articles"
 * }
 * </pre>
 *
 * <p>You can skip HTTP method if there's no confusion:
 *
 * <pre>
 * {@code
 * router.uri(CreateHandler.class);
 * // Also returns "/articles"
 * }
 * </pre>
 *
 * <p>You can specify params as map:
 *
 * <pre>
 * {@code
 * // Things in params will be converted to String
 * Map<Object, Object> params = new HashMap<Object, Object>();
 * params.put("id", 123);
 * router.uri(ShowHandler.class, params);
 * // Returns "/articles/123"
 * }
 * </pre>
 *
 * <p>Convenient way to specify params:
 *
 * <pre>
 * {@code
 * router.uri(ShowHandler.class, "id", 123);
 * // Returns "/articles/123"
 * }
 * </pre>
 *
 * <h3>Allowed methods</h3>
 *
 * <p>If you want to implement
 * <a href="https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol#Request_methods">OPTIONS</a>
 * or
 * <a href="https://en.wikipedia.org/wiki/Cross-origin_resource_sharing">CORS</a>,
 * you can use {@link #allowedMethods(String)}.
 *
 * <p>For {@code OPTIONS *}, use {@link #allAllowedMethods()}.
 */
public class Router<T> {
    private final Map<HttpMethod, MethodlessRouter<T>> routers =
            new HashMap<HttpMethod, MethodlessRouter<T>>();

    private final MethodlessRouter<T> anyMethodRouter =
            new MethodlessRouter<T>();

    private T notFound;

    //--------------------------------------------------------------------------
    // Design decision:
    // We do not allow access to routers and anyMethodRouter, because we don't
    // want to expose MethodlessRouter, OrderlessRouter, and PathPattern.
    // Exposing those will complicate the use of this package.

    /**
     * Returns the fallback target for use when there's no match at
     * {@link #route(HttpMethod, String)}.
     */
    public T notFound() {
        return notFound;
    }

    /**
     * Returns the number of routes in this router.
     */
    public int size() {
        int ret = anyMethodRouter.size();

        for (MethodlessRouter<T> router : routers.values()) {
            ret += router.size();
        }

        return ret;
    }

    //--------------------------------------------------------------------------

    /**
     * Add route to the "first" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public Router<T> addRouteFirst(HttpMethod method, String pathPattern, T target) {
        getMethodlessRouter(method).addRouteFirst(pathPattern, target);
        return this;
    }

    /**
     * Add route to the "other" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public Router<T> addRoute(HttpMethod method, String pathPattern, T target) {
        getMethodlessRouter(method).addRoute(pathPattern, target);
        return this;
    }

    /**
     * Add route to the "last" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public Router<T> addRouteLast(HttpMethod method, String pathPattern, T target) {
        getMethodlessRouter(method).addRouteLast(pathPattern, target);
        return this;
    }

    /**
     * Sets the fallback target for use when there's no match at
     * {@link #route(HttpMethod, String)}.
     */
    public Router<T> notFound(T target) {
        this.notFound = target;
        return this;
    }

    private MethodlessRouter<T> getMethodlessRouter(HttpMethod method) {
        if (method == null) {
            return anyMethodRouter;
        }

        MethodlessRouter<T> r = routers.get(method);
        if (r == null) {
            r = new MethodlessRouter<T>();
            routers.put(method, r);
        }

        return r;
    }

    //--------------------------------------------------------------------------

    /**
     * Removes the route specified by the path pattern.
     */
    public void removePathPattern(String pathPattern) {
        for (MethodlessRouter<T> r : routers.values()) {
            r.removePathPattern(pathPattern);
        }
        anyMethodRouter.removePathPattern(pathPattern);
    }

    /**
     * Removes all routes leading to the target.
     */
    public void removeTarget(T target) {
        for (MethodlessRouter<T> r : routers.values()) {
            r.removeTarget(target);
        }
        anyMethodRouter.removeTarget(target);
    }

    //--------------------------------------------------------------------------

    /**
     * If there's no match, returns the result with {@link #notFound(Object) notFound}
     * as the target if it is set, otherwise returns {@code null}.
     */
    public RouteResult<T> route(HttpMethod method, String uri) {
        MethodlessRouter<T> router = routers.get(method);
        if (router == null) {
            router = anyMethodRouter;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String[] tokens = decodePathTokens(uri);

        RouteResult<T> ret = router.route(uri, decoder.path(), tokens);
        if (ret != null) {
            return new RouteResult<T>(uri, decoder.path(), ret.pathParams(), decoder.parameters(), ret.target());
        }

        if (router != anyMethodRouter) {
            ret = anyMethodRouter.route(uri, decoder.path(), tokens);
            if (ret != null) {
                return new RouteResult<T>(uri, decoder.path(), ret.pathParams(), decoder.parameters(), ret.target());
            }
        }

        if (notFound != null) {
            return new RouteResult<T>(uri, decoder.path(), Collections.<String, String>emptyMap(), decoder.parameters(), notFound);
        }

        return null;
    }

    private String[] decodePathTokens(String uri) {
        // Need to split the original URI (instead of QueryStringDecoder#path) then decode the tokens (components),
        // otherwise /test1/123%2F456 will not match /test1/:p1

        int qPos = uri.indexOf("?");
        String encodedPath = (qPos >= 0) ? uri.substring(0, qPos) : uri;

        String[] encodedTokens = PathPattern.removeSlashesAtBothEnds(encodedPath).split("/");

        String[] decodedTokens = new String[encodedTokens.length];
        for (int i = 0; i < encodedTokens.length; i++) {
            String encodedToken = encodedTokens[i];
            decodedTokens[i] = QueryStringDecoder.decodeComponent(encodedToken);
        }

        return decodedTokens;
    }

    //--------------------------------------------------------------------------
    // For implementing OPTIONS and CORS.

    /**
     * Returns allowed methods for a specific URI.
     * <p>
     * For {@code OPTIONS *}, use {@link #allAllowedMethods()} instead of this method.
     */
    public Set<HttpMethod> allowedMethods(String uri) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String[] tokens = PathPattern.removeSlashesAtBothEnds(decoder.path()).split("/");

        if (anyMethodRouter.anyMatched(tokens)) {
            return allAllowedMethods();
        }

        Set<HttpMethod> ret = new HashSet<HttpMethod>(routers.size());
        for (Entry<HttpMethod, MethodlessRouter<T>> entry : routers.entrySet()) {
            MethodlessRouter<T> router = entry.getValue();
            if (router.anyMatched(tokens)) {
                HttpMethod method = entry.getKey();
                ret.add(method);
            }
        }

        return ret;
    }

    /**
     * Returns all methods that this router handles. For {@code OPTIONS *}.
     */
    public Set<HttpMethod> allAllowedMethods() {
        if (anyMethodRouter.size() > 0) {
            Set<HttpMethod> ret = new HashSet<HttpMethod>(9);
            ret.add(HttpMethod.CONNECT);
            ret.add(HttpMethod.DELETE);
            ret.add(HttpMethod.GET);
            ret.add(HttpMethod.HEAD);
            ret.add(HttpMethod.OPTIONS);
            ret.add(HttpMethod.PATCH);
            ret.add(HttpMethod.POST);
            ret.add(HttpMethod.PUT);
            ret.add(HttpMethod.TRACE);
            return ret;
        } else {
            return new HashSet<HttpMethod>(routers.keySet());
        }
    }

    //--------------------------------------------------------------------------
    // Reverse routing.

    /**
     * Given a target and params, this method tries to do the reverse routing
     * and returns the URI.
     *
     * <p>Placeholders in the path pattern will be filled with the params.
     * The params can be a map of {@code placeholder name -> value}
     * or ordered values.
     *
     * <p>If a param doesn't have a corresponding placeholder, it will be put
     * to the query part of the result URI.
     *
     * @return {@code null} if there's no match
     */
    public String uri(HttpMethod method, T target, Object... params) {
        MethodlessRouter<T> router = (method == null) ? anyMethodRouter : routers.get(method);

        // Fallback to anyMethodRouter if no router is found for the method
        if (router == null) {
            router = anyMethodRouter;
        }

        String ret = router.uri(target, params);
        if (ret != null) {
            return ret;
        }

        // Fallback to anyMethodRouter if the router was not anyMethodRouter and no path is found
        return (router != anyMethodRouter) ? anyMethodRouter.uri(target, params) : null;
    }

    /**
     * Given a target and params, this method tries to do the reverse routing
     * and returns the URI.
     *
     * <p>Placeholders in the path pattern will be filled with the params.
     * The params can be a map of {@code placeholder name -> value}
     * or ordered values.
     *
     * <p>If a param doesn't have a corresponding placeholder, it will be put
     * to the query part of the result URI.
     *
     * @return {@code null} if there's no match
     */
    public String uri(T target, Object... params) {
        Collection<MethodlessRouter<T>> rs = routers.values();
        for (MethodlessRouter<T> r : rs) {
            String ret = r.uri(target, params);
            if (ret != null) {
                return ret;
            }
        }
        return anyMethodRouter.uri(target, params);
    }

    //--------------------------------------------------------------------------

    /**
     * Returns visualized routing rules.
     */
    @Override
    public String toString() {
        // Step 1/2: Dump routers and anyMethodRouter in order
        int numRoutes = size();
        List<String> methods = new ArrayList<String>(numRoutes);
        List<String> patterns = new ArrayList<String>(numRoutes);
        List<String> targets = new ArrayList<String>(numRoutes);

        // For router
        for (Entry<HttpMethod, MethodlessRouter<T>> e : routers.entrySet()) {
            HttpMethod method = e.getKey();
            MethodlessRouter<T> router = e.getValue();
            aggregateRoutes(method.toString(), router.first().routes(), methods, patterns, targets);
            aggregateRoutes(method.toString(), router.other().routes(), methods, patterns, targets);
            aggregateRoutes(method.toString(), router.last().routes(), methods, patterns, targets);
        }

        // For anyMethodRouter
        aggregateRoutes("*", anyMethodRouter.first().routes(), methods, patterns, targets);
        aggregateRoutes("*", anyMethodRouter.other().routes(), methods, patterns, targets);
        aggregateRoutes("*", anyMethodRouter.last().routes(), methods, patterns, targets);

        // For notFound
        if (notFound != null) {
            methods.add("*");
            patterns.add("*");
            targets.add(targetToString(notFound));
        }

        // Step 2/2: Format the List into aligned columns: <method> <patterns> <target>
        int maxLengthMethod = maxLength(methods);
        int maxLengthPattern = maxLength(patterns);
        String format = "%-" + maxLengthMethod + "s  %-" + maxLengthPattern + "s  %s\n";
        int initialCapacity = (maxLengthMethod + 1 + maxLengthPattern + 1 + 20) * methods.size();
        StringBuilder b = new StringBuilder(initialCapacity);
        for (int i = 0; i < methods.size(); i++) {
            String method = methods.get(i);
            String pattern = patterns.get(i);
            String target = targets.get(i);
            b.append(String.format(format, method, pattern, target));
        }
        return b.toString();
    }

    /**
     * Helper for toString.
     */
    private static <T> void aggregateRoutes(
            String method, Map<PathPattern, T> routes,
            List<String> accMethods, List<String> accPatterns, List<String> accTargets) {
        for (Entry<PathPattern, T> entry : routes.entrySet()) {
            accMethods.add(method);
            accPatterns.add("/" + entry.getKey().pattern());
            accTargets.add(targetToString(entry.getValue()));
        }
    }

    /**
     * Helper for toString.
     */
    private static int maxLength(List<String> coll) {
        int max = 0;
        for (String e : coll) {
            int length = e.length();
            if (length > max) {
                max = length;
            }
        }
        return max;
    }

    /**
     * Helper for toString.
     *
     * <p>For example, returns
     * "io.server.example.http.router.HttpRouterServerHandler" instead of
     * "class io.server.example.http.router.HttpRouterServerHandler"
     */
    private static String targetToString(Object target) {
        if (target instanceof Class) {
            return ((Class<?>) target).getName();
        } else {
            return target.toString();
        }
    }

    //--------------------------------------------------------------------------

    public Router<T> CONNECT(String path, T target) {
        return addRoute(HttpMethod.CONNECT, path, target);
    }

    public Router<T> DELETE(String path, T target) {
        return addRoute(HttpMethod.DELETE, path, target);
    }

    public Router<T> GET(String path, T target) {
        return addRoute(HttpMethod.GET, path, target);
    }

    public Router<T> HEAD(String path, T target) {
        return addRoute(HttpMethod.HEAD, path, target);
    }

    public Router<T> OPTIONS(String path, T target) {
        return addRoute(HttpMethod.OPTIONS, path, target);
    }

    public Router<T> PATCH(String path, T target) {
        return addRoute(HttpMethod.PATCH, path, target);
    }

    public Router<T> POST(String path, T target) {
        return addRoute(HttpMethod.POST, path, target);
    }

    public Router<T> PUT(String path, T target) {
        return addRoute(HttpMethod.PUT, path, target);
    }

    public Router<T> TRACE(String path, T target) {
        return addRoute(HttpMethod.TRACE, path, target);
    }

    public Router<T> ANY(String path, T target) {
        return addRoute(null, path, target);
    }

    //--------------------------------------------------------------------------

    public Router<T> CONNECT_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.CONNECT, path, target);
    }

    public Router<T> DELETE_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.DELETE, path, target);
    }

    public Router<T> GET_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.GET, path, target);
    }

    public Router<T> HEAD_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.HEAD, path, target);
    }

    public Router<T> OPTIONS_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.OPTIONS, path, target);
    }

    public Router<T> PATCH_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.PATCH, path, target);
    }

    public Router<T> POST_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.POST, path, target);
    }

    public Router<T> PUT_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.PUT, path, target);
    }

    public Router<T> TRACE_FIRST(String path, T target) {
        return addRouteFirst(HttpMethod.TRACE, path, target);
    }

    public Router<T> ANY_FIRST(String path, T target) {
        return addRouteFirst(null, path, target);
    }

    //--------------------------------------------------------------------------

    public Router<T> CONNECT_LAST(String path, T target) {
        return addRouteLast(HttpMethod.CONNECT, path, target);
    }

    public Router<T> DELETE_LAST(String path, T target) {
        return addRouteLast(HttpMethod.DELETE, path, target);
    }

    public Router<T> GET_LAST(String path, T target) {
        return addRouteLast(HttpMethod.GET, path, target);
    }

    public Router<T> HEAD_LAST(String path, T target) {
        return addRouteLast(HttpMethod.HEAD, path, target);
    }

    public Router<T> OPTIONS_LAST(String path, T target) {
        return addRouteLast(HttpMethod.OPTIONS, path, target);
    }

    public Router<T> PATCH_LAST(String path, T target) {
        return addRouteLast(HttpMethod.PATCH, path, target);
    }

    public Router<T> POST_LAST(String path, T target) {
        return addRouteLast(HttpMethod.POST, path, target);
    }

    public Router<T> PUT_LAST(String path, T target) {
        return addRouteLast(HttpMethod.PUT, path, target);
    }

    public Router<T> TRACE_LAST(String path, T target) {
        return addRouteLast(HttpMethod.TRACE, path, target);
    }

    public Router<T> ANY_LAST(String path, T target) {
        return addRouteLast(null, path, target);
    }
}
