import "./DocumentSearchPage.css"
import React, {useEffect, useState} from "react"
import {format, List, mapTable, Pageable, Router} from "react-ui-simplicity";
import {process} from "../../../App";
import Document from "../../../domain/document/Document";
import navigate = Router.navigate;

function DocumentSearchPage(properties: SearchPageMobile.Attributes) {

    const {queryParams} = properties

    const loader = new class extends Pageable.Loader {
        async onLoad(query: Pageable.Query, callback: Pageable.Callback) {
            const urlBuilder = new URL("/service/documents", window.location.origin)
            const searchParams = urlBuilder.searchParams;

            searchParams.set("index", query.index.toString())
            searchParams.set("limit", query.limit.toString())
            searchParams.set("text", queryParams["text"])
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
        <div className={"search-page"}>
            <div className={"center-horizontal"}>
                <List loader={loader}>
                    <List.Item>
                        {
                            ({row}: { row: Document }) => (
                                <div className={"selected"} onClick={() => navigate(`/documents/document/${row.id}`)}>
                                    <div style={{
                                        display: "flex",
                                        alignItems: "baseline",
                                        justifyContent: "space-between",
                                        gap: "12px"
                                    }}>
                                        <div style={{display: "flex", alignItems: "baseline", gap: "12px"}}>
                                            <h2 style={{color: "var(--color-selected)"}}>{row.title}</h2>
                                            <small>{row.score}</small>
                                        </div>
                                        <small>{row.user.nickName}: {format(row.created, "dd.MM.yyyy HH:mm")}</small>
                                    </div>
                                    <p>{row.description}</p>
                                </div>
                            )
                        }
                    </List.Item>
                </List>
            </div>
        </div>
    )
}

namespace SearchPageMobile {
    export interface Attributes {
        queryParams: Router.QueryParams
    }
}

export default DocumentSearchPage