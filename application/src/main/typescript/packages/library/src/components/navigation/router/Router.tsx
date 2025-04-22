import "./Router.css"
import React, {Dispatch, FunctionComponent, SetStateAction, useContext, useEffect, useLayoutEffect, useRef, useState} from "react"
import {SystemContext, SystemContextHolder, WindowRef} from "../../../System";
import {v4} from "uuid";
import Route = Router.Route;
import QueryParams = Router.QueryParams;
import PathParams = Router.PathParams;

const scrollAreaCache = new Map<string, number>()

function Router(properties: Router.Attributes) {

    const {onRoute, ...rest} = properties

    const [state, setState] = useState(<div></div>)

    let systemContextHolder = useContext(SystemContext);

    const {routes, windows}: { routes: Route[], windows : [WindowRef[], Dispatch<SetStateAction<WindowRef[]>>] } = systemContextHolder

    const [childRoutes, setChildRoutes] = useState([])

    const scrollArea = useRef<HTMLDivElement>(null);

    useLayoutEffect(() => {
        type RouteWithPath = [string, Route];

        function flattenRoutes(routes: Route[], parentPath: string = ''): RouteWithPath[] {
            return routes.reduce((previous: RouteWithPath[], current: Route) => {
                const currentPath = `${parentPath}${current.path}`.replace(/\/\//g, '/');
                const currentRouteWithPath: RouteWithPath = [currentPath, current];
                const childrenRoutes = current.children
                    ? flattenRoutes(current.children, currentPath)
                    : [];
                return [...previous, currentRouteWithPath, ...childrenRoutes];

            }, []);
        }

        const flattened = flattenRoutes(routes);
        const regexRoutes: [RegExp, Route][] = flattened.map(([path, route]) => (
            [
                route.subRouter ? new RegExp("^" + path.replace(/\{\w+}/g, '([\\w\\d-]+)') + ".*$") : new RegExp("^" + path.replace(/\{\w+}/g, '([\\w\\d-]+)') + "$"),
                route
            ]
        ));

        const resolveQueryParameters = (): QueryParams => {
            let segments = window.location.search
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

        function resolvePathParams(regex: RegExp, pathname: string, option: [RegExp, Route]) {
            const matcher = regex.exec(pathname);
            const pathParams = {} as any

            if (matcher) {
                for (let index = 1; index < matcher.length; index++) {
                    const value = matcher[index];
                    const pathSegments = pathname.split('/');
                    const indexOfSegment = pathSegments.indexOf(value);
                    const indexOfRoute = regexRoutes.indexOf(option);
                    const [actualRoute, factory] = flattened[indexOfRoute];
                    const actualRouteSegment = actualRoute.split('/').slice(1);
                    const key = actualRouteSegment[indexOfSegment - 1];
                    let slice = key.slice(1, key.length - 1);
                    pathParams[slice] = value
                }
            }
            return pathParams;
        }

        let oldRoute : string = null
        let oldComponent : any = null
        let oldSearch : string = null

        const loadComponent = (state? : any) => {

            const baseUrl = process.env.PUBLIC_URL

            const pathname = window.location.pathname.replace(baseUrl, "/").replace("//", "/");
            const search = window.location.search;

            const option = regexRoutes.find(([regex, route]) => regex.test(pathname));

            if (!option) {
                const allRoutes = flattened.map(([path]) => path).join('\n');
                throw new Error(`No Route found for: ${pathname}\n Available Routes:\n${allRoutes}`);
            } else {
                const [regex, route] = option;

                if (oldRoute === route.path && oldComponent === route.component && oldSearch === search && ! state) {
                    oldSearch = search
                    return route.children
                } else {
                    oldRoute = route.path
                    oldComponent = route.component
                    oldSearch = search

                    const queryParams = new Map<string, string>();
                    const querySegments = window.location.search.split('&').slice(1);
                    querySegments.forEach(segment => {
                        const [key, value] = segment.split('=');
                        queryParams.set(key, value);
                    });

                    let pathParameters: PathParams = resolvePathParams(regex, pathname, option);
                    let queryParameters: QueryParams = resolveQueryParameters()
                    let component = route.component

                    if (component) {
                        let loader = route.loader
                        if (loader) {

                            if (onRoute) {
                                onRoute(true)
                            }

                            let loaders = Object.entries(loader).map(([key, value]: [key: string, value: any]) => {
                                return {name: key, value: value(pathParameters, queryParameters)}
                            })
                            Promise.all(loaders.map(loader => loader.value))
                                .then(response => {
                                    const data = response.reduce((prev, elem, index) => {
                                        prev[loaders[index].name] = elem
                                        return prev
                                    }, {})


                                    setState(
                                        React.createElement(component, {
                                            pathParams: pathParameters,
                                            queryParams: queryParameters,
                                            key : v4(),
                                            ...data
                                        })
                                    )

                                    if (onRoute) {
                                        onRoute(false)
                                    }
                                })
                                .catch((response) => {
                                    console.error(response)
                                })
                        } else {
                            setState(
                                React.createElement(component, {
                                    pathParams: pathParameters,
                                    queryParams: queryParameters
                                })
                            )
                        }
                    }

                    return route.children
                }

            }

        }

        setChildRoutes(loadComponent())

        const handlePopstate = (event : PopStateEvent) => {
            setChildRoutes(loadComponent(event.state))
        }

        scrollArea.current.addEventListener("scroll", () => {
            scrollAreaCache.set(window.location.href, scrollArea.current.scrollTop)
        })

        window.addEventListener("popstate", handlePopstate)

        return () => {
            window.removeEventListener("popstate", handlePopstate)
        }
    }, [routes])

    useEffect(() => {
        let scrollTop = scrollAreaCache.get(window.location.href);
        if (scrollTop) {
            scrollArea.current.scrollTop = scrollTop
        }
    }, [state]);

    function getContextHolder() {
        return new SystemContextHolder(childRoutes, windows, systemContextHolder.darkMode);
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
    export interface Attributes {
        onRoute?: (loading: boolean) => void
    }

    export function navigate(url: string, data?: any) {
        window.history.pushState(data, "", url)
        window.dispatchEvent(new PopStateEvent("popstate", {state : data}))
    }

    export interface PathParams {
        [key : string] : string
    }

    export interface QueryParams {
        [key : string] : string
    }

    export interface Loader {
        [key : string] : (path : PathParams, query : QueryParams) => Promise<any>
    }

    export interface Route {
        path  : string
        subRouter? : boolean
        component? : FunctionComponent<any>
        children? : Route[]
        loader? : Loader
    }

}

export default Router

