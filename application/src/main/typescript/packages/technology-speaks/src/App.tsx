import './App.css';
import React, {useEffect, useState} from 'react';
import {Router, System} from "react-ui-simplicity";
import {init} from "./Persistence"
import {routes} from "./routes";

init()

export function App(properties : App.Attributes) {
    const {data, host, language, cookies} = properties
    const [path, setPath] = useState(properties.path);
    const [search, setSearch] = useState(properties.search);

    useEffect(() => {
        const onPopState = () => {
            setPath(window.location.pathname);
            setSearch(window.location.search);
        };

        window.addEventListener("popstate", onPopState);
        return () => window.removeEventListener("popstate", onPopState);
    }, []);

    return <System depth={0}
                   routes={routes}
                   path={path}
                   cookies={cookies}
                   search={search}
                   data={data}
                   host={host}
                   language={language}
    />;
}

namespace App {
    export interface Attributes {
        host : string
        language : string
        cookies : Record<string, string>
        path: string
        search: string
        data : [Router.Route, React.ReactElement][]
    }
}