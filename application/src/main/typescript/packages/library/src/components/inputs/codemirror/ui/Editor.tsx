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
import {dracula} from "@uiw/codemirror-theme-dracula";
import {typescriptCompletionSource} from "../extensions/AutoComplete";
import {fileNameFacet} from "../extensions/FileName";
import {FileService} from "../service/FileService";
import {env, system} from "../typescript/Environment";
import {html} from "@codemirror/lang-html";
import {javascript} from "@codemirror/lang-javascript";
import {css} from "@codemirror/lang-css";
import {CodeMirrorContent} from "../domain/CodeMirrorContent";


export const draculaTheme = EditorView.theme({
    '&': {
        backgroundColor: '#282a36',
        color: '#f8f8f2',
    },
    '.cm-content': {
        caretColor: '#f8f8f2',
    },
    '&.cm-focused .cm-selectionBackground, .cm-selectionBackground, .cm-content ::selection': {
        backgroundColor: '#44475a',
    },
    '.cm-gutters': {
        backgroundColor: '#282a36',
        color: '#6272a4',
        border: 'none',
    },
}, { dark: true });

function getExtensions(typescript: any, updateListener: Extension, newFileName: string, info: RequestInformation) {
    return [
        basicSetup,
        updateListener,
        fileNameFacet.of(newFileName),
        info.cookie.theme === "dark" ? draculaTheme : [],
        info.cookie.theme === "dark" ? dracula : [],
        keymap.of([indentWithTab, ...defaultKeymap]),
    ]
}

function getExtensionsForTypescript(typescript: any, updateListener: Extension, newFileName: string, info: RequestInformation) {
    return [
        ...getExtensions(typescript, updateListener, newFileName, info),

        typescript,
        autocompletion({override: [typescriptCompletionSource(newFileName)]}),
        tsErrorHighlighter,
        diagnosticsField,
        diagnosticsPlugin,
        closeTooltipOnClick
    ];
}

function getExtensionsForHTML(htmlMixed: any, updateListener: Extension, newFileName: string, info: RequestInformation) {
    return [
        ...getExtensions(typescript, updateListener, newFileName, info),

        htmlMixed
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

let saveTimeout : ReturnType<typeof setTimeout>

export function Editor(properties: Editor.Attributes) {

    const {configuration, value, style} = properties

    const [state, setState] = useState<CodeMirrorContent>(value)

    const editorViewRef = useRef<HTMLDivElement>(null);

    const [editorInitializer, setEditorInitializer] = useState(false)

    const {info} = useContext(SystemContext)

    const fileService = new FileService(configuration, system, env)

    const updateListener = EditorView.updateListener.of(update => {
        if (update.docChanged) {
            const content = update.state.doc.toString();

            clearTimeout(saveTimeout);

            saveTimeout = setTimeout(async () => {
                try {
                    state.content = content;
                    const response = await fileService.updateFile(state as any);
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
    }, [editorInitializer]);

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
    }, [state, editorInitializer]);

    useEffect(() => {
        if (state.content !== value.content) {
            setState(value)
        }
    }, [value]);

    useEffect(() => {

        setEditorInitializer(true)

        return () => {
            if (editorView) {
                editorView.destroy()
            }
        }
    }, []);

    return (
        <div className={"editor"} style={style} ref={editorViewRef}></div>
    )
}

export namespace Editor {
    export interface Attributes {
        value : CodeMirrorContent
        configuration: CodeMirror.Configuration
        style? : CSSProperties
    }
}

export default Editor