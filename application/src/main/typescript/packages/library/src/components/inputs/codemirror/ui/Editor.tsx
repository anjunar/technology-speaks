import React, {CSSProperties, useContext, useEffect, useMemo, useRef, useState} from 'react';
import {SystemContext} from "../../../../System";
import CodeMirror from "../CodeMirror";
import {basicSetup, EditorView } from "codemirror";
import {EditorState, Extension} from "@codemirror/state";
import { defaultKeymap, indentWithTab } from "@codemirror/commands";
import { keymap } from '@codemirror/view';
import {
    closeTooltipOnClick,
    diagnosticsField,
    diagnosticsPlugin,
    requestDiagnosticsUpdate,
    tsErrorHighlighter
} from "../extensions/Diagnostics";
import {RequestInformation} from "technology-speaks/src/request";
import {autocompletion} from "@codemirror/autocomplete";
import {multiLanguageCompletion, typescriptCompletionSource} from "../extensions/AutoComplete";
import {fileNameFacet} from "../extensions/FileName";
import {dracula} from "@uiw/codemirror-theme-dracula";
import {material} from "@uiw/codemirror-theme-material";
import {FileService} from "../service/FileService";
import {env, system} from "../typescript/Environment";
import {html} from "@codemirror/lang-html";
import {javascript} from "@codemirror/lang-javascript";
import {css} from "@codemirror/lang-css";

function getExtensionsForTypescript(htmlMixed: any, updateListener: Extension, newFileName: string, info: RequestInformation) {
    return [
        basicSetup,
        htmlMixed,
        autocompletion({override: [typescriptCompletionSource(newFileName)]}),
        tsErrorHighlighter,
        diagnosticsField,
        diagnosticsPlugin,
        updateListener,
        fileNameFacet.of(newFileName),
        closeTooltipOnClick,
        info.cookie.theme === "dark" ? dracula : material,
        keymap.of([indentWithTab, ...defaultKeymap])
    ];
}

function getExtensionsForHTML(htmlMixed: any, updateListener: Extension, newFileName: string, info: RequestInformation) {
    return [
        basicSetup,
        htmlMixed,
        autocompletion({override: [multiLanguageCompletion]}),
        updateListener,
        fileNameFacet.of(newFileName),
        info.cookie.theme === "dark" ? dracula : material
    ];
}

const htmlMixed = html({
    matchClosingTags: true,
    autoCloseTags: true,
    nestedLanguages: [
        {
            tag: "script",
            parser: javascript().language.parser
        },
        {
            tag: "style",
            parser: css().language.parser
        }
    ]
})

const typescript = javascript({
    typescript: true,
    jsx: true
})

let saveTimeout;

export function Editor(properties: Editor.Attributes) {

    const {configuration, value, style} = properties

    const [state, setState] = useState<CodeMirror.FileEntry>(value)

    const editorViewRef = useRef<HTMLDivElement>(null);

    const [initializer, setInitializer] = useState(false)

    const {info} = useContext(SystemContext)

    const fileService = new FileService(configuration, system, env)

    const updateListener = EditorView.updateListener.of(update => {
        if (update.docChanged) {
            const content = update.state.doc.toString();

            clearTimeout(saveTimeout);

            saveTimeout = setTimeout(async () => {
                try {
                    const response = await fileService.updateFile({ ...state, content });
                    state.content = content;
                    console.log("✅ Datei gespeichert.");
                } catch (err) {
                    console.error("❌ Fehler beim Speichern:", err);
                }
            }, 1000);
        }
    });

    const editorView = useMemo(() => {
        if (editorViewRef.current === null) {
            return null;
        }

        return new EditorView({
            parent: editorViewRef.current
        });
    }, [initializer]);

    useEffect(() => {
        if (state && editorView) {
            if (state.name.endsWith("html")) {
                editorView.setState(EditorState.create({
                    doc: state.content,
                    extensions: getExtensionsForHTML(htmlMixed, updateListener, state.name, info)
                }))
            } else {
                editorView.setState(EditorState.create({
                    doc: state.content,
                    extensions: getExtensionsForTypescript(typescript, updateListener, state.name, info)
                }))
                requestDiagnosticsUpdate(editorView);
            }
        }
    }, [state, initializer]);

    useEffect(() => {
        setState(value)
    }, [value]);

    useEffect(() => {

        setInitializer(true)

        return () => {
            if (editorView) {
                editorView.destroy()
            }
        }
    }, []);

    return (
        <div className={"editor"} style={style} ref={editorViewRef}></div>
    )
};

export namespace Editor {
    export interface Attributes {
        value : CodeMirror.FileEntry
        configuration: CodeMirror.Configuration
        style? : CSSProperties
    }
}

export default Editor;