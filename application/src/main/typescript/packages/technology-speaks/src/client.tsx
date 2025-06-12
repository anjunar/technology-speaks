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

function resolvePreferredLanguage(header: string): string {
    if (!header) return "en";

    const languages = header
        .split(",")
        .map(part => {
            const [lang, q] = part.trim().split(";q=");
            return { lang, q: parseFloat(q || "1") };
        })
        .sort((a, b) => b.q - a.q);

    return languages[0]?.lang?.split("-")[0] || "en";
}

function parseCookieString(cookieString) {
    return cookieString
        .split("; ")
        .map(cookie => cookie.split("="))
        .reduce((acc, [key, value]) => {
            acc[key] = decodeURIComponent(value);
            return acc;
        }, {});
}

async function main() {
    const resolved = resolveRoute(initialPath, initialSearch, routes);

    const components = await resolveComponentList(resolved, initialPath, initialSearch, window.location.origin, parseCookieString(document.cookie), window.navigator.language)

    hydrateRoot(document.getElementById('root'), (
        <App
            path={initialPath}
            search={initialSearch}
            data={components}
            host={window.location.origin}
            cookies={parseCookieString(document.cookie) || {}}
            language={window.navigator.language.split("-")[0] || "en"}
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