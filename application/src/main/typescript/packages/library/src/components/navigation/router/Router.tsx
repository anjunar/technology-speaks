import "./Router.css"
import React, {FunctionComponent, useContext, useEffect, useRef, useState} from "react"
import {SystemContext, SystemContextHolder} from "../../../System"
import {match, MatchFunction} from "path-to-regexp"
import Route = Router.Route;
import QueryParams = Router.QueryParams;
import PathParams = Router.PathParams;
import {useHydrated} from "../../../hooks";

const scrollAreaCache = new Map<string, number>()

export async function resolveComponentList(
    path: string,
    search: string,
    routes: Route[],
    host : string
): Promise<React.ReactElement[]> {
    const queryParams = resolveQueryParameters(search)

    async function walk(routes: Route[]): Promise<React.ReactElement[]> {
        const flattened = flattenRoutes(routes)

        for (const [rawPath, route] of flattened) {
            let pathParams : PathParams
            if (route.subRouter && path.startsWith(rawPath)) {
                pathParams = {}
            } else {
                const matcher = match(rawPath, { decode: decodeURIComponent })
                const matched = matcher(path)
                if (!matched) continue
                pathParams = matched.params as PathParams
            }

            let component = route.component ?? route.dynamic?.(pathParams, queryParams)
            if (!component) return []

            const props: any = { pathParams, queryParams }

            if (route.loader) {
                const loaderEntries = Object.entries(route.loader)
                const loaded = await Promise.all(
                    loaderEntries.map(([_, fn]) => fn(host + rawPath, pathParams, queryParams))
                )
                loaderEntries.forEach(([key], i) => {
                    props[key] = loaded[i]
                })
            }

            const currentElement = React.createElement(component as FunctionComponent<any>, props)

            const childElements = route.children
                ? await walk(route.children)
                : []

            return [currentElement, ...childElements]
        }

        return []
    }

    return walk(routes)
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

export function resolveQueryParameters(search : string): QueryParams {
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

    const {depth, path, search, routes, windows, darkMode, data, host, language, cookies} = useContext(SystemContext)

    const [state, setState] = useState(data[depth])

    const [childRoutes, setChildRoutes] = useState<Route[]>([])

    const scrollArea = useRef<HTMLDivElement>(null)

    const hydrated = useHydrated()

    function processUrlChange() {

        const flattened = flattenRoutes(routes)

        const matchers: [MatchFunction<object>, Route, string][] = flattened.map(([rawPath, route]) => {
            const matcher = match(rawPath, {decode: decodeURIComponent})
            return [matcher, route, rawPath]
        })

        let oldRoute: string = null
        let oldComponent: any = null
        let oldSearch: string = null

        const loadComponent = (callback: (value: any[]) => void) => {
            const pathname = path.replace("//", "/")

            const option = matchers.find(([matcher]) => {
                try {
                    let matcher1 = matcher(pathname);
                    return matcher1
                } catch {
                    return false
                }
            })

            if (!option) {
                const allRoutes = flattened.map(([p]) => p).join('\n')
                throw new Error(`No Route found for: ${pathname}\n Available Routes:\n${allRoutes}`)
            } else {
                const [matcher, route, rawPath] = option

                if (oldRoute === route.path && oldComponent === route.component && (oldSearch === search || route.subRouter)) {
                    oldSearch = search
                    return route.children
                } else {
                    oldRoute = route.path
                    oldComponent = route.component
                    oldSearch = search

                    let pathParameters: PathParams
                    if (route.subRouter && path.startsWith(rawPath)) {
                        pathParameters = {}
                    } else {
                        const matched = matcher(pathname)
                        if (!matched) throw new Error("Matcher sollte ja passen!")

                        pathParameters = matched.params as any
                    }
                    const queryParameters: QueryParams = resolveQueryParameters(search)
                    let component = route.component
                    if (!component) {
                        component = route.dynamic(pathParameters, queryParameters)
                    }

                    if (component) {
                        let loader = route.loader
                        if (loader) {
                            if (onRoute) {
                                onRoute(true)
                            }

                            let loaders = Object.entries(loader).map(([key, value]) => {
                                return {name: key, value: value(host + rawPath, pathParameters, queryParameters)}
                            })

                            Promise.all(loaders.map(l => l.value))
                                .then(response => {
                                    const data = response.reduce((prev, elem, index) => {
                                        prev[loaders[index].name] = elem
                                        return prev
                                    }, {})

                                    setState(
                                        React.createElement(component as FunctionComponent<any>, {
                                            pathParams: pathParameters,
                                            queryParams: queryParameters,
                                            ...data
                                        })
                                    )

                                    if (onRoute) {
                                        onRoute(false)
                                    }
                                    callback(route.children)
                                })
                                .catch(console.error)
                        } else {
                            setState(
                                React.createElement(component as FunctionComponent<any>, {
                                    pathParams: pathParameters,
                                    queryParams: queryParameters
                                })
                            )
                            callback(route.children)
                        }
                    }
                }
            }
        }

        loadComponent(route => setChildRoutes(route))
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
        return new SystemContextHolder(depth + 1, path, search, host, cookies, routes,  windows, darkMode, data, language)
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
        [key: string]: (path : string, pathParams: PathParams, queryParams: QueryParams) => Promise<any>
    }

    export interface Route {
        path: string
        subRouter?: boolean
        component?: FunctionComponent<any> | MultiComponent
        dynamic?: (path: PathParams, query: QueryParams) => FunctionComponent<any> | MultiComponent
        children?: Route[]
        loader?: Loader
    }

    export interface MultiComponent {
        mobile: FunctionComponent<any>
        desktop: FunctionComponent<any>
    }

    export class RedirectError extends Error {
        constructor(public url: string) {
            super()
        }
    }
}

export default Router
