import "./Router.css"
import React, {FunctionComponent, useContext, useEffect, useRef, useState} from "react"
import {SystemContext, SystemContextHolder} from "../../../System"
import {match, MatchFunction} from "path-to-regexp"
import Route = Router.Route;
import QueryParams = Router.QueryParams;
import PathParams = Router.PathParams;

const scrollAreaCache = new Map<string, number>()

function Router(properties: Router.Attributes) {

    const {onRoute, ...rest} = properties

    const {path, search, routes, windows, darkMode, data} = useContext(SystemContext)

    const [state, setState] = useState(data)

    const [childRoutes, setChildRoutes] = useState<Route[]>([])

    const scrollArea = useRef<HTMLDivElement>(null)

    function processUrlChange() {
        function flattenRoutes(routes: Route[], parentPath: string = ''): Router.RouteWithPath[] {
            return routes.reduce((previous: Router.RouteWithPath[], current: Route) => {
                // Pfad zusammenbauen, Doppel-Slashes entfernen
                const currentPath = `${parentPath}${current.path}`.replace(/\/\//g, '/')
                const currentRouteWithPath: Router.RouteWithPath = [currentPath, current]
                const childrenRoutes = current.children
                    ? flattenRoutes(current.children, currentPath)
                    : []
                return [...previous, currentRouteWithPath, ...childrenRoutes]
            }, [])
        }

        const flattened = flattenRoutes(routes)

        // Hilfsfunktion: Ersetze {param} durch :param für path-to-regexp
        function normalizePath(path: string): string {
            return path.replace(/\{(\w+)\}/g, ':$1')
        }

        const matchers: [MatchFunction<object>, Route, string][] = flattened.map(([rawPath, route]) => {
            const matcher = match(rawPath, {decode: decodeURIComponent})
            return [matcher, route, rawPath]
        })

        // Query Parameter parsen
        const resolveQueryParameters = (): QueryParams => {
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

        let oldRoute: string = null
        let oldComponent: any = null
        let oldSearch: string = null

        const loadComponent = (callback: (value: any[]) => void) => {
            const pathname = path.replace("//", "/")

            // Suche nach erstem matcher, der passt
            const option = matchers.find(([matcher]) => {
                try {
                    return matcher(pathname) !== false
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

                    const matched = matcher(pathname)
                    if (!matched) throw new Error("Matcher sollte ja passen!")

                    const pathParameters: PathParams = matched.params as any
                    const queryParameters: QueryParams = resolveQueryParameters()
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
                                return {name: key, value: value(pathParameters, queryParameters)}
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
        processUrlChange()
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
        return new SystemContextHolder(path, search, childRoutes, windows, darkMode)
    }

    if (typeof window === "undefined") {
        processUrlChange()
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
        window.history.pushState(data, "", url)
        window.dispatchEvent(new PopStateEvent("popstate", {state: data}))
    }

    export interface PathParams {
        [key: string]: string
    }

    export interface QueryParams {
        [key: string]: string | string[]
    }

    export interface Loader {
        [key: string]: (path: PathParams, query: QueryParams) => Promise<any>
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
}

export default Router
