import {RangeSetBuilder, StateEffect, StateField} from "@codemirror/state";
import {Decoration, DecorationSet, showTooltip, Tooltip, ViewPlugin, ViewUpdate} from "@codemirror/view";
import {EditorView} from "codemirror";
import {env} from "../typescript/Environment";
import {syntaxTree} from "@codemirror/language";
import {fileNameFacet, fileNameField} from "./FileName";

const setDiagnostics = StateEffect.define<Tooltip[]>();

export const diagnosticsField = StateField.define<Tooltip[]>({
    create() { return []; },
    update(tooltips, tr) {
        for (const e of tr.effects) {
            if (e.is(setDiagnostics)) return e.value;
        }
        return tooltips;
    },
    provide: f => showTooltip.from(f, tooltips => tooltips[0])
});

export const diagnosticsPlugin = ViewPlugin.fromClass(class {
    constructor(public view: EditorView) {}

    update(update: ViewUpdate) {
        if (update.docChanged || update.viewportChanged) {
            requestDiagnosticsUpdate(this.view);
        }
    }
});

export const closeTooltipOnClick = ViewPlugin.fromClass(class {
    constructor(public view: EditorView) {
        this.clickHandler = this.clickHandler.bind(this);
        view.dom.addEventListener("click", this.clickHandler);
    }
    clickHandler() {
        this.view.dispatch({
            effects: setDiagnostics.of([])
        });
    }
    destroy() {
        this.view.dom.removeEventListener("click", this.clickHandler);
    }
});

function createTooltipsFromDiagnostics(diagnostics: readonly import("typescript").Diagnostic[]): Tooltip[] {
    return diagnostics
        .filter(d => d.start !== undefined && d.length !== undefined)
        .map(d => ({
            pos: d.start!,
            above: true,
            create: () => {
                const dom = document.createElement("div");
                dom.textContent = typeof d.messageText === "string"
                    ? d.messageText
                    : (d.messageText as any).messageText || "Fehler";
                dom.style.backgroundColor = "#ffdddd";
                dom.style.color = "#a00";
                dom.style.padding = "2px 6px";
                dom.style.borderRadius = "4px";
                dom.style.fontSize = "0.8em";
                dom.style.maxWidth = "300px";
                dom.style.whiteSpace = "pre-wrap";
                return { dom };
            }
        }));
}

export function requestDiagnosticsUpdate(view: EditorView) {
    const filename = view.state.facet(fileNameFacet);
    const diagnostics = env.languageService.getSemanticDiagnostics(filename);
    const tooltips = createTooltipsFromDiagnostics(diagnostics);

/*
    view.dispatch({
        effects: setDiagnostics.of(tooltips)
    });
*/
}

export const tsErrorHighlighter = ViewPlugin.fromClass(class {
    decorations: DecorationSet;

    constructor(view: EditorView) {
        this.decorations = this.buildDecorations(view);
    }

    update(update: ViewUpdate) {
        if (update.docChanged || update.viewportChanged) {
            this.decorations = this.buildDecorations(update.view);
        }
    }

    buildDecorations(view: EditorView) {
        const builder = new RangeSetBuilder<Decoration>();
        const filename = view.state.facet(fileNameFacet);
        const diagnostics = env.languageService.getSemanticDiagnostics(filename);

        diagnostics.forEach(diag => {
            if (diag.start !== undefined && diag.length !== undefined) {
                const from = diag.start;
                const to = diag.start + diag.length;
                if (isTSRange(view, from, to)) {
                    builder.add(from, to, Decoration.mark({class: "ts-error"}));
                }
            }
        });

        return builder.finish();
    }
}, {
    decorations: v => v.decorations
});

function isTSRange(view: EditorView, from: number, to: number) {
    let tree = syntaxTree(view.state);
    let syntaxNode = tree.resolveInner(from, -1);
    let node = syntaxNode;
    while (node && node.to <= to) {
        if (node.name === "Script" || node.name === "ImportDeclaration" || node.name === "TSFile") {
            return true;
        }
        node = node.parent;
    }
    return false;
}
