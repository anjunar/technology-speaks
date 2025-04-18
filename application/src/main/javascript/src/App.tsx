import "./App.css"

import React, {useEffect, useLayoutEffect, useState} from "react"
import {Drawer, Link, Router, ToolBar, useMatchMedia, Viewport} from "react-ui-simplicity";
import navigate = Router.navigate;
import Application from "./domain/Application";
import onLink = Link.onLink;

export function process(response: Response) {
    if (response.status === 403) {
        if (location.search.indexOf("redirect") === -1) {
            navigate(`/security/login?redirect=${location.pathname}${location.search}`)
        }
    }
}

function App(properties: AppContent.Attributes) {

    const {application} = properties

    const [open, setOpen] = useState(false)

    const mediaQuery = useMatchMedia("(max-width: 1440px)")

    const [page, setPage] = useState(0)

    const onLinkClick = () => {
        if (mediaQuery) {
            setOpen(false)
        } else {
            setOpen(true)
        }
    }

    useEffect(() => {
        setOpen(! mediaQuery)
    }, []);

    return (
        <div className={"app"}>
            <ToolBar>
                <div slot="left">
                    <button className="material-icons" onClick={() => setOpen(!open)}>
                        menu
                    </button>
                </div>
                <div slot={"right"}>
                    <div style={{display : "flex", gap : "5px", justifyContent : "flex-end"}}>
                        {
                            onLink(application.$links, "login", (link) => (
                                <Link value={link.url}>
                                    <span className="material-icons">login</span>
                                </Link>
                            ))
                        }
                        {
                            onLink(application.$links, "register", (link) => (
                                <Link value={link.url}>
                                    <span className="material-icons">app_registration</span>
                                </Link>
                            ))
                        }
                        {
                            onLink(application.$links, "logout", (link) => (
                                <Link value={link.url}>
                                    <span className="material-icons">logout</span>
                                </Link>
                            ))
                        }
                    </div>
                </div>
            </ToolBar>
            <Drawer.Container>
                <Drawer open={open}>
                    <h1>Content</h1>
                </Drawer>
                <Drawer.Content onClick={onLinkClick}>
                    <Viewport>
                        <Router/>
                    </Viewport>
                </Drawer.Content>
            </Drawer.Container>
        </div>
    )
}

namespace AppContent {
    export interface Attributes {
        application : Application
    }
}

export default App