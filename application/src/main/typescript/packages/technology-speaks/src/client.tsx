import React from 'react';
import {hydrateRoot} from 'react-dom/client';
import {App} from './App';
import {
    resolveComponentList, resolveRoute,
} from "react-ui-simplicity";
import {routes} from "./routes";
import Cookies from "js-cookie";

const initialPath = window.location.pathname
const initialSearch = window.location.search

async function main() {
    const resolved = resolveRoute(initialPath, initialSearch, routes);

    const components = await resolveComponentList(resolved, initialPath, initialSearch, window.location.origin)

    hydrateRoot(document.getElementById('root'), (
        <App
            path={initialPath}
            search={initialSearch}
            data={components}
            host={window.location.origin}
            cookies={document.cookie}
            language={window.navigator.language.split("-")[0] || "en"}
            theme={Cookies.get("theme") || "light"}
        />
    ),  {
        onUncaughtError: (error, errorInfo) => {
            console.error(errorInfo.componentStack)
        },
        onRecoverableError: (error, errorInfo) => {
            console.warn(errorInfo.componentStack)
        },
        onCaughtError: (error, errorInfo) => {
            console.error(errorInfo.componentStack)
        }
    })
}

main().catch(console.error)