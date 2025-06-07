import './App.css';
import React, {useEffect, useState} from 'react';
import {System} from "react-ui-simplicity";
import {routes} from "./routes";
import {init} from "./Persistence"

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

    return <System routes={routes} path={path} search={search} data={properties.initialData}/>;
}

namespace App {
    export interface Attributes {
        initialPath: string
        initialSearch: string
        initialData : any
    }
}