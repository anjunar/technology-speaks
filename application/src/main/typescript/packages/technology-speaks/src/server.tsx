import React from "react";
import { App } from "./App";
import { renderToString } from "react-dom/server";

export function renderHtml(path: string, search: string) {
    const html = renderToString(<App initialPath={path} initialSearch={search} />);
    const dataScript = `<script>window.__INITIAL_DATA__ = ${JSON.stringify({ path, search })}</script>`;

    return `
    <!DOCTYPE html>
    <html lang="de">
      <head>
        <meta charset="UTF-8" />
        <title>SSR App</title>
        <script defer src="/client.js"></script>
      </head>
      <body>
        <div id="root">${html}</div>
        ${dataScript}
      </body>
    </html>
    `;
}