import "./SearchPage.desktop.css"
import React, {useState} from "react"
import {MarkDownView, Router} from "react-ui-simplicity";
import Document from "../../../domain/document/Document";
import Documents from "./ui/Documents";
import navigate = Router.navigate;

function SearchPageDesktop(properties: SearchPageDesktop.Attributes) {

    const {queryParams} = properties

    const [selected, setSelected] = useState<Document>(null)

    return (
        <div className={"search-page"}>
            <Documents style={{flex : 1}} text={queryParams["text"]} selected={selected} onSelect={row => setSelected(row)}/>
            <div style={{flex: 1}}>
                <div style={{display : "flex", justifyContent : "space-between", alignItems : "baseline"}}>
                    <h1 style={{color: "var(--color-selected)"}}>{selected?.title}</h1>
                    <button onClick={() => navigate(`documents/document/${selected.id}`)} className={"material-icons"}>settings</button>
                </div>
                <MarkDownView value={selected?.editor}/>
            </div>

        </div>
    )
}

namespace SearchPageDesktop {
    export interface Attributes {
        queryParams: Router.QueryParams
    }
}

export default SearchPageDesktop