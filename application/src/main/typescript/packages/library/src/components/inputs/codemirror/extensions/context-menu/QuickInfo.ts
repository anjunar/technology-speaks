import {env} from "../../typescript/Environment";

export function quickInfo(filename: string, pos: number, event: MouseEvent) {
    const quickInfo = document.createElement("button")
    quickInfo.textContent = "QuickInfo";
    quickInfo.className = "hover"
    quickInfo.onclick = () => {
        showQuickInfo(filename, pos, event);
    };
    return quickInfo;
}

function showQuickInfo(filename: string, pos: number, event: MouseEvent) {
    const info = env.languageService.getQuickInfoAtPosition(filename, pos);
    if (!info) return;

    const text = (info.displayParts ?? []).map(p => p.text).join("");
    const tooltip = document.createElement("div");
    tooltip.textContent = text;
    tooltip.style.position = "absolute";
    tooltip.style.top = `${event.clientY + 20}px`;
    tooltip.style.left = `${event.clientX}px`;
    tooltip.style.background = "var(--color-background-secondary)";
    tooltip.style.border = "1px solid #88a";
    tooltip.style.padding = "4px";
    tooltip.style.borderRadius = "4px";
    tooltip.style.zIndex = "1001";
    tooltip.style.fontSize = "0.8em";

    document.body.appendChild(tooltip);

    setTimeout(() => {
        const close = () => {
            tooltip.remove();
            window.removeEventListener("click", close);
        };
        window.addEventListener("click", close);
    }, 300)

}
