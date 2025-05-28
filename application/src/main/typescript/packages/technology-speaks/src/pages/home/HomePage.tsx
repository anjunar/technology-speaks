import "./HomePage.css"
import React from "react"
import DocumentSearch from "../../domain/document/DocumentSearch";
import {Form, FormModel, Input, Router, useForm} from "react-ui-simplicity";
import navigate = Router.navigate;

function HomePage(properties: HomePage.Attributes) {

    const {} = properties

    let search = useForm(properties.search);

    function onSearch(event : React.KeyboardEvent) {
        if (event.key === "Enter") {
            navigate(`documents/search?text=${search.text}`)
        }
    }

    return (
        <div className={"home-page"}>
            <div className={"center"}>
                <div style={{display : "flex", justifyContent : "stretch", flexDirection : "column"}}>
                    <div style={{display : "flex", alignItems : "center", gap : "12px", fontSize : "8vw"}}>
                        <img src={"/assets/logo.png"} style={{width : "1em"}}/>
                        <h1 style={{fontSize : "1em", margin : 0, padding : 0, color : "white"}}>Technology Speaks</h1>
                    </div>
                    <Form value={search} style={{marginTop : "12px", display : "flex", justifyContent : "stretch"}}>
                        <Input name={"text"} onKeyUp={onSearch} placeholder={"Search with natural language"} style={{backgroundColor : "var(--color-background-primary)", flex : 1, width : "100%", padding : "12px"}}/>
                    </Form>
                </div>
            </div>
        </div>
    )
}

namespace HomePage {
    export interface Attributes {
        search : DocumentSearch
    }
}

export default HomePage