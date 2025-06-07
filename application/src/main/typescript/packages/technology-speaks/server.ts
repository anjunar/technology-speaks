import express from "express";
import path from "path";
import { renderHtml } from "./src/server";

const app = express();
const port = 3000;

app.use(express.static("dist")); // z.B. für /client.js, /index.css

app.get("*", (req, res) => {
    const html = renderHtml(req.path, req.url.includes("?") ? req.url.split("?")[1] : "");
    res.send(html);
});

app.listen(port, () => {
    console.log(`SSR-Server läuft auf http://localhost:${port}`);
});