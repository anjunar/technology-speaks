import "./DocumentFormPage.css"
import React from "react"
import Document from "../../../domain/document/Document";
import {MarkDownEditor, MarkDownView, SchemaForm, SchemaInput, useForm} from "react-ui-simplicity";

function DocumentFormPage(properties: DocumentFormPage.Attributes) {

    const {form} = properties

    let document = useForm(form);

    async function onSubmit(name: string, form: any) {

    }

    return (
        <div className={"document-form-page"}>
            <SchemaForm value={document} onSubmit={onSubmit} style={{height : "calc(100% - 48px)"}}>
                <SchemaInput name={"title"}/>
                <div style={{display : "flex", height : "100%"}}>
                    <MarkDownEditor name={"editor"} style={{flex : 1, height : "100%"}}/>
                    <MarkDownView name={"editor"} style={{flex : 1, height : "100%"}}/>
                </div>
            </SchemaForm>
        </div>
    )
}

namespace DocumentFormPage {
    export interface Attributes {
        form : Document
    }
}

export default DocumentFormPage