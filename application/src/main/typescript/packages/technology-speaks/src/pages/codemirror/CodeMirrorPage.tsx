import './CodeMirrorPage.css'
import React, {useState} from 'react';
import {CodeMirror, CodeMirrorWorkspace, JSONSerializer, mapTable, useForm} from "react-ui-simplicity";
import {
    AbstractCodeMirrorFile
} from "react-ui-simplicity";

export function CodeMirrorPage(properties: CodeMirrorPage.Attributes) {

    const {workspace} = properties

    const editor = useForm<CodeMirrorWorkspace>(workspace)

    async function bulk(files : AbstractCodeMirrorFile[]) {
        return await fetch("/service/codemirror/anjunar/head/files", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(JSONSerializer(files))
        });
    }

    async function loadAllFiles() {
        try {
            const response = await fetch("/service/codemirror/anjunar/head/files");
            if (!response.ok) throw new Error("Fehler beim Laden der Dateien");
            const [rows] = mapTable(await response.json())
            return rows
        } catch (err) {
            console.error("Fehler beim Laden:", err);
        }
    }

    async function updateFile(file : AbstractCodeMirrorFile) {
        return await fetch("/service/codemirror/anjunar/head/files/file", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(JSONSerializer(file))
        });
    }

    async function deleteFile(name: string) {
        return await fetch(`/service/codemirror/anjunar/head/files/file/${name}`, {method: "DELETE"});
    }

    async function renameFile(oldName: string, newName: string) {
        return await fetch(`/service/codemirror/anjunar/head/files/file/${oldName}?newName=${newName}`, {method: "PATCH"});
    }

    async function saveWorkspace() {
        return await fetch("/service/codemirror/workspace", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(JSONSerializer(editor))
        });
    }

    return (
        <div className={"codemirror-page"}>
            <CodeMirror style={{height: "50%"}} configuration={{loadAllFiles, updateFile, deleteFile, renameFile, saveWorkspace, bulk}}
                        value={editor}/>
            <iframe sandbox={"allow-scripts allow-same-origin"} src={`https://patrick.anjunar.com`}
                    style={{width: "100%", height: "50%"}}/>
        </div>
    )
}

namespace CodeMirrorPage {
    export interface Attributes {
        workspace : CodeMirrorWorkspace
    }
}

export default CodeMirrorPage;