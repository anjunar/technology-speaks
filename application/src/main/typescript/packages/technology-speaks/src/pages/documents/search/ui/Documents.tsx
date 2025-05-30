import React, {CSSProperties} from "react"
import {format, List, mapTable, Pageable, Router} from "react-ui-simplicity";
import Document from "../../../../domain/document/Document";
import {process} from "../../../../App";

function Documents(properties: Documents.Attributes) {

    const {text, style, onSelect} = properties

    const loader = new class extends Pageable.Loader {
        async onLoad(query: Pageable.Query, callback: Pageable.Callback) {
            const urlBuilder = new URL("/service/documents", window.location.origin)
            const searchParams = urlBuilder.searchParams;

            searchParams.set("index", query.index.toString())
            searchParams.set("limit", query.limit.toString())
            searchParams.set("text", text)
            searchParams.set("sort", "score:asc")

            const response = await fetch(urlBuilder.toString())

            if (response.ok) {
                let [mapped, size] = mapTable(await response.json());
                callback(mapped, size)
            } else {
                process(response)
            }
        }
    }

    return (
        <div className={"documents"} style={{minWidth : "360px", ...style}}>
            <List loader={loader}>
                <List.Item>
                    {
                        ({row} : {row : Document}) => (
                            <div className={"selected"} onClick={() => onSelect(row)}>
                                <div style={{display : "flex", alignItems : "baseline", justifyContent : "space-between", gap : "12px"}}>
                                    <h2 style={{color : "var(--color-selected)"}}>{row.title}</h2>
                                    <small>{row.user.nickName}: {format(row.created, "dd.MM.yyyy HH:mm")}</small>
                                </div>
                                <p>{row.description}</p>
                            </div>
                        )
                    }
                </List.Item>
            </List>
        </div>
    )
}

namespace Documents {
    export interface Attributes {
        text : string
        onSelect : (document : Document) => void
        style? : CSSProperties
    }
}

export default Documents