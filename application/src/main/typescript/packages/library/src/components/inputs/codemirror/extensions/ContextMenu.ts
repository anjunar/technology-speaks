import {fileNameFacet} from "./FileName";
import {ViewPlugin} from "@codemirror/view";
import {EditorView} from "codemirror";
import {quickInfo} from "./context-menu/QuickInfo";

export const contextMenuPlugin = ViewPlugin.fromClass(class {
    constructor(public view: EditorView) {
        this.onContextMenu = this.onContextMenu.bind(this);
        view.dom.addEventListener("contextmenu", this.onContextMenu);
    }

    destroy() {
        this.view.dom.removeEventListener("contextmenu", this.onContextMenu);
    }

    onContextMenu(event: MouseEvent) {
        event.preventDefault();

        const filename = this.view.state.facet(fileNameFacet);
        const pos = this.view.posAtCoords({ x: event.clientX, y: event.clientY });
        if (pos == null) return;

        const menu = document.createElement("div");
        menu.style.position = "absolute";
        menu.style.top = `${event.clientY}px`;
        menu.style.left = `${event.clientX}px`;
        menu.style.background = "var(--color-background-secondary)";
        menu.style.border = "1px solid #ccc";
        menu.style.padding = "4px";
        menu.style.zIndex = "1000";
        menu.style.fontSize = "0.85em";
        menu.style.cursor = "pointer";

        menu.appendChild(quickInfo(filename, pos, event));

        document.body.appendChild(menu);

        const close = () => {
            menu.remove();
            window.removeEventListener("click", close);
        };
        window.addEventListener("click", close);
    }
});