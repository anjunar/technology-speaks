import './CodeMirrorPage.css'
import React, {useState} from 'react';
import {CodeMirror} from "react-ui-simplicity";

export function CodeMirrorPage(properties: CodeMirrorPage.Attributes) {

    const {} = properties

    const [editor, setEditor] = useState<CodeMirror.FileEntry[]>([])

    async function loadAllFiles() {
        try {
            const response = await fetch("/service/codemirror/anjunar/files");
            if (!response.ok) throw new Error("Fehler beim Laden der Dateien");
            return await response.json()
        } catch (err) {
            console.error("Fehler beim Laden:", err);
        }
    }

    async function updateFile(file : CodeMirror.FileEntry) {
        return await fetch("/service/codemirror/anjunar/files/file", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(file)
        });
    }

    async function deleteFile(name: string) {
        return await fetch(`/service/codemirror/anjunar/files/file/${name}`, {method: "DELETE"});
    }

    async function renameFile(oldName: string, newName: string) {
        return await fetch(`/service/codemirror/anjunar/files/file/${oldName}?newName=${newName}`, {method: "PATCH"});
    }

    return (
        <div className={"codemirror-page"}>
            <CodeMirror style={{height: "50%"}} configuration={{loadAllFiles, updateFile, deleteFile, renameFile}}
                        value={editor} onChange={file => setEditor(file)}/>
            <iframe sandbox={"allow-scripts allow-same-origin"} src={`https://patrick.anjunar.com`}
                    style={{width: "100%", height: "50%"}}/>
        </div>
    )
}

namespace CodeMirrorPage {
    export interface Attributes {
    }
}

export default CodeMirrorPage;