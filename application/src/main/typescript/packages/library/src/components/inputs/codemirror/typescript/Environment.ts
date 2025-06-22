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

function importRewriteTransformer(context: ts.TransformationContext): ts.Transformer<ts.SourceFile> {
    const visitor: ts.Visitor = (node): ts.VisitResult<ts.Node> => {
        if (ts.isImportDeclaration(node) && ts.isStringLiteral(node.moduleSpecifier)) {
            const rewrites: Record<string, string> = {
                "react": "./react",
                "react-dom/client": "./react-dom/client",
            };
            const spec = node.moduleSpecifier.text;
            if (spec in rewrites) {
                return ts.factory.updateImportDeclaration(
                    node,
                    node.modifiers,
                    node.importClause,
                    ts.factory.createStringLiteral(rewrites[spec]),
                    node.assertClause
                );
            }
        }
        return ts.visitEachChild(node, visitor, context);
    };

    return (sourceFile: ts.SourceFile): ts.SourceFile => {
        return ts.visitNode(sourceFile, visitor) as ts.SourceFile;
    };
}

export const transpile = (filename: string, js : (js : string, sourceMap : string) => void) => {
    const program = env.languageService.getProgram();
    if (!program) throw new Error("Kein Programm vorhanden");

    const sourceFile = program.getSourceFile(filename);
    if (!sourceFile) throw new Error("SourceFile nicht gefunden");

    const transformers: ts.CustomTransformers = {
        before: [importRewriteTransformer],
    };

    let jsOutput = "";
    let mapOutput = "";

    const emitResult = program.emit(
        sourceFile,
        (fileName, data) => {
            if (fileName.endsWith(".js")) jsOutput = data;
            else if (fileName.endsWith(".js.map")) mapOutput = data;
        },
        undefined,
        false,
        transformers
    );

    if (emitResult.emitSkipped) {
        throw new Error("Emit wurde übersprungen");
    }

    js(jsOutput, mapOutput);
};
