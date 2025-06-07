import "./DocumentFormPage.css"
import React, {useRef, useState} from "react"
import Document from "../../../domain/document/Document";
import {
    Button,
    FormModel,
    JSONSerializer,
    MarkDownEditor,
    MarkDownView,
    Router,
    SchemaForm,
    SchemaInput,
    useForm,
    Window
} from "react-ui-simplicity";
import {process} from "../../../App";
import {createPortal} from "react-dom";
import navigate = Router.navigate;

function DocumentFormPage(properties: DocumentFormPage.Attributes) {

    const {form} = properties

    const domain = useForm(form);

    const [open, setOpen] = useState(false)

    const [buffer, setBuffer] = useState("")

    const scrollRef = useRef<HTMLDivElement>(null);

    async function onSubmit(name: string, form: FormModel) {
        let link = domain.$links[name];

        const response = await fetch("/service" + link.url, {
            body: JSON.stringify(JSONSerializer(domain)),
            headers: {"content-type": "application/json"},
            method: link.method
        })

        if (response.ok) {
            setOpen(true)

            let eventSource = new EventSource(`/service/documents/document/${domain.id}/batch`);

            eventSource.onmessage = (e) => {
                setBuffer((prev) => {
                    const next = prev + e.data;

                    requestAnimationFrame(() => {
                        if (scrollRef.current) {
                            scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
                        }
                    });

                    return next;
                });

                if (e.data === "Done") {
                    eventSource.close()
                    setOpen(false)
                    navigate("/documents/search")
                }
            };
        } else {
            if (response.status === 403) {
                process(response)
            } else {
                let errors = await response.json()
                form.setErrors(errors)
            }
        }
    }

    let actions = Object.values(domain.$links)
        .filter((link) => link.method !== "GET")
        .map((link) => <Button key={link.rel} name={link.rel}>{link.title}</Button>)

    return (
        <div className={"document-form-page"}>
            {
                open && createPortal((
                    <Window centered={true} style={{width: "70%", height: "50vh"}}>
                        <Window.Header>
                            Server
                        </Window.Header>
                        <Window.Content>
                            <div ref={scrollRef} style={{overflowY: "auto", padding: "20px", height: "calc(50vh - 90px)"}}>
                                <p>
                                    {buffer}
                                </p>
                            </div>
                        </Window.Content>
                    </Window>
                ), document.getElementById("viewport"))
            }
            <SchemaForm value={domain} onSubmit={onSubmit}
                        style={{display: "flex", height: "calc(100% - 70px)", flexDirection: "column"}}>
                <SchemaInput name={"title"}/>
                <div style={{flex: 1, display: "flex", height: "100%"}}>
                    <MarkDownEditor name={"editor"} style={{flex: 1, height: "100%"}}/>
                    <MarkDownView name={"editor"} style={{flex: 1, height: "100%"}}/>
                </div>
                <div style={{display: "flex", justifyContent: "flex-end"}}>{actions}</div>
            </SchemaForm>
        </div>
    )
}

namespace DocumentFormPage {
    export interface Attributes {
        form: Document
    }
}

export default DocumentFormPage