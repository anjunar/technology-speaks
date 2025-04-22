import React, {CSSProperties} from "react"

function Page(properties : Page.Attributes) {

    const {children, ...rest} = properties

    return <div {...rest}>{children}</div>
}

namespace Page {

    export interface Attributes {
        children : React.ReactNode
        style? : CSSProperties
    }

}

export default Page