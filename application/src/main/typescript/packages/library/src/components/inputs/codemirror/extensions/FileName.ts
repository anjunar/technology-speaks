import {Facet, StateEffect, StateField} from "@codemirror/state";

export const setFileName = StateEffect.define<string>();

export const fileNameField = StateField.define<string>({
    create() {
        return "";
    },
    update(value, tr) {
        for (let e of tr.effects) {
            if (e.is(setFileName)) return e.value;
        }
        return value;
    }
});

export const fileNameFacet = Facet.define<string, string>({
    combine: inputs => inputs.length ? inputs[inputs.length - 1] : ""
});