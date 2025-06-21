import {RangeSetBuilder, StateEffect} from "@codemirror/state";
import {Decoration, DecorationSet, hoverTooltip, Tooltip, ViewPlugin, ViewUpdate} from "@codemirror/view";
import {EditorView} from "codemirror";
import {env} from "../typescript/Environment";
import {fileNameFacet} from "./FileName";

const setDiagnostics = StateEffect.define<Tooltip[]>();

export const tsErrorTooltipHover = hoverTooltip((view, pos) => {
    const filename = view.state.facet(fileNameFacet);
    const diagnostics = env.languageService.getSemanticDiagnostics(filename);

    const match = diagnostics.find(d =>
        d.start !== undefined &&
        d.length !== undefined &&
        pos >= d.start &&
        pos <= d.start + d.length
    );

    if (!match) return null;

    const dom = document.createElement("div");
    dom.textContent = typeof match.messageText === "string"
        ? match.messageText
        : (match.messageText as any).messageText || "Fehler";
    dom.style.backgroundColor = "#ffdddd";
    dom.style.color = "#a00";
    dom.style.padding = "2px 6px";
    dom.style.borderRadius = "4px";
    dom.style.fontSize = "0.8em";
    dom.style.maxWidth = "300px";
    dom.style.whiteSpace = "pre-wrap";

    return {
        pos: match.start!,
        above: true,
        create: () => ({dom})
    };
});

export const diagnosticsPlugin = ViewPlugin.fromClass(class {
    timeout: ReturnType<typeof setTimeout>

    constructor(public view: EditorView) {
    }

    update(update: ViewUpdate) {
        if (update.docChanged || update.viewportChanged) {

            clearTimeout(this.timeout);

            this.timeout = setTimeout(() => {
                requestDiagnosticsUpdate(this.view)
            }, 3000);
        }
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
                return {dom};
            }
        }));
}

export function requestDiagnosticsUpdate(view: EditorView) {
    const filename = view.state.facet(fileNameFacet);
    const diagnostics = env.languageService.getSemanticDiagnostics(filename);
    const tooltips = createTooltipsFromDiagnostics(diagnostics);

    view.dispatch({
        effects: setDiagnostics.of(tooltips)
    });
}

export const tsErrorHighlighter = ViewPlugin.fromClass(class {
    decorations: DecorationSet;
    timer: ReturnType<typeof setTimeout>

    constructor(view: EditorView) {
        this.decorations = this.buildDecorations(view);
    }

    update(update: ViewUpdate) {
        if (update.docChanged || update.viewportChanged) {

            clearTimeout(this.timer);

            this.timer = setTimeout(() => {
                this.decorations = this.buildDecorations(update.view);
            }, 3000)

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
                builder.add(from, to, Decoration.mark({class: "ts-error"}));
            }
        });

        return builder.finish();
    }
}, {
    decorations: v => v.decorations
})