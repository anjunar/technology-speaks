import "./Link.css"
import React, {CSSProperties, ReactNode, useLayoutEffect, useState} from "react"
import RestLink from "../../../domain/container/LinkObject";
import LinkContainerObject from "../../../domain/container/LinkContainerObject";
import LinksObject from "../../../domain/container/LinksObject";
import Router from "../router/Router";

function Link(properties : Link.Attributes) {

    const {data, value, children, className, style, icon, onClick} = properties

    let href = value.replace("//", "/");

    const [activeState, setActiveState] = useState(false)

    const onClickHandler: React.MouseEventHandler<HTMLAnchorElement> = event => {
        event.preventDefault()
        Router.navigate(href)
        onClick && onClick(event)
    }

    useLayoutEffect(() => {
        let listener = () => {
            setActiveState(window.location.pathname === href)
        }

        listener()

        window.addEventListener("popstate", listener)

        return () => {
            window.removeEventListener("popstate", listener)
        }
    }, [])

    return (
        <a
            style={style}
            href={href}
            onClick={onClickHandler}
            className={className + (activeState ? " active" : "")}>
            <div style={{display: "flex", gap: "12px", alignItems: "center"}}>
                {
                    icon && <span className="material-icons">{icon}</span>
                }
                {
                    children && <span>{children}</span>
                }
            </div>
        </a>
    )
}

namespace Link {

    export interface Attributes {
        data?: any
        value: string
        icon? : string
        children?: ReactNode
        style? : CSSProperties
        className? : string
        onClick? : React.MouseEventHandler<HTMLAnchorElement>
    }

    export function onLink($links :LinkContainerObject, rel: string, callback: (link: RestLink) => React.ReactNode) {
        if ($links) {
            let link = $links[rel];

            if (link) {
                return callback(link)
            }
        }
        return ""
    }

    export function renderWithSymbol(container: LinksObject) {
        return Object.values(container?.links || {}).map((link: any) => (
            <Link key={link.rel} value={link.url}>
                <i className={"material-icons"}>navigate_next</i>
            </Link>
        ));
    }

    export function renderWithDescription(container: LinksObject) {
        return Object.values(container?.links || {}).map((link: any) => (
            <Link key={link.rel} value={link.url}>
                {link.title}
            </Link>
        ));
    }

}

export default Link