import './CodeMirrorPage.css'
import React, {useContext, useEffect, useMemo, useRef, useState} from 'react';
import {CodeMirror, SystemContext} from "react-ui-simplicity";

export function CodeMirrorPage(properties: CodeMirrorPage.Attributes) {

    const {} = properties

    const iframeRef = useRef<HTMLIFrameElement>(null);

    const [editor, setEditor] = useState<CodeMirror.FileEntry>(null)

    async function loadAllFiles() {
        try {
            const response = await fetch("/service/codemirror/files");
            if (!response.ok) throw new Error("Fehler beim Laden der Dateien");

            const files = await response.json();

            console.log("Alle Dateien geladen.");

            return files
        } catch (err) {
            console.error("Fehler beim Laden:", err);
        }
    }

    async function updateFile(name: string, content: string, transpiled : string, sourceMap : string) {
        return await fetch("/service/codemirror/files", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, content, transpiled, sourceMap })
        });
    }

    return (
        <div className={"codemirror-page"}>
            <CodeMirror style={{height : "50%"}} configuration={{loadAllFiles, updateFile}} value={editor} onChange={file => setEditor(file)}/>
            <iframe sandbox="allow-scripts allow-same-origin" src={"http://anjunar.localhost:3000"} style={{width: "100%", height: "50%"}}/>
        </div>


    )
}

namespace CodeMirrorPage {
    export interface Attributes {
    }
}

export default CodeMirrorPage;