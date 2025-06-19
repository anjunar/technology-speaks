import "./CodeMirror.css"
import React, {CSSProperties, useContext, useEffect, useState} from 'react';
import {env, system, transpile} from "./typescript/Environment";
import {useInput} from "../../../hooks";
import {Model} from "../../shared";
import {FormContext} from "../form/Form";
import Drawer from "../../layout/drawer/Drawer";
import {createPortal} from "react-dom";
import Window from "../../modal/window/Window";
import FileManager from "../../navigation/files/FileManager";
import {FileService} from "./service/FileService";
import Editor from "./ui/Editor";
import Tabs from "../../layout/tabs/Tabs";
import Tab from "../../layout/tabs/Tab";
import Page from "../../layout/pages/Page";
import Pages from "../../layout/pages/Pages";

function fileName(editor: CodeMirror.FileEntry) {
    let lastIndexOf = editor.name.lastIndexOf("/");
    return editor.name.substring(lastIndexOf + 1);
}

export function CodeMirror(properties: CodeMirror.Attributes) {

    const {name, standalone, value, onChange, onModel, configuration, style} = properties

    const [model, state, setState] = useInput<CodeMirror.FileEntry[]>(name, value, standalone, "codemirror")

    const [files, setFiles] = useState<CodeMirror.FileEntry[]>([{
        name: "/index.tsx",
        content: "",
        $type: "CodeMirrorHTML"
    }])

    const [page, setPage] = useState(0)

    const [newPathName, setNewPathName] = useState("/")

    const [newFileName, setNewFileName] = useState("newfile")

    const [drawerOpen, setDrawerOpen] = useState(false)

    const [createFileOpen, setCreateFileOpen] = useState(false)

    const formContext = useContext(FormContext)

    const fileService = new FileService(configuration, system, env)

    function closeEditor(page : number) {
        state.splice(page, 1)
        let newState = [...state];
        setState(newState)

        if (onChange) {
            onChange(newState)
        }
    }

    const commands: FileManager.Commands = {
        onRead(file: FileManager.TreeNode) {
            let entry = state.find(fileEntry => fileEntry.name === file.fullName);

            if (entry) {
                let indexOf = state.indexOf(entry);
                setPage(indexOf)
            } else {
                let fromFiles = files.find(fileEntry => fileEntry.name === file.fullName);
                let newState = [fromFiles, ...state];
                setState(newState)

                if (onChange) {
                    onChange(newState)
                }
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

    function fileTemplate(type: string) {
        switch (type) {
            case "ts" :
                return "import React from \"react\";"
            case "tsx" :
                return "import React from \"react\";\nimport ReactDom from \"react-dom/client\";"
            case "css" :
                return ""
            case "html" :
                return `<html lang="en">
    <head>
        <script type="module" src="index"></script>    
    </head>
    <body>
        <div id="root"></div>
    </body>
</html>`
        }
    }

    function createNewFile(type: string) {
        let content = fileTemplate(type)
        let name = newPathName + newFileName + "." + type;
        const newFile = {name: name, content: content, $type: "CodeMirror" + type.toUpperCase()};

        setFiles(prevFiles => [...prevFiles, newFile]);

        fileService.createFile(newFile)

        let newState = [newFile, ...state];
        setState(newState)

        if (onChange) {
            onChange(newState)
        }

        setCreateFileOpen(false)

    }

    function transpileHandler() {
        files.filter(file => file.name.endsWith(".tsx") || file.name.endsWith(".ts"))
            .forEach(file => {
                transpile(file.name, (js, sourceMap) => {
                    configuration.updateFile({...file, transpiled: js, sourceMap})
                })
            })
    }

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
                <button className={"material-icons"} title={"Transpile"} onClick={() => transpileHandler()}>modeling</button>
            </div>
            <Drawer.Container>
                <Drawer open={drawerOpen}>
                    <div style={{padding: "16px"}}>
                        <FileManager files={files} commands={commands}></FileManager>
                    </div>
                </Drawer>
                <Drawer.Content>
                    <div style={{height: "100%"}}>
                        <Tabs page={page} onPage={page => setPage(page)} centered={false}>
                            {
                                state.map((editor, index) => (
                                    <Tab>
                                        <div style={{display : "flex", alignItems : "center", gap : "12px"}}>
                                            <span>{fileName(editor)}</span>
                                            <button onClick={() => closeEditor(index)} className={"material-icons"} style={{fontSize : "16px"}}>close</button>
                                        </div>
                                    </Tab>
                                ))
                            }
                        </Tabs>
                        <Pages page={page} style={{height : "100%"}}>
                            {
                                state.map(editor => (
                                    <Page style={{height : "100%"}}>
                                        <Editor style={{height : "100%"}} configuration={configuration} value={editor}/>
                                    </Page>
                                ))
                            }
                        </Pages>
                    </div>
                </Drawer.Content>
            </Drawer.Container>
        </div>
    )
};

export namespace CodeMirror {
    export interface Attributes {
        name?: string
        value?: FileEntry[]
        standalone?: boolean
        onChange?: (value: FileEntry[]) => void
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
        $type: string
        name: string;
        content: string;
        transpiled?: string;
        sourceMap?: string
        contentType?: string
    }
}

export default CodeMirror;