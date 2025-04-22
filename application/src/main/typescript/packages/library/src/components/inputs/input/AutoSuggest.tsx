import "./AutoSuggest.css"
import React, {CSSProperties, useState} from "react";
import Input from "./Input";
import {useInput} from "../../../hooks/UseInputHook";

function AutoSuggest(properties : AutoSuggest.Attributes) {

    const {name, children, dynamicWidth, style, autoSuggest, value, standalone} = properties

    const [open, setOpen] = useState(false)

    const [model, state, setState] = useInput(name, value, standalone, "text")

    function onFocus() {
        setOpen(true)
    }

    function onBlur() {
        setTimeout(() => {
            setOpen(false)
        }, 300)
    }

    return (
        <div className={"auto-suggest"} style={style}>
            <Input type={"text"} dynamicWidth={dynamicWidth}
                   name={name} onChange={(value : any) => setState(value)}
                   standalone={true} onKeyDown={event => event.stopPropagation()}
                   value={state} onFocus={onFocus} onBlur={onBlur}/>
            {
                open ? (
                    <div className={"overlay"}>
                        {
                            autoSuggest.loader(state.toString()).map(item => (
                                <div key={autoSuggest.extractor(item)} className={"item"} onClick={() => setState(autoSuggest.extractor(item))}>
                                    {children(item)}
                                </div>
                            ))
                        }
                    </div>
                ) : ""
            }
        </div>
    )
}

namespace AutoSuggest {
    export interface Attributes {
        autoSuggest : {loader : (value : string) => any[], extractor : (value : any) => string}
        children : (element : any) => React.ReactElement
        dynamicWidth : boolean
        name : string
        standalone? : boolean
        value? : string
        style : CSSProperties
    }
}

export default AutoSuggest