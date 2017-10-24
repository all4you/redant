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

/**
 * Router that contains information about route matching orders, but doesn't
 * contain information about HTTP request methods.
 *
 * <p>Routes are devided into 3 sections: "first", "last", and "other".
 * Routes in "first" are matched first, then in "other", then in "last".
 */
final class MethodlessRouter<T> {
    private final OrderlessRouter<T> first = new OrderlessRouter<T>();
    private final OrderlessRouter<T> other = new OrderlessRouter<T>();
    private final OrderlessRouter<T> last = new OrderlessRouter<T>();

    //--------------------------------------------------------------------------

    /**
     * Returns the "first" router; routes in this router will be matched first.
     */
    public OrderlessRouter<T> first() {
        return first;
    }

    /**
     * Returns the "other" router; routes in this router will be matched after
     * those in the "first" router, but before those in the "last" router.
     */
    public OrderlessRouter<T> other() {
        return other;
    }

    /**
     * Returns the "last" router; routes in this router will be matched last.
     */
    public OrderlessRouter<T> last() {
        return last;
    }

    /**
     * Returns the number of routes in this router.
     */
    public int size() {
        return first.routes().size() + other.routes().size() + last.routes().size();
    }

    //--------------------------------------------------------------------------

    /**
     * Adds route to the "first" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public MethodlessRouter<T> addRouteFirst(String pathPattern, T target) {
        first.addRoute(pathPattern, target);
        return this;
    }

    /**
     * Adds route to the "other" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public MethodlessRouter<T> addRoute(String pathPattern, T target) {
        other.addRoute(pathPattern, target);
        return this;
    }

    /**
     * Adds route to the "last" section.
     *
     * <p>A path pattern can only point to one target. This method does nothing if the pattern
     * has already been added.
     */
    public MethodlessRouter<T> addRouteLast(String pathPattern, T target) {
        last.addRoute(pathPattern, target);
        return this;
    }

    //--------------------------------------------------------------------------

    /**
     * Removes the route specified by the path pattern.
     */
    public void removePathPattern(String pathPattern) {
        first.removePathPattern(pathPattern);
        other.removePathPattern(pathPattern);
        last.removePathPattern(pathPattern);
    }

    /**
     * Removes all routes leading to the target.
     */
    public void removeTarget(T target) {
        first.removeTarget(target);
        other.removeTarget(target);
        last.removeTarget(target);
    }

    //--------------------------------------------------------------------------

    /**
     * @return {@code null} if no match
     */
    public RouteResult<T> route(String uri, String decodedPath, String[] pathTokens) {
        RouteResult<T> ret = first.route(uri, decodedPath, pathTokens);
        if (ret != null) {
            return ret;
        }

        ret = other.route(uri, decodedPath, pathTokens);
        if (ret != null) {
            return ret;
        }

        ret = last.route(uri, decodedPath, pathTokens);
        if (ret != null) {
            return ret;
        }

        return null;
    }

    /**
     * Checks if there's any matching route.
     */
    public boolean anyMatched(String[] requestPathTokens) {
        return first.anyMatched(requestPathTokens) ||
                other.anyMatched(requestPathTokens) ||
                last.anyMatched(requestPathTokens);
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
        String ret = first.uri(target, params);
        if (ret != null) {
            return ret;
        }

        ret = other.uri(target, params);
        if (ret != null) {
            return ret;
        }

        return last.uri(target, params);
    }
}
