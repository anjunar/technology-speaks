import React, {useState} from "react"
import {NodeDescriptor, Window} from "react-ui-simplicity";
import {createPortal} from "react-dom";

function SecuredProperty(properties: ManagedProperty.Attributes) {

    const {descriptor} = properties

    const [open, setOpen] = useState(false)

    return (
        <div>
            {
                open && createPortal(
                    <Window centered={true}>
                        <Window.Header>
                            <div style={{display : "flex", justifyContent : "flex-end"}}>
                                <button className={"material-icons"} onClick={() => setOpen(false)}>close</button>
                            </div>
                        </Window.Header>
                        <Window.Content>
                            <div>
                                Test
                            </div>
                        </Window.Content>
                    </Window>,
                document.getElementById("viewport"))
            }
            <button className={"material-icons"} onClick={() => setOpen(! open)}>settings</button>
        </div>
    )
}

namespace ManagedProperty {
    export interface Attributes {
        descriptor : NodeDescriptor
    }
}

export default SecuredProperty