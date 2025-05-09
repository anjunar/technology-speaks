import React from "react"
import {Button, FormSchemaFactory, SchemaForm, useForm} from "react-ui-simplicity";

function Search(properties: Search.Attributes) {

    const {value, submit} = properties

    let domain = useForm(value);

    let fields = Object.entries(domain.$descriptors.properties).map(([key, descriptor]) => (
        <div key={key} style={{display: "flex", alignItems: "center"}}>
            <FormSchemaFactory style={{flex: 1}} name={key}/>
        </div>
    ))

    function onSubmit(name: string, form: any) {
        submit(domain)
    }

    return (
        <div>
            <SchemaForm value={domain} onSubmit={onSubmit}>
                {
                    fields
                }
                <Button name={"search"}>Send</Button>
            </SchemaForm>
        </div>
    )
}

namespace Search {
    export interface Attributes {
        value: any
        submit : (form : any) => void
    }
}

export default Search
