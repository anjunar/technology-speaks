import React, {CSSProperties} from "react"

function Pages(properties : Pages.Attributes) {

    const {children, page, rendered = true, ...rest} = properties

    if (rendered) {
        return <div {...rest}>{children[page]}</div>
    } else {
        return <div {...rest}>{
            children.map((child, index) => (
                <div key={index} style={{height : "100%", display : index === page ? "block" : "none"}}>{child}</div>
            ))
        }</div>
    }
}

namespace Pages {
    export interface Attributes {
        children : React.ReactElement[]
        page : number
        style? : CSSProperties,
        rendered? : boolean
    }
}

export default Pages
