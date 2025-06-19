import "./CodeMirror.css"
import React, {CSSProperties, useContext, useEffect, useMemo, useRef, useState} from 'react';
import {basicSetup, EditorView} from "codemirror"
import {css} from "@codemirror/lang-css"
import {html} from "@codemirror/lang-html"
import {javascript} from "@codemirror/lang-javascript"
import {EditorState, Extension} from "@codemirror/state"
import {autocompletion} from "@codemirror/autocomplete";
import {
    closeTooltipOnClick,
    diagnosticsField,
    diagnosticsPlugin,
    requestDiagnosticsUpdate,
    tsErrorHighlighter
} from "./extensions/Diagnostics";
import {multiLanguageCompletion, typescriptCompletionSource} from "./extensions/AutoComplete";
import {env, system, transpile} from "./typescript/Environment";
import {fileNameFacet} from "./extensions/FileName";
import {useInput} from "../../../hooks";
import {Model} from "../../shared";
import {FormContext} from "../form/Form";
import {dracula} from "@uiw/codemirror-theme-dracula"
import {material} from "@uiw/codemirror-theme-material"
import {SystemContext} from "../../../System";
import {RequestInformation} from "technology-speaks/src/request";
import Drawer from "../../layout/drawer/Drawer";
import {createPortal} from "react-dom";
import Window from "../../modal/window/Window";
import FileManager from "../../navigation/files/FileManager";
import {FileService} from "./service/FileService";

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
        info.cookie.theme === "dark" ? dracula : material
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

export function CodeMirror(properties: CodeMirror.Attributes) {

    const {name, standalone, value, onChange, onModel, configuration, style} = properties

    const editorViewRef = useRef<HTMLDivElement>(null);

    const [files, setFiles] = useState<CodeMirror.FileEntry[]>([{name: "/index.tsx", content: "", $type : "CodeMirrorHTML"}])

    const [newPathName, setNewPathName] = useState("/")

    const [newFileName, setNewFileName] = useState("newfile")

    const [drawerOpen, setDrawerOpen] = useState(false)

    const [createFileOpen, setCreateFileOpen] = useState(false)

    const [model, state, setState] = useInput<CodeMirror.FileEntry>(name, value, standalone, "codemirror")

    const formContext = useContext(FormContext)

    const {info} = useContext(SystemContext)

    const fileService = new FileService(configuration, system, env)

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

    const updateListener = EditorView.updateListener.of(async update => {
        if (update.docChanged) {
            const content = update.state.doc.toString();

            const response = fileService.updateFile({...state, content})

            let fileEntry = files.find(file => file.name === state.name);

            setState(fileEntry)

            if (onChange) {
                onChange(fileEntry)
            }

            if (fileEntry) {
                fileEntry.content = content;
            } else {
                setFiles([...files, {name: state.name, content, $type : state.$type}])
            }
        }
    });


    const editorView = useMemo(() => {
        if (editorViewRef.current === null) {
            return null;
        }

        return new EditorView({
            parent: editorViewRef.current
        });
    }, [editorViewRef?.current]);

    const commands: FileManager.Commands = {
        onRead(file: FileManager.TreeNode) {
            let entry = files.find(fileEntry => fileEntry.name === file.fullName);
            setState(entry)

            if (onChange) {
                onChange(entry)
            }
        },
        onCreate(path: string) {
            setCreateFileOpen(true)
            setNewPathName(path)
        },
        onRemove(fileName: string) {
            let findIndex = files.findIndex(file => file.name === fileName);
            files.splice(findIndex, 1)
            fileService.deleteFile(fileName)
        }
    }

    function fileTemplate(type : string) {
        switch (type) {
            case "ts" : return "import React from \"react\";"
            case "tsx" : return "import React from \"react\";\nimport ReactDom from \"react-dom/client\";"
            case "css" : return ""
            case "html" : return `<html lang="en">
    <head>
        <script type="module" src="index"></script>    
    </head>
    <body>
        <div id="root"></div>
    </body>
</html>`
        }
    }

    function createNewFile(type : string) {
        let content = fileTemplate(type)
        let name = newPathName + newFileName + "." + type;
        const newFile = {name: name, content: content, $type : "CodeMirror" + type.toUpperCase()};

        setFiles(prevFiles => [...prevFiles, newFile]);

        fileService.createFile(newFile)

        setState(newFile)

        if (onChange) {
            onChange(newFile)
        }

        setCreateFileOpen(false)

    }

    function transpileHandler() {
        files.filter(file => file.name.endsWith(".tsx") || file.name.endsWith(".ts"))
            .forEach(file => {
                transpile(file.name, (js, sourceMap) => {
                    configuration.updateFile({...file, transpiled : js, sourceMap})
                })
            })
    }

    useEffect(() => {
        if (state) {
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
    }, [state]);

    useEffect(() => {

        if (formContext) {
            if (onModel) {
                onModel(model)
            }
        }

        configuration.loadAllFiles()
            .then((files: CodeMirror.FileEntry[]) => {
                let fileNames = files.map(file => {
                    env.createFile(file.name, file.content)
                    return file.name
                });

                setFiles(files)

            })

        return () => {
            if (editorView) {
                editorView.destroy()
            }
        }

    }, []);

    return (
        <div className={"code-mirror"} style={style}>
            {
                createFileOpen && createPortal((
                    <Window centered={true} resizable={false} style={{zIndex: 9999}}>
                        <Window.Header>
                            <div style={{display: "flex", alignItems: "center", justifyContent: "space-between"}}>
                                <span>Create File</span>
                                <button type="button" className="material-icons"
                                        onClick={() => setCreateFileOpen(false)}>close
                                </button>
                            </div>
                        </Window.Header>
                        <Window.Content>
                            <input type={"text"} value={newFileName}
                                   onChange={(event) => setNewFileName(event.target.value)}/>
                            <div>
                                <button className={"hover"} onClick={() => createNewFile("ts")}>TS</button>
                                <button className={"hover"} onClick={() => createNewFile("tsx")}>TSX</button>
                                <button className={"hover"} onClick={() => createNewFile("html")}>HTML</button>
                                <button className={"hover"} onClick={() => createNewFile("css")}>CSS</button>
                            </div>
                        </Window.Content>
                    </Window>
                ), document.getElementById("viewport"))
            }
            <div className={"left-panel"}>
                <button className={"material-icons"} title={"Show Folders"} style={{marginTop: "5px"}}
                        onClick={() => setDrawerOpen(!drawerOpen)}>folder_open
                </button>
                <button className={"material-icons"} title={"Create File"}
                        onClick={() => setCreateFileOpen(true)}>docs_add_on
                </button>
                <button className={"material-icons"} title={"Transpile"} onClick={() => transpileHandler()}>modeling
                </button>
            </div>
            <Drawer.Container>
                <Drawer open={drawerOpen}>
                    <div style={{padding: "16px"}}>
                        <FileManager files={files} commands={commands}></FileManager>
                    </div>
                </Drawer>
                <Drawer.Content>
                    <div style={{height: "100%"}}>
                        <div className={"editor"} ref={editorViewRef}></div>
                    </div>
                </Drawer.Content>
            </Drawer.Container>
        </div>
    )
};

export namespace CodeMirror {
    export interface Attributes {
        name?: string
        value?: FileEntry
        standalone?: boolean
        onChange?: (value: FileEntry) => void
        onModel?: (value: Model) => void
        configuration: Configuration
        style?: CSSProperties
    }

    export interface Configuration {
        loadAllFiles(): Promise<any>

        updateFile(name: CodeMirror.FileEntry): Promise<Response>

        deleteFile(name: string): Promise<Response>

        renameFile(oldName: string, newName: string): Promise<Response>
    }

    export interface FileEntry {
        $type : string
        name: string;
        content: string;
        transpiled?: string;
        sourceMap?: string
        contentType? : string
    }
}

export default CodeMirror;