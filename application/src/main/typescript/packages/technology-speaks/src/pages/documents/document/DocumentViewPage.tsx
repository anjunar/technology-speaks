import "./DocumentViewPage.css"
import React from "react"
import Document from "../../../domain/document/Document";
import {MarkDownView, Router} from "react-ui-simplicity";
import navigate = Router.navigate;

function DocumentViewPage(properties: DocumentViewPage.Attributes) {

    const {form} = properties

    return (
        <div className={"document-view-page"}>
            <div className={"center-horizontal"}>
                <div style={{maxWidth: "800px", minWidth: "360px", height: "100%"}}>
                    <div style={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
                        <div style={{display: "flex", alignItems: "baseline"}}>
                            <h1>{form.title}</h1>
                        </div>
                        <div>
                            <button className={"material-icons"}
                                    onClick={() => navigate(`documents/document/${form.id}`)}>markdown
                            </button>
                            <button className={"material-icons"}
                                    onClick={() => navigate(`documents/document/${form.id}`)}>history
                            </button>
                        </div>

                    </div>

                    <div style={{display : "flex", gap : "5px"}}>
                        {
                            form.hashTags.map(hashTag => (<small>{hashTag.value}</small>))
                        }
                    </div>


                    <MarkDownView value={form.editor}/>
                </div>
            </div>
        </div>
    )
}

namespace DocumentViewPage {
    export interface Attributes {
        form: Document
    }
}

export default DocumentViewPage