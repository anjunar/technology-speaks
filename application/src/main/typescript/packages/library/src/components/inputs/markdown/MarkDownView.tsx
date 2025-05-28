import "./MarkDownView.css"
import React, {CSSProperties, useEffect, useMemo, useRef} from "react"
import {reMarkFactoryForHTML} from "./parser/ReMarkFactory";
import {useInput} from "../../../hooks";
import EditorModel from "./model/EditorModel";

function MarkDownView(properties: MarkDownView.Attributes) {

    const {style, value, standalone, name} = properties

    const viewRef = useRef<HTMLDivElement>(null);

    const [model, state, setState] = useInput<EditorModel>(name, value, standalone, "editor")

    const reMarkForHTML = useMemo(() => {
        return reMarkFactoryForHTML(state)
    }, []);

    useEffect(() => {
        if (state?.ast) {
            reMarkForHTML.run(state.ast)
                .then((tree: any) => reMarkForHTML
                    .stringify(tree)
                )
                .then(html => viewRef.current.innerHTML = html)
        }
    }, [state]);

    return (
        <div ref={viewRef} className={"mark-down-view"} style={style}></div>
    )
}

namespace MarkDownView {
    export interface Attributes {
        style?: CSSProperties
        name?: string
        standalone?: boolean
        value?: EditorModel
    }
}

export default MarkDownView