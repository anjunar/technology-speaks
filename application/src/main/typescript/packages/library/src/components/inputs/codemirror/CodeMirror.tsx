import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';
import {basicSetup, EditorView} from "codemirror"
import {css} from "@codemirror/lang-css"
import {html} from "@codemirror/lang-html"
import {javascript} from "@codemirror/lang-javascript"
import {EditorState, Transaction} from "@codemirror/state"
import {autocompletion} from "@codemirror/autocomplete";
import {
    closeTooltipOnClick,
    diagnosticsField,
    diagnosticsPlugin,
    requestDiagnosticsUpdate,
    tsErrorHighlighter
} from "./extensions/Diagnostics";
import {multiLanguageCompletion} from "./extensions/AutoComplete";
import {env, transpile} from "./typescript/Environment";
import {fileNameFacet, setFileName} from "./extensions/FileName";
import {useInput} from "../../../hooks";
import {Model} from "../../shared";
import FileEntry = CodeMirror.FileEntry;
import {FormContext} from "../form/Form";

export function CodeMirror(properties: CodeMirror.Attributes) {

    const {name, standalone, value, onChange, onModel, loadAllFiles} = properties

    const editorViewRef = useRef<HTMLDivElement>(null);

    const [files, setFiles] = useState<CodeMirror.FileEntry[]>([{ name: "/index.tsx", content: ""}])

    const [newFileName, setNewFileName] = useState("newfile.tsx")

    const [initializer, setInitializer] = useState(false)

    const activeFile = useRef("/index.tsx");

    const [model, state, setState]  = useInput(name, value, standalone, "codemirror")

    let context = useContext(FormContext)

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

    const updateListener = EditorView.updateListener.of(update => {
        if (update.docChanged) {
            const content = update.state.doc.toString();
            fetch("/service/codemirror/files", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name: activeFile.current, content })
            });

            let fileEntry = files.find(file => file.name === activeFile.current);

            setState(content)

            if (onChange) {
                onChange(content)
            }

            if (fileEntry) {
                fileEntry.content = content;
            } else {
                setFiles([...files, {name: activeFile.current, content}])
            }
        }
    });


    const editorView = useMemo(() => {
        if (editorViewRef.current === null) {
            return null;
        }

        let view = new EditorView({
            parent: editorViewRef.current,
            state: EditorState.create({
                doc: "",
                extensions: [
                    basicSetup,
                    htmlMixed,
                    autocompletion({
                        override: [multiLanguageCompletion],
                    }),
                    tsErrorHighlighter,
                    diagnosticsField,
                    diagnosticsPlugin,
                    updateListener,
                    fileNameFacet.of(activeFile.current),
                    closeTooltipOnClick
                ]
            })
        });

        requestDiagnosticsUpdate(view);

        return view;
    }, [initializer])

    function activeFileHandler(file: FileEntry) {
        activeFile.current = file.name

        const state = EditorState.create({
            doc: file.content,
            extensions: [
                basicSetup,
                htmlMixed,
                autocompletion({ override: [multiLanguageCompletion] }),
                tsErrorHighlighter,
                diagnosticsField,
                diagnosticsPlugin,
                updateListener,
                fileNameFacet.of(file.name),
                closeTooltipOnClick
            ]
        });

        editorView.setState(state);
        setState(file.content);
        if (onChange) {
            onChange(file.content)
        }

        requestDiagnosticsUpdate(editorView);
    }

    function createNewFile() {

        let content = "import React from \"https://esm.sh/react\";";
        const newFile = { name: newFileName, content: content };

        setFiles(prevFiles => [...prevFiles, newFile]);
        activeFile.current = newFileName;

        const state = EditorState.create({
            doc: newFile.content,
            extensions: [
                basicSetup,
                htmlMixed,
                autocompletion({ override: [multiLanguageCompletion] }),
                tsErrorHighlighter,
                diagnosticsField,
                diagnosticsPlugin,
                updateListener,
                fileNameFacet.of(newFileName),
                closeTooltipOnClick
            ]
        });

        env.createFile(newFileName, content);

        editorView.setState(state);

        setState(content)
        if (onChange) {
            onChange(content)
        }

        requestDiagnosticsUpdate(editorView);

    }

    function transpileHandler() {
        files.forEach(file => {
            transpile(file.name, js => {
                fetch("/service/codemirror/files", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ name: file.name, transpiled :js })
                });
            })
        })
    }

    useEffect(() => {

        if (context) {
            if (onModel) {
                onModel(model)
            }
        }

        loadAllFiles()
            .then((files : CodeMirror.FileEntry[]) => {
                let fileNames = files.map(file => {
                    env.createFile(file.name, file.content)
                    return file.name
                });

                setFiles(files)

                setInitializer(true)
            })

        return () => {
            if (editorView) {
                editorView.destroy()
            }
        }

    }, []);

    return (
        <div>
            <div ref={editorViewRef}></div>
            <input type={"text"} value={newFileName} onChange={(event) => setNewFileName(event.target.value)}/>
            <button onClick={() => createNewFile()}>Create New File</button>
            <button onClick={() => transpileHandler()}>Transpile</button>
            <div>
                {
                    files.map(file => (
                        <p><a key={file.name} onClick={() => activeFileHandler(file)}>{file.name}</a></p>
                    ))
                }
            </div>
        </div>
    )
};

namespace CodeMirror {
    export interface Attributes {
        name?: string
        value?: string
        standalone?: boolean
        onChange?: (value: string) => void
        onModel?: (value: Model) => void
        loadAllFiles() : Promise<any>
    }

    export interface FileEntry {
        name: string;
        content: string;
    }
}

export default CodeMirror;