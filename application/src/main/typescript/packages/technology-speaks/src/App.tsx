import './App.css';
import React, {useEffect, useState} from 'react';
import {Router, System} from "react-ui-simplicity";
import {init} from "./Persistence"
import {routes} from "./routes";

init()

export function App(properties : App.Attributes) {
    const [path, setPath] = useState(properties.initialPath);
    const [search, setSearch] = useState(properties.initialSearch);

    useEffect(() => {
        const onPopState = () => {
            setPath(window.location.pathname);
            setSearch(window.location.search);
        };

        window.addEventListener("popstate", onPopState);
        return () => window.removeEventListener("popstate", onPopState);
    }, []);

    return <System depth={0} routes={routes} path={path} search={search} data={properties.initialData} host={properties.host}/>;
}

namespace App {
    export interface Attributes {
        host : string
        initialPath: string
        initialSearch: string
        initialData : any[]
    }
}