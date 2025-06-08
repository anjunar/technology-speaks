import express from 'express';
import React from 'react';
import {renderToString} from 'react-dom/server';
import {App} from './src/App';
import {createProxyMiddleware} from 'http-proxy-middleware';
import {resolveComponentList} from "react-ui-simplicity/src/components/navigation/router/Router";
import {routes} from "./src/routes"
import * as path from "node:path";

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

const app = express();
const PORT = 3000;

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
        headers: {
            'x-language': 'de',
        }
    })
);

function sendToClient<ResBody, LocalsObj>(path: string, search: string, res: any, data: any, language: string, cookie: string) : void {
    const appHtml = renderToString(
        <App
            initialPath={path}
            initialSearch={search}
            initialData={data}
            host={res.get('host')}
            language={language}
            cookies={cookie}
        />
    );
    res.send(`
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8" />
        <link rel="stylesheet" href="/static/assets/style.css">
        <title>SSR React App</title>
      </head>
      <body>
        <div id="root">${appHtml}</div>
        <script src="/static/main.js"></script>
      </body>
    </html>
  `);
}

app.get('*', (req, res) => {
    let path = req.path
    let search = "?" + (req.url.split('?'))[1] || '';
    let host = req.get('host');
    let cookie = req.get("cookie");

    const language = resolvePreferredLanguage(req.headers['accept-language']);

    resolveComponentList(path, search, routes, host)
        .then((component) => {
            if (component) {
                sendToClient(path, search, res, component, language, cookie)
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
