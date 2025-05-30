import "./SearchPage.css"
import React from "react"
import {Router} from "react-ui-simplicity";
import Documents from "./ui/Documents";
import navigate = Router.navigate;

function SearchPage(properties: SearchPageMobile.Attributes) {

    const {queryParams} = properties

    return (
        <div className={"search-page"}>
            <div className={"center-horizontal"}>
                <Documents onSelect={document => navigate(`/documents/search/${document.id}`)} style={{maxWidth: "800px"}} text={queryParams["text"]}/>
            </div>
        </div>
    )
}

namespace SearchPageMobile {
    export interface Attributes {
        queryParams: Router.QueryParams
    }
}

export default SearchPage