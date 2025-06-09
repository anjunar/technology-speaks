import express from 'express';
import React from 'react';
import {renderToString} from 'react-dom/server';
import {App} from './src/App';
import {createProxyMiddleware} from 'http-proxy-middleware';
import {resolveComponentList} from "react-ui-simplicity/src/components/navigation/router/Router";
import {routes} from "./src/routes"
import cookieParser from 'cookie-parser';
import * as cheerio from 'cheerio';
import * as path from "node:path";
import * as fs from "node:fs";
import {Router} from "react-ui-simplicity";
import session from 'express-session';

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

const indexHtmlPath = path.resolve(__dirname, 'public', 'index.html');
const rawHtmlTemplate = fs.readFileSync(indexHtmlPath, 'utf8');

const app = express();
const PORT = 3000;

app.use(cookieParser());

app.use(session({
    secret: 'dein-geheimes-session-secret',
    resave: false,
    saveUninitialized: false,
    cookie: { secure: false } // für localhost, true wenn HTTPS
}));

app.use(
    '/.well-known',
    createProxyMiddleware({
        target: 'http://localhost:3001/.well-known',
        changeOrigin: true,
    })
);

app.use(
    '/static',
    createProxyMiddleware({
        target: 'http://localhost:3001/static',
        changeOrigin: true,
    })
);

app.use(
    '/assets',
    createProxyMiddleware({
        target: 'http://localhost:3001/assets',
        changeOrigin: true,
    })
);

app.use(
    '/service',
    createProxyMiddleware({
        target: 'http://localhost:3001/service',
        changeOrigin: true,
        on : {
            proxyReq: (proxyReq, req, res) => {
                proxyReq.setHeader('x-language', req.headers["x-language"] || "de");
                proxyReq.setHeader('host', 'localhost:3000');
            }
        }
    })
);

function sendToClient(path: string, search: string, res: any, data: [Router.Route, React.ReactElement][], language: string, cookie: string, theme : string) : void {

    const appHtml = renderToString(
        <App
            path={path}
            search={search}
            data={data}
            host={res.get('host')}
            language={language}
            cookies={cookie}
            theme={theme}
        />
    );

    const $ = cheerio.load(rawHtmlTemplate);
    $('#root').html(appHtml);
    $('html').attr("data-theme", theme);
    res.send($.html());
}

app.get('*', (req, res) => {
    const path = req.path
    const search = "?" + (req.url.split('?'))[1] || '';
    const host = req.get('host');
    const cookie = req.get("cookie");
    const theme = req.cookies.theme || 'light';

    const language = resolvePreferredLanguage(req.headers['accept-language']);

    resolveComponentList(path, search, routes, host, false, true)
        .then((components) => {
            if (components) {
                sendToClient(path, search, res, components, language, cookie, theme)
            } else {
                res.status(404).send('Not found');
            }
        })
        .catch((error) => {
            if (error.url) {
                res.statusCode = 302
                res.setHeader("Location", error.url)
                res.end()
            } else {
                res.status(500).send(error.message);
            }
        })

});

app.listen(PORT, () => {
    console.log(`🚀 Server is listening on http://localhost:${PORT}`);
});
