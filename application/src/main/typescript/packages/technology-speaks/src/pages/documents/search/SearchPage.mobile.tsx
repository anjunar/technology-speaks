import "./SearchPage.mobile.css"
import React, {useState} from "react"
import Document from "../../../domain/document/Document";
import {Router} from "react-ui-simplicity";
import Documents from "./ui/Documents";

function SearchPageMobile(properties: SearchPageMobile.Attributes) {

    const {queryParams} = properties

    const [selected, setSelected] = useState<Document>(null)

    return (
        <div className={"search-page"}>
            <Documents text={queryParams["text"]} selected={selected} onSelect={row => setSelected(row)}/>
        </div>
    )
}

namespace SearchPageMobile {
    export interface Attributes {
        queryParams: Router.QueryParams
    }
}

export default SearchPageMobile