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
import {AbstractCodeMirrorFile} from "./domain/AbstractCodeMirrorFile";
import {JSONDeserializer} from "../../../mapper";
import {match} from "../../../pattern-match";
import {CodeMirrorTS} from "./domain/CodeMirrorTS";
import {CodeMirrorImage} from "./domain/CodeMirrorImage";
import {CodeMirrorCSS} from "./domain/CodeMirrorCSS";
import {CodeMirrorHTML} from "./domain/CodeMirrorHTML";
import {CodeMirrorWorkspace} from "./domain/CodeMirrorWorkspace";

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

function fileName(editor: AbstractCodeMirrorFile) {
    let lastIndexOf = editor.name.lastIndexOf("/");
    return editor.name.substring(lastIndexOf + 1);
}

export function CodeMirror(properties: CodeMirror.Attributes) {

    const {name, standalone, value, onChange, onModel, configuration, style} = properties

    const [model, state, setState] = useInput<CodeMirrorWorkspace>(name, value, standalone, "codemirror")

    const [files, setFiles] = useState<AbstractCodeMirrorFile[]>(null)

    const [page, setPage] = useState(0)

    const [newPathName, setNewPathName] = useState("/")

    const [newFileName, setNewFileName] = useState("newfile")

    const [drawerOpen, setDrawerOpen] = useState(false)

    const [createFileOpen, setCreateFileOpen] = useState(false)

    const formContext = useContext(FormContext)

    const fileService = new FileService(configuration, system, env)

    function closeEditor(page : number) {
        state.open.splice(page, 1)

        configuration.saveWorkspace()

        if (onChange) {
            onChange(state)
        }
    }

    const commands: FileManager.Commands = {
        onRead(file: FileManager.TreeNode) {
            let entry = state.open.find(fileEntry => fileEntry.name === file.fullName);

            if (entry) {
                let indexOf = state.open.indexOf(entry);
                setPage(indexOf)
            } else {
                let fromFiles = files.find(fileEntry => fileEntry.name === file.fullName);

                state.open.push(fromFiles)

                if (onChange) {
                    onChange(state)
                }
            }

            configuration.saveWorkspace()

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

    function createNewFile(type: string) {
        let content = fileTemplate(type)
        let name = newPathName + newFileName + "." + type;
        const newFile = JSONDeserializer<AbstractCodeMirrorFile>({name: name, content: content, $type: "CodeMirror" + type.toUpperCase()}, false);

        setFiles(prevFiles => [...prevFiles, newFile]);

        fileService.updateFile(newFile)

        state.open.push(newFile)

        if (onChange) {
            onChange(state)
        }

        setCreateFileOpen(false)

    }

    function transpileHandler() {
        files.filter(file => file.name.endsWith(".tsx") || file.name.endsWith(".ts"))
            .forEach(file => {
                transpile(file.name, (js, sourceMap) => {
                    let sourceFile = env.getSourceFile(file.name);
                    configuration.updateFile(JSONDeserializer({ $type : file.$type, id : file.id, name : file.name, content : sourceFile.text, transpiled: js, sourceMap}, false))
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
            .then((files: AbstractCodeMirrorFile[]) => {
                let fileNames = files.map(file => {

                    match(file)
                        .withObject(CodeMirrorTS, ts => {
                            env.createFile(ts.name, ts.content || "// Comment")
                        })
                        .withObject(CodeMirrorCSS, css => {
                            env.createFile(css.name, css.content)
                        })
                        .withObject(CodeMirrorHTML, html => {
                            env.createFile(html.name, html.content)
                        })

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
                        <FileManager files={files || []} commands={commands}></FileManager>
                    </div>
                </Drawer>
                <Drawer.Content>
                    <div style={{height: "100%"}}>
                        <Tabs page={page} onPage={page => setPage(page)} centered={false}>
                            {
                                files && state.open.map((editor, index) => (
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
                                files && state.open.filter(editor => editor instanceof CodeMirrorTS || editor instanceof CodeMirrorHTML || editor instanceof CodeMirrorCSS)
                                    .map(editor => (
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
        value?: CodeMirrorWorkspace
        standalone?: boolean
        onChange?: (value: CodeMirrorWorkspace) => void
        onModel?: (value: Model) => void
        configuration: Configuration
        style?: CSSProperties
    }

    export interface Configuration {
        loadAllFiles(): Promise<any>

        updateFile(name: AbstractCodeMirrorFile): Promise<Response>

        deleteFile(name: string): Promise<Response>

        renameFile(oldName: string, newName: string): Promise<Response>

        saveWorkspace(): Promise<Response>
    }

}

export default CodeMirror;