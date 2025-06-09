import "./Router.css"
import React, {FunctionComponent, useContext, useEffect, useRef, useState} from "react"
import {SystemContext, SystemContextHolder} from "../../../System"
import {match} from "path-to-regexp"
import {useHydrated} from "../../../hooks";
import Route = Router.Route;
import QueryParams = Router.QueryParams;
import PathParams = Router.PathParams;

const scrollAreaCache = new Map<string, number>()

const elementCache = new Map<string, React.ReactElement>();
const loaderCache = new Map<string, Record<string, any>>();

export async function resolveComponentList(
    path: string,
    search: string,
    routes: Route[],
    host: string,
    findFirst = false,
    forceReload = false
): Promise<[Route, React.ReactElement][]> {
    const queryParams = resolveQueryParameters(search);

    async function walk(routes: Route[]): Promise<[Route, React.ReactElement][]> {
        const flattened = flattenRoutes(routes);

        for (const [rawPath, route] of flattened) {
            let pathParams: PathParams;

            if (route.subRouter && path.startsWith(rawPath)) {
                pathParams = {};
            } else {
                const matcher = match(rawPath, { decode: decodeURIComponent });
                const matched = matcher(path);
                if (!matched) continue;
                pathParams = matched.params as PathParams;
            }

            const component = route.component ?? (await route.dynamic?.(pathParams, queryParams));
            if (!component) return [];

            const routeKey = rawPath + JSON.stringify(pathParams) + JSON.stringify(queryParams);

            // 💾 Loader-Cache
            let props: Record<string, any> = { pathParams, queryParams };

            if (route.loader) {
                if (!forceReload && loaderCache.has(routeKey) && route.subRouter === true) {
                    props = { ...props, ...loaderCache.get(routeKey)! };
                } else {
                    const loaderEntries = Object.entries(route.loader);
                    const loaded = await Promise.all(
                        loaderEntries.map(([_, fn]) =>
                            fn(host + rawPath, pathParams, queryParams)
                        )
                    );
                    loaderEntries.forEach(([key], i) => {
                        props[key] = loaded[i];
                    });
                    loaderCache.set(routeKey, { ...props });
                }
            }

            // ⚡ Element-Cache
            if (elementCache.has(routeKey) && route.subRouter === true) {
                const element = elementCache.get(routeKey)!;
                const childElements = !findFirst && route.children
                    ? await walk(route.children)
                    : [];
                return [[route, element], ...childElements];
            }

            const element = React.createElement(component as FunctionComponent<any>, props);
            elementCache.set(routeKey, element);

            const childElements = !findFirst && route.children
                ? await walk(route.children)
                : [];

            return [[route, element], ...childElements];
        }

        return [];
    }

    return walk(routes);
}

export function flattenRoutes(routes: Route[], parentPath: string = ''): Router.RouteWithPath[] {
    return routes.reduce((previous: Router.RouteWithPath[], current: Route) => {
        const currentPath = `${parentPath}${current.path}`.replace(/\/\//g, '/')
        const currentRouteWithPath: Router.RouteWithPath = [currentPath, current]
        const childrenRoutes = current.children
            ? flattenRoutes(current.children, currentPath)
            : []
        return [...previous, currentRouteWithPath, ...childrenRoutes]
    }, [])
}

export function resolveQueryParameters(search: string): QueryParams {
    let segments = search
        .slice(1)
        .split("&")
        .filter(str => str.length > 0)
    return segments.reduce((prev: any, current) => {
        let split = current.split("=")
        let element = prev[split[0]]
        if (element) {
            if (element instanceof Array) {
                element.push(split[1])
                return prev
            } else {
                prev[split[0]] = [element, split[1]]
                return prev
            }
        } else {
            prev[split[0]] = split[1]
            return prev
        }
    }, {})
}

function Router(properties: Router.Attributes) {

    const {onRoute, ...rest} = properties

    const {
        depth,
        path,
        search,
        routes,
        windows,
        darkMode,
        data,
        host,
        language,
        cookies,
        theme
    } = useContext(SystemContext)

    const hydrated = useHydrated()

    const [state, setState] = useState<React.ReactElement<any>>(() => {
        return data[depth][1]
    })

    const [childRoutes, setChildRoutes] = useState<Route[]>(data[depth][0].children)

    const scrollArea = useRef<HTMLDivElement>(null)

    async function processUrlChange() {
        if (onRoute) onRoute(true);

        try {
            const [components] = await resolveComponentList(path, search, routes, host, true);
            const [route, component] = components

            setState(component);

            setChildRoutes(route.children);
        } catch (error) {
            console.error("Fehler beim Laden der Komponenten", error);
            setState(null);
            setChildRoutes([]);
        }

        if (onRoute) onRoute(false);
    }

    useEffect(() => {
        if (hydrated) {
            processUrlChange()
        }
    }, [path, search])

    useEffect(() => {
        let scrollTop = scrollAreaCache.get(window.location.href)
        if (scrollTop) {
            scrollArea.current.scrollTop = scrollTop
        }
    }, [state])

    useEffect(() => {
        let listener = () => {
            scrollAreaCache.set(window.location.href, scrollArea.current.scrollTop)
        }

        scrollArea.current.addEventListener("scroll", listener)

        return () => {
            scrollArea.current?.removeEventListener("scroll", listener)
        }
    }, [])

    function getContextHolder() {
        return new SystemContextHolder(depth + 1, path, search, host, cookies, childRoutes, windows, darkMode, data, language, theme)
    }

    return (
        <div ref={scrollArea} className={"router"} {...rest}>
            <SystemContext.Provider value={getContextHolder()}>
                {state}
            </SystemContext.Provider>
        </div>
    )
}

namespace Router {
    export type RouteWithPath = [string, Route]

    export interface Attributes {
        onRoute?: (loading: boolean) => void
    }

    export function navigate(url: string, data?: any) {
        try {
            const path = new URL("http://" + url, window.location.origin);
            window.history.pushState(data, "", path)
            window.dispatchEvent(new PopStateEvent("popstate", {state: data}))
        } catch (e) {
            window.history.pushState(data, "", url)
            window.dispatchEvent(new PopStateEvent("popstate", {state: data}))
        }
    }

    export interface PathParams {
        [key: string]: string
    }

    export interface QueryParams {
        [key: string]: string | string[]
    }

    export interface Loader {
        [key: string]: (path: string, pathParams: PathParams, queryParams: QueryParams) => Promise<any>
    }

    export interface Route {
        path: string
        subRouter?: boolean
        component?: FunctionComponent<any>
        dynamic?: (path: PathParams, query: QueryParams) => FunctionComponent<any>
        children?: Route[]
        loader?: Loader
    }

    export class RedirectError extends Error {
        constructor(public url: string) {
            super()
        }
    }
}

export default Router
