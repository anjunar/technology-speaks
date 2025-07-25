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
import {CodeMirrorCSS} from "./domain/CodeMirrorCSS";
import {CodeMirrorHTML} from "./domain/CodeMirrorHTML";
import {CodeMirrorWorkspace} from "./domain/CodeMirrorWorkspace";
import VersionControl from "./ui/VersionControl";
import CodeMirrorTag from "./domain/CodeMirrorTag";

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

    const {name, standalone, value, onChange, onModel, configuration, style, vcr} = properties

    const [model, state, setState] = useInput<CodeMirrorWorkspace>(name, value, standalone, "codemirror")

    const [files, setFiles] = useState<AbstractCodeMirrorFile[]>(null)

    const [tags, setTags] = useState<CodeMirrorTag[]>([])

    const [page, setPage] = useState(0)

    const [newPathName, setNewPathName] = useState("/")

    const [newFileName, setNewFileName] = useState("newfile")

    const [drawerOpen, setDrawerOpen] = useState("close")

    const [createFileOpen, setCreateFileOpen] = useState(false)

    const formContext = useContext(FormContext)

    const fileService = new FileService(configuration, system, env)

    function closeEditor(page: number) {
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
        const newFile = JSONDeserializer<AbstractCodeMirrorFile>({
            name: name,
            content: content,
            $type: "CodeMirror" + type.toUpperCase().replace("TSX", "TS")
        }, false);

        setFiles(prevFiles => [...prevFiles, newFile]);

        fileService.createFile(newFile)

        state.open.push(newFile)

        if (onChange) {
            onChange(state)
        }

        setCreateFileOpen(false)

    }

    function transpileHandler() {
        const filtered = files.filter(file => file instanceof CodeMirrorTS)

        filtered.forEach(file => {
            transpile(file.name, (js, sourceMap) => {
                file.transpiled = js
                file.sourceMap = sourceMap
            })
        })

        fileService.bulk(filtered)

    }

    useEffect(() => {

        if (formContext) {
            if (onModel) {
                onModel(model)
            }
        }

        configuration.loadAllFiles()
            .then((files: AbstractCodeMirrorFile[]) => {
                files.forEach(file => {

                    match(file)
                        .withObject(CodeMirrorTS, ts => {
                            env.createFile(ts.name, ts.content || "// Comment")
                        })
                        .withObject(CodeMirrorCSS, css => {
                            system.writeFile(css.name, css.content)
                        })
                        .withObject(CodeMirrorHTML, html => {
                            system.writeFile(html.name, html.content)
                        })

                    return file.name
                });

                setFiles(files)
            })

        vcr.loadAll()
            .then(tags => {
                setTags(tags)
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
                        onClick={() => setDrawerOpen(drawerOpen === "fileManager" ? "close" : "fileManager")}>folder_open</button>
                <button className={"material-icons"} title={"Version Control"}
                        onClick={() => setDrawerOpen(drawerOpen === "versionControl" ? "close" : "versionControl")}>deployed_code_history</button>
                <button className={"material-icons"} title={"Transpile"} onClick={() => transpileHandler()}>modeling
                </button>
            </div>
            <Drawer.Container>
                <Drawer open={drawerOpen !== "close"}>
                    <div style={{padding: "16px"}}>
                        {
                            drawerOpen === "fileManager" && (<FileManager files={files || []} commands={commands}></FileManager>)
                        }
                        {
                            drawerOpen === "versionControl" && (<VersionControl tags={tags}/>)
                        }
                    </div>
                </Drawer>
                <Drawer.Content>
                    <div style={{height: "100%"}}>
                        <Tabs page={page} onPage={page => setPage(page)} centered={false}>
                            {
                                files && state.open.map((editor, index) => (
                                    <Tab>
                                        <div style={{display: "flex", alignItems: "center", gap: "12px"}}>
                                            <span>{fileName(editor)}</span>
                                            <button onClick={() => closeEditor(index)} className={"material-icons"}
                                                    style={{fontSize: "16px"}}>close
                                            </button>
                                        </div>
                                    </Tab>
                                ))
                            }
                        </Tabs>
                        <Pages page={page} style={{height: "100%"}}>
                            {
                                files && state.open.filter(editor => editor instanceof CodeMirrorTS || editor instanceof CodeMirrorHTML || editor instanceof CodeMirrorCSS)
                                    .map(editor => (
                                        <Page style={{height: "100%"}}>
                                            <Editor style={{height: "100%"}} configuration={configuration} value={editor}/>
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
        vcr : VCR
        style?: CSSProperties
    }

    export interface VCR {
        loadAll() : Promise<any>

        save(name : string) : Promise<any>
        
        load(name : string) : Promise<any>
    }

    export interface Configuration {

        bulk(files: AbstractCodeMirrorFile[]): Promise<any>

        loadAllFiles(): Promise<any>

        updateFile(name: AbstractCodeMirrorFile): Promise<Response>

        deleteFile(name: string): Promise<Response>

        renameFile(oldName: string, newName: string): Promise<Response>

        saveWorkspace(): Promise<Response>
    }

}

export default CodeMirror;