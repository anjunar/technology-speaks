import "./Tab.css"
import React, {useEffect, useState} from "react"

function Tab(properties: Tab.Attributes) {

    const {className, children, tab, selected, onClick, ...rest} = properties

    const [isSelected, setSelected] = useState(selected)

    function onClickHandler(event : React.MouseEvent) : void {
        event.preventDefault()

        tab.onSelect()

        if (onClick) {
            onClick()
        }

    }

    useEffect(() => {
        if (tab) {
            tab.addListener((selected : any) => {
                setSelected(selected)
            })
        }
    }, [])

    return (
        <div
            onMouseDown={onClickHandler}
            className={
                (className ? className + " " : "") +
                "tab" +
                (isSelected ? " selected" : "")
            }
            {...rest}
        >
            {children}
        </div>
    )
}

namespace Tab {
    export interface Attributes {
        className? : string
        children : React.ReactNode
        tab? : any
        selected? : boolean
        onClick? : () => void
    }
}

export default Tab