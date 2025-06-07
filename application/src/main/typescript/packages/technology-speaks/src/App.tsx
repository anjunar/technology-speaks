import './App.css';
import React, {useEffect, useState} from 'react';
import {System} from "react-ui-simplicity";
import {routes} from "./routes";
import {init} from "./Persistence"

init()

export function App({initialPath, initialSearch}: { initialPath: string; initialSearch: string }) {
    const [path, setPath] = useState(initialPath);
    const [search, setSearch] = useState(initialSearch);

    useEffect(() => {
        const onPopState = () => {
            setPath(window.location.pathname);
            setSearch(window.location.search);
        };

        window.addEventListener("popstate", onPopState);
        return () => window.removeEventListener("popstate", onPopState);
    }, []);

    return <System routes={routes} path={path} search={search}/>;
}