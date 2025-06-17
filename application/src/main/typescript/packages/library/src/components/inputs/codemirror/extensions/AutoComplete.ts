import {CompletionContext} from "@codemirror/autocomplete";
import {syntaxTree} from "@codemirror/language";
import {env} from "../typescript/Environment";
import {EditorState} from "@codemirror/state";
import {cssCompletionSource} from "@codemirror/lang-css";
import {htmlCompletionSource} from "@codemirror/lang-html";
import {localCompletionSource} from "@codemirror/lang-javascript";
import {fileNameFacet} from "./FileName";

export const typescriptCompletionSource = (filename: string) => {
    return async (context: CompletionContext) => {
        const word = context.matchBefore(/\w*/);

        const code = context.state.doc.toString();
        env.updateFile(filename, code);

        const cursorPos = context.pos;
        const completions = env.languageService.getCompletionsAtPosition(filename, cursorPos, {
            includeCompletionsForModuleExports: true,
            includeCompletionsWithInsertText: true,
            allowTextChangesInNewFiles: true,
        });

        if (!completions) return null;

        return {
            from: word.from,
            options: completions.entries.map((entry) => ({
                label: entry.name,
                type: entry.kind,
                info: entry.kindModifiers,
            })),
        };
    };
};

function getLanguageAtPosition(context) {
    const tree = syntaxTree(context.state);
    let node = tree.resolveInner(context.pos, -1);

    while (node) {
        if (node.name === "Script" || node.name === "ImportDeclaration") return "typescript";
        if (node.name === "StyleElement") return "css";
        if (node.name === "OpenTag" || node.name === "TagName") return "html";
        node = node.parent;
    }
    return "";
}

export const multiLanguageCompletion = async (context) => {
    const lang = getLanguageAtPosition(context);
    const filename = context.state.facet(fileNameFacet)

    if (filename.endsWith(".js") || filename.endsWith(".jsx")) {
        return localCompletionSource(context);
    }
    if (filename.endsWith(".css")) {
        return cssCompletionSource(context);
    }
    if (filename.endsWith(".html")) {
        return htmlCompletionSource(context);
    }

    if (lang === "typescript") return localCompletionSource(context);
    if (lang === "css") return cssCompletionSource(context);
    if (lang === "html") return htmlCompletionSource(context);

    return null;
};
