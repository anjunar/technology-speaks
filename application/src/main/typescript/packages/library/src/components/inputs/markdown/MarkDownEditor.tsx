import "./MarkDownEditor.css"
import React, {CSSProperties, RefObject, useEffect, useLayoutEffect, useMemo, useRef, useState} from "react"
import Toolbar from "./ui/Toolbar";
import Footer from "./ui/Footer";
import {encodeBase64, findNodesByRange, reMarkFactoryForHTML, reMarkFactoryForMarkDown} from "./parser/ReMarkFactory";
import {Node} from 'unist';
import {useInput} from "../../../hooks";
import {Model} from "../../shared";
import EditorModel from "./model/EditorModel";
import EditorFile from "./model/EditorFile";


export const MarkDownContext = React.createContext<MarkDownEditor.Context>(null)

function MarkDownEditor(properties: MarkDownEditor.Attributes) {

    const {style, value, standalone, name, onModel, onChange} = properties

    const textAreaRef = useRef<HTMLTextAreaElement>(null);

    const [page, setPage] = useState(0)

    const [astUpdate, setAstUpdate] = useState(false)

    const [model, state, setState] = useInput<EditorModel>(name, value, standalone, "editor")

    const reMarkForHTML = useMemo(() => {
        return reMarkFactoryForHTML(state)
    }, []);

    const reMarkForMarkDown = useMemo(() => {
        return reMarkFactoryForMarkDown(state)
    }, []);

    const [text, setText] = useState(() => {
        if (state.ast) {
            return reMarkForMarkDown.stringify(state.ast)
        }
        return ""
    });

    const [cursor, setCursor] = useState<Node[]>(null)

    function onStoreClick(file: EditorFile) {
        let textArea = textAreaRef.current;

        let selectionStart = textArea.selectionStart;
        let selectionEnd = textArea.selectionEnd

        let pre = textArea.value.substring(0, selectionStart);
        let post = textArea.value.substring(selectionEnd);

        textArea.value = `${pre}![Picture](${file.name})${post}`

        const event = new Event('input', {bubbles: true, cancelable: true});

        textArea.dispatchEvent(event);
    }

    function onSelect() {
        let textArea = textAreaRef.current;

        const nodes = findNodesByRange(state.ast, textArea.selectionStart, textArea.selectionEnd);

        setCursor(nodes.filter(node => node.type !== "root"))
    }

    useEffect(() => {
        let ast = reMarkForHTML.parse(text);

        const editor = new EditorModel();
        editor.files = state?.files || [];
        editor.ast = ast;

        setState(editor)

        if (onChange) {
            onChange(state)
        }

        if (onModel) {
            onModel(model)
        }


    }, [text]);

    useEffect(() => {
        if (state?.ast) {
            let markDown: string = reMarkForMarkDown.stringify(state.ast);

            if (markDown !== text) {
                setText(markDown)
            }
        }
    }, [astUpdate]);

    useEffect(() => {
        // For form validation -> Error messages
        model.callbacks.push((validate: boolean) => {
            if (onModel) {
                onModel(model)
            }
        })

        if (onModel) {
            onModel(model)
        }
    }, []);

    useLayoutEffect(() => {
        if (onModel) {
            onModel(model)
        }
    }, [value, model.dirty]);

    return (
        <div className={"markdown-editor"} style={style}>
            <MarkDownContext.Provider value={{
                model: state, textAreaRef, cursor, updateAST() {
                    setAstUpdate(!astUpdate)
                }
            }}>
                <Toolbar page={page} onPage={value => setPage(value)}/>
                <textarea onSelect={onSelect} ref={textAreaRef} onInput={(event: any) => setText(event.target.value)} value={text} className={"content"}></textarea>
                <div>
                    {
                        state?.files?.map(file => <img key={file.name} title={file.name} src={encodeBase64(file.type, file.subType, file.data)} style={{height: "32px"}} onClick={() => onStoreClick(file)}/>)
                    }
                </div>
                <Footer page={page} onPage={(value) => setPage(value)}/>
            </MarkDownContext.Provider>
        </div>
    )
}

namespace MarkDownEditor {
    export interface Attributes {
        style?: CSSProperties
        name?: string
        standalone?: boolean
        value?: EditorModel
        onChange?: (value: EditorModel) => void
        onModel?: (value: Model) => void
    }

    export interface Context {
        model: EditorModel
        textAreaRef: RefObject<HTMLTextAreaElement>
        cursor: Node[]

        updateAST(): void
    }
}

export default MarkDownEditor