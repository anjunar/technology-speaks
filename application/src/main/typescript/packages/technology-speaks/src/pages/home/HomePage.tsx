import "./HomePage.css"
import React from "react"

function HomePage(properties: HomePage.Attributes) {

    const {} = properties

    return (
        <div className={"home-page"}>
            <div className={"center"} style={{fontSize : "8vw"}}>
                <div style={{display : "flex", alignItems : "center", gap : "12px"}}>
                    <img src={"/assets/logo.png"} style={{width : "1em"}}/>
                    <h1 style={{fontSize : "1em", margin : 0, padding : 0, color : "white"}}>Technology Speaks</h1>
                </div>
            </div>
        </div>
    )
}

namespace HomePage {
    export interface Attributes {

    }
}

export default HomePage