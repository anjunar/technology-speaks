import React from 'react';
import {hydrateRoot} from 'react-dom/client';
import {App} from './App';
import {
    resolveComponentList,
} from "react-ui-simplicity/src/components/navigation/router/Router";
import {routes} from "./routes";

const initialPath = window.location.pathname
const initialSearch = window.location.search

async function main() {
    const components = await resolveComponentList(initialPath, initialSearch, routes, window.location.origin)

    hydrateRoot(document.getElementById('root'), (
        <App
            initialPath={initialPath}
            initialSearch={initialSearch}
            initialData={components}
            host={window.location.origin}
        />
    ))
}

main().catch(console.error)