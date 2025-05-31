import React, {useState} from "react"
import {mapForm, NodeDescriptor, useForm, Window} from "react-ui-simplicity";
import {createPortal} from "react-dom";
import ManagedProperty from "../../domain/shared/ManagedProperty";
import SecuredPropertyForm from "./SecuredPropertyForm";

function SecuredProperty(properties: SecuredProperty.Attributes) {

    const {descriptor} = properties

    const [form, setForm] = useState(null)

    async function openWindow() {
        let link = descriptor.links["secured"];
        let response = await fetch(`/service${link.url}`)
        let json = await response.json()
        setForm(mapForm<ManagedProperty>(json))
    }

    function closeWindow() {
        setForm(null)
    }

    return (
        <div>
            {
                form && createPortal(
                    <Window centered={true} style={{width : "250px", height : "200px"}}>
                        <Window.Header>
                            <div style={{display : "flex", justifyContent : "flex-end"}}>
                                <button className={"material-icons"} onClick={() => closeWindow()}>close</button>
                            </div>
                        </Window.Header>
                        <Window.Content>
                            <div>
                                <SecuredPropertyForm form={form} onClose={closeWindow}/>
                            </div>
                        </Window.Content>
                    </Window>,
                document.getElementById("viewport"))
            }
            <button className={"material-icons"} onClick={() => openWindow()}>settings</button>
        </div>
    )
}

namespace SecuredProperty {
    export interface Attributes {
        descriptor : NodeDescriptor
    }
}

export default SecuredProperty