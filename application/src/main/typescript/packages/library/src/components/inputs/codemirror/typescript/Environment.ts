import lib from "./lib.json";
import react from "./react/index.d"
import reactDom from "./react-dom/index.d"
import reactDomClient from "./react-dom/client.d"
import {createSystem, createVirtualTypeScriptEnvironment} from "@typescript/vfs";
import ts from "typescript";

const fsMap = new Map();
for (const key in lib) {
    fsMap.set(key, (lib as any)[key]);
}

export const system = createSystem(fsMap);

system.writeFile("/index.tsx", "import React from \"react\";")
system.writeFile("/global.d.ts", `
declare module "react" {
  ${react}
}

declare module "react-dom" {
  ${reactDom}
}

declare module "react-dom/client" {
  ${reactDomClient}
}`)

system.writeFile("/index.html", `
<!DOCTYPE html> 
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Document</title>
        <script type="module" src="./index"></script>
    </head>
    <body>
        <div id="root"></div>    
    </body>
</html>`)

const compilerOpts = {
    target: ts.ScriptTarget.ESNext,
    lib: Object.keys(lib).map(key => key.replace('/lib.', '')),
    allowJs: true,
    esModuleInterop: true,
    module: ts.ModuleKind.ESNext,
    jsx: ts.JsxEmit.React,
    moduleResolution: ts.ModuleResolutionKind.NodeJs,
    sourceMap: true,
};

export let env = createVirtualTypeScriptEnvironment(
    system,
    ["/index.tsx", "/global.d.ts"],
    ts,
    compilerOpts
);

export const transpile = (filename: string, js : (js : string, sourceMap : string) => void) => {
    const output = env.languageService.getEmitOutput(filename);
    const jsOutput = output.outputFiles.find(file => file.name.endsWith(".js"));
    const mapOutput = output.outputFiles.find(file => file.name.endsWith(".map"));
    let replace = jsOutput.text
        .replace("from \"react\"", "from \"./react\"")
        .replace("from \"react-dom/client\"", "from \"./react-dom/client\"")
    js(replace, mapOutput.text);
};
