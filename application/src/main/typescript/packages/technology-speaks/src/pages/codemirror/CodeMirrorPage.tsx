import './CodeMirrorPage.css'
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {CodeMirror} from "react-ui-simplicity";

export function CodeMirrorPage(properties: CodeMirrorPage.Attributes) {

    const {} = properties

    const iframeRef = useRef<HTMLIFrameElement>(null);

    function send() {
        iframeRef.current.srcdoc = `
        <html>
            <head>
                <script type="module">
                    import {} from "/service/codemirror/files/index";
                </script>
            </head>
            <body>
                <div id="root"></div>
            </body>
        </html>
        `
    }

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

    return (
        <div>
            <CodeMirror loadAllFiles={loadAllFiles}/>
            <button onClick={send}>send</button>
            <iframe ref={iframeRef} style={{width: "100%", height: "100%"}}/>
        </div>


    )
}

namespace CodeMirrorPage {
    export interface Attributes {
    }
}

export default CodeMirrorPage;