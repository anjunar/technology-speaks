import "./MarkDown.css"
import React, {CSSProperties, RefObject, useEffect, useMemo, useRef, useState} from "react"
import Toolbar from "./ui/Toolbar";
import Footer from "./ui/Footer";
import EditorModel = MarkDown.EditorModel;
import {encodeBase64, findNodesByRange, reMarkFactoryForHTML, reMarkFactoryForMarkDown} from "./parser/ReMarkFactory";
import type { Root } from 'mdast';
import { Node } from 'unist';


export const MarkDownContext = React.createContext<MarkDown.Context>(null)

function MarkDown(properties: MarkDown.Attributes) {

    const {style} = properties

    const textAreaRef = useRef<HTMLTextAreaElement>(null);

    const viewRef = useRef<HTMLDivElement>(null);

    const [page, setPage] = useState(0)

    const [astUpdate, setAstUpdate] = useState(false)

    const [model, setModel] = useState<EditorModel>({
        store: {
            files: []
        },
        ast: null
    })

    const [text, setText] = useState('**test**');

    const [cursor, setCursor] = useState<Node[]>(null)

    const reMarkForHTML = useMemo(() => {
        return reMarkFactoryForHTML(model)
    }, []);

    const reMarkForMarkDown = useMemo(() => {
        return reMarkFactoryForMarkDown(model)
    }, []);

    function onStoreClick(file: MarkDown.File) {
        let textArea = textAreaRef.current;

        let selectionStart = textArea.selectionStart;
        let selectionEnd = textArea.selectionEnd

        let pre = textArea.value.substring(0, selectionStart);
        let post = textArea.value.substring(selectionEnd);

        textArea.value = `${pre}![Picture](${file.name})${post}`

        const event = new Event('input', { bubbles: true, cancelable: true});

        textArea.dispatchEvent(event);
    }

    function onSelect() {
        let textArea = textAreaRef.current;

        const nodes = findNodesByRange(model.ast, textArea.selectionStart, textArea.selectionEnd);

        setCursor(nodes.filter(node => node.type !== "root"))
    }

    useEffect(() => {
        let ast = reMarkForHTML.parse(text);

        setModel({
            store: model.store,
            ast: ast
        })

    }, [text]);

    useEffect(() => {

        if (model.ast) {
            reMarkForHTML.run(model.ast)
                .then((tree : any) => reMarkForHTML
                    .stringify(tree)
                )
                .then(html => viewRef.current.innerHTML = html)
        }

    }, [model]);

    useEffect(() => {
        if (model.ast) {
            let markDown : string = reMarkForMarkDown.stringify(model.ast);

            if (markDown !== text) {
                setText(markDown)
            }
        }
    }, [astUpdate]);

    return (
        <div className={"markdown-editor"} style={style}>
            <MarkDownContext.Provider value={{model: model, textAreaRef, cursor, updateAST() { setAstUpdate(! astUpdate) }}}>
                <Toolbar page={page} onPage={value => setPage(value)}/>
                <textarea onSelect={onSelect} ref={textAreaRef} onInput={(event: any) => setText(event.target.value)} value={text} className={"content"}></textarea>
                <div>
                    {
                        model.store.files.map(file => <img key={file.name} title={file.name} src={encodeBase64(file.type, file.subType, file.data)} style={{height: "32px"}} onClick={() => onStoreClick(file)}/>)
                    }
                </div>
                <div ref={viewRef} className={"view"}></div>
                <Footer page={page} onPage={(value) => setPage(value)}/>
            </MarkDownContext.Provider>
        </div>
    )
}

namespace MarkDown {
    export interface Attributes {
        style?: CSSProperties
    }

    export interface EditorModel {
        store: FileStore
        ast: Root
    }

    export interface File {
        name: string,
        type: string
        subType: string,
        lastModified: number
        data: string
    }

    export interface FileStore {
        files: File[]
    }

    export interface Context {
        model: EditorModel
        textAreaRef: RefObject<HTMLTextAreaElement>
        cursor: Node[]
        updateAST() : void
    }
}

export default MarkDown