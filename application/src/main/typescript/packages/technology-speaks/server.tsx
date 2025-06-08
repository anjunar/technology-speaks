import express from 'express';
import React from 'react';
import {renderToString} from 'react-dom/server';
import {App} from './src/App';
import {createProxyMiddleware} from 'http-proxy-middleware';
import {resolveComponentList} from "react-ui-simplicity/src/components/navigation/router/Router";
import {routes} from "./src/routes"
import {Router} from "react-ui-simplicity";

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
        changeOrigin: true
    })
);

function sendToClient<ResBody, LocalsObj>(path: string, search: string, res: any, data: any) {
    const appHtml = renderToString(
        <App
            initialPath={path}
            initialSearch={search}
            initialData={data}
            host={res.get('host')}
        />
    );
    res.send(`
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8" />
        <title>SSR React App</title>
        <style>
            html, body, #root {
               height: 100%;
            }        
        </style>
      </head>
      <body>
        <div id="root">${appHtml}</div>
        <script src="/static/main.js"></script>
      </body>
    </html>
  `);
}

app.get('*', (req, res) => {
    let path = req.path.split('?')[0];
    let search = req.path.split('?')[1] || '';
    let host = req.get('host');

    resolveComponentList(path, search, routes, host)
        .then((component) => {
            if (component) {
                sendToClient(path, search, res, component)
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
