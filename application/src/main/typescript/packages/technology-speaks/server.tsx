import express from 'express';
import http from 'http';
import React from 'react';
import {renderToString} from 'react-dom/server';
import {App} from './src/App';
import {createProxyMiddleware} from 'http-proxy-middleware';
import {resolveComponentList, resolveRoute} from "react-ui-simplicity";
import {routes} from "./src/routes"
import cookieParser from 'cookie-parser';
import * as cheerio from 'cheerio';
import * as path from "node:path";
import * as fs from "node:fs";
import {Router} from "react-ui-simplicity";
import {RequestInformation} from "./src/request";

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


let wsProxy = createProxyMiddleware({
    target: 'ws://localhost:8080',
    changeOrigin: true,
    ws : true
});

app.use(cookieParser());

app.use((req, res, next) => {
    const host = req.headers.host;
    const subdomain = host?.split('.')[0];

    if (subdomain && subdomain !== host && req.hostname.endsWith('localhost')) {
        const targetPath = `/service/codemirror/files/user/${subdomain}`;
        createProxyMiddleware({
            target: 'http://localhost:3000' + targetPath,
            changeOrigin: true,
            cookieDomainRewrite: 'localhost:3000',
        })(req, res, next);
    } else {
        next();
    }
});

app.use(
    "/toggle-drawer",
    express.urlencoded({ extended: true })
);

app.use(
    "/toggle-theme",
    express.urlencoded({ extended: true })
);

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
        ws: true,
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
                proxyReq.setHeader("x-forwarded-protocol", "http")
                proxyReq.setHeader("x-forwarded-host", 'localhost:3000')
            }
        }
    })
);

function sendToClient(res: any, data: [Router.Route, React.ReactElement][], info : RequestInformation) : void {

    const appHtml = renderToString(
        <App
            data={data}
            info={info}
        />
    );

    const $ = cheerio.load(rawHtmlTemplate);
    $('#root').html(appHtml);
    $('html').attr("data-theme", info.cookie["theme"] || "light") ;
    res.send($.html());
}

app.post('/toggle-drawer', (req, res) => {
    if (req.body.drawer === "open") {
        res.setHeader("Set-Cookie", "drawer=open; Path=/; Max-Age=31536000");
    } else {
        res.setHeader("Set-Cookie", "drawer=close; Path=/; Max-Age=31536000");
    }
    res.redirect(req.headers.referer || "/");
})

app.post('/toggle-theme', (req, res) => {
    if (req.body.theme === "dark") {
        res.setHeader("Set-Cookie", "theme=dark; Path=/; Max-Age=31536000");
    } else {
        res.setHeader("Set-Cookie", "theme=light; Path=/; Max-Age=31536000");
    }
    res.redirect(req.headers.referer || "/");
})


app.get('*', (req, res) => {
    const path = req.path
    const search = "?" + ((req.url.split('?'))[1] || '');
    const host = req.get('host');
    const protocol = req.protocol;
    const cookie = req.cookies as Record<string, string>
    const drawer = req.cookies["drawer"];
    const language = resolvePreferredLanguage(req.headers['accept-language']);

    const request : RequestInformation = {protocol, host, path, search, cookie, language};

    if (! drawer) {
        res.setHeader("Set-Cookie", "drawer=open; Path=/; Max-Age=31536000");
    }


    const resolved = resolveRoute(request, routes);

    resolveComponentList(resolved, request)
        .then((components) => {
            if (components) {
                sendToClient(res, components, request)
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

const server = http.createServer(app);

server.on('upgrade', (req, socket, head) => {
    console.log('[UPGRADE] Upgrade erhalten:', req.url);
    // @ts-ignore
    wsProxy.upgrade(req, socket, head)
});

server.listen(PORT, () => {
    console.log(`🚀 Server is listening on http://localhost:${PORT}`);
});
