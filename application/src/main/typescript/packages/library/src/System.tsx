import "./System.css"
import React, {createContext, Dispatch, SetStateAction, useLayoutEffect, useState} from "react";
import {init} from "./domain/Persistence";
import Router from "./components/navigation/router/Router";
import Input from "./components/inputs/input/Input";
import ToolBar from "./components/layout/toolbar/ToolBar";
import Progress from "./components/indicators/progress/Progress";
import Cookies from 'js-cookie';

init()

export class WindowRef {

    name: string

    constructor(name: string) {
        this.name = name;
    }
}

export class SystemContextHolder {

    constructor(public depth : number = 0,
                public path : string = "",
                public search : string = "",
                public host : string = "",
                public cookies: string = "",
                public routes: Router.Route[] = [],
                public windows: [WindowRef[], Dispatch<SetStateAction<WindowRef[]>>] = null,
                public darkMode : boolean = false,
                public data : any[] = [],
                public language : string = "en",
                public theme : string = "light") {
    }

}

var patchedFetch = false

export const SystemContext = createContext(new SystemContextHolder())

function System(properties : System.Attributes) {

    const {depth, path, search, routes, data, host, language, cookies, theme} = properties

    const [loading, setLoading] = useState([])

    const [windows, setWindows] = useState<WindowRef[]>([])

    const [darkMode, setDarkMode] = useState(theme === "dark")

    useLayoutEffect(() => {
        const matchMedia = window.matchMedia('(prefers-color-scheme: dark)');

        const handler = (e: MediaQueryListEvent) => {
            const newIsDark = e.matches;
            setDarkMode(newIsDark);
        };

        matchMedia.addEventListener('change', handler);

        return () => {
            matchMedia.removeEventListener('change', handler);
        };
    }, []);

    useLayoutEffect(() => {
        if (darkMode) {
            document.documentElement.setAttribute("data-theme", "dark")
            Cookies.set('theme', 'dark', { expires: 365, path: '/' });
        } else {
            document.documentElement.setAttribute("data-theme", "light")
            Cookies.set('theme', 'light', { expires: 365, path: '/' });
        }
    }, [darkMode]);

    function applyForProxy(argArray: any[], target: (input: (RequestInfo | URL), init?: RequestInit) => Promise<Response>, thisArg: any) {
        setLoading(prev => [...prev, argArray])

        let init = argArray[1] || {}
        argArray[1] = Object.assign(init, {
            credentials: 'include',
            // signal: AbortSignal.timeout(12000),
            headers: Object.assign(init.headers || {}, {'x-language': language, "cookie": cookies})
        })


        let promise = Reflect.apply(target, thisArg, argArray)

        promise
            .then(() => {
                setTimeout(() => {
                    setLoading(prev => {
                        let indexOf = prev.indexOf(argArray)
                        prev.splice(indexOf)
                        return [...prev]
                    })
                }, 1000)
            })
            .catch((response: any) => {
                if (response.name === "TimeoutError") {
                    setLoading([])
                    Router.navigate("/errors/timeout")
                }
            })

        return promise
    }

    if (patchedFetch === false) {
        globalThis.fetch = new Proxy(globalThis.fetch, {
            apply(target: (input: (RequestInfo | URL), init?: RequestInit) => Promise<Response>, thisArg: any, argArray: any[]): any {
                return applyForProxy(argArray, target, thisArg);
            }
        })
        patchedFetch = true
    }


    useLayoutEffect(() => {

        window.fetch = new Proxy(window.fetch, {
            apply(target: (input: (RequestInfo | URL), init?: RequestInit) => Promise<Response>, thisArg: any, argArray: any[]): any {
                return applyForProxy(argArray, target, thisArg);
            }
        })
    }, []);

    return (
        <div className={"system"}>
            <SystemContext.Provider value={new SystemContextHolder(depth, path, search, host,cookies, routes, [windows, setWindows], darkMode, data, language, theme)}>
                <div style={{position: "absolute", zIndex: 9999, top: 0, left: 0, height: "4px", width: "100%"}}>
                    {
                        loading.length > 0 && <Progress/>
                    }
                </div>
                <Router/>
                <ToolBar>
                    <div slot={"left"}>
                        <div style={{display: "flex"}} onClick={(event) => event.stopPropagation()}>
                            {
                                windows.map((window, index) => (
                                    <div key={index}
                                         style={{backgroundColor: "var(--color-background-tertiary)"}}>{window.name}</div>
                                ))
                            }
                        </div>
                    </div>
                    <div slot={"right"}>
                        <div style={{display : "flex", alignItems : "center", gap : "5px", justifyContent : "flex-end"}}>
                            <Input type={"checkbox"} value={darkMode} onChange={(value: any) => setDarkMode(value)}/>
                        </div>
                    </div>
                </ToolBar>
            </SystemContext.Provider>
        </div>
    )
}

namespace System {
    export interface Attributes {

        depth : number
        search : string
        path : string
        cookies : string
        routes : Router.Route[]
        host : string
        data : any[]
        language : string
        theme : string
    }
}

export default System