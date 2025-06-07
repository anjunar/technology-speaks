import express from 'express';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { App } from './src/App';
import path from 'path';
import { createProxyMiddleware } from 'http-proxy-middleware';

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

app.get('*', (req, res) => {
    const appHtml = renderToString(
        <App
            initialPath={req.path.split('?')[0]}
            initialSearch={req.path.split('?')[1] || ''}
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
});

app.listen(PORT, () => {
    console.log(`🚀 Server is listening on http://localhost:${PORT}`);
});
