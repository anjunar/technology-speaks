import React from "react"
import Confirmation from "../../domain/user/Confirmation";
import {Button, SchemaForm, SchemaInput, useForm} from "react-ui-simplicity";

function ConfirmationPage(properties: ConfirmationPage.Attributes) {

    const {form} = properties
    
    const domain = useForm(form)

    return (
        <div className={"confirmation-page"} style={{display : "flex", justifyContent : "center", alignItems : "center", height : "100%"}}>
            <SchemaForm value={domain}>
                <SchemaInput name={"code"}/>
                <Button name={"confirm"}>Confirm</Button>
                {
                    Object.entries(domain.$links).map(([key, link]) => (
                        <Button key={key} name={key}>Resend to {key.split(":")[1]}</Button>
                    ))
                }
            </SchemaForm>
        </div>
    )
}

namespace ConfirmationPage {
    export interface Attributes {
        form : Confirmation
    }
}

export default ConfirmationPage