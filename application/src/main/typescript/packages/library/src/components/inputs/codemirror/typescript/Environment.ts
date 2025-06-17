import lib from "./lib.json";
import csstype from "./csstype/index.d"
import react from "./react/index.d"
import reactJsx from "./react/jsx-dev-runtime.d"
import reactDom from "./react-dom/index.d"
import reactDomClient from "./react-dom/client.d"
import {createSystem, createVirtualTypeScriptEnvironment} from "@typescript/vfs";
import ts from "typescript";

const fsMap = new Map();
for (const key in lib) {
    fsMap.set(key, (lib as any)[key]);
}

const system = createSystem(fsMap);

/*
fsMap.set('/node_modules/@types/csstype/index.d.ts', csstype);
fsMap.set('/node_modules/@types/react/index.d.ts', react);
fsMap.set('/node_modules/@types/react/jsx-dev-runtime.d.ts', reactJsx);
fsMap.set('/node_modules/@types/react-dom/index.d.ts', reactDom);
fsMap.set('/node_modules/@types/react-dom/client.d.ts', reactDomClient);
*/

system.writeFile("/index.tsx", "export const html = 1")
system.writeFile("/global.d.ts", `
declare module "csstype" {
  ${csstype}
}

declare module "react" {
  ${react}
  ${reactJsx}
}

declare module "react-dom/client" {
  ${reactDom}
  ${reactDomClient}
}`)

const compilerOpts = {
    target: ts.ScriptTarget.ESNext,
    lib: Object.keys(lib).map(key => key.replace('/lib.', '')),
    allowJs: true,
    esModuleInterop: true,
    module: ts.ModuleKind.ESNext,
    jsx: ts.JsxEmit.React,
    moduleResolution: ts.ModuleResolutionKind.NodeJs,
    typeRoots: ["/node_modules/@types"]
};

export let env = createVirtualTypeScriptEnvironment(
    system,
    ["/index.tsx", "/global.d.ts"],
    ts,
    compilerOpts
);

export const transpile = (filename: string, js : (value : string) => void) => {
    const output = env.languageService.getEmitOutput(filename);
    const jsOutput = output.outputFiles.find(file => file.name.endsWith(".js"));
    js(jsOutput.text);
};
