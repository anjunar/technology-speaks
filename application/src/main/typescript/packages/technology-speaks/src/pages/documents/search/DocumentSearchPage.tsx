import "./DocumentSearchPage.css"
import React, {useState} from "react"
import {
    Button,
    format,
    Link,
    LinkContainerObject,
    List,
    mapTable, ObjectDescriptor,
    Pageable,
    Router,
    SchemaForm,
    SchemaInput,
    useForm
} from "react-ui-simplicity";
import {process} from "../../Root";
import Document from "../../../domain/document/Document";
import navigate = Router.navigate;
import DocumentSearch from "../../../domain/document/DocumentSearch";
import onLink = Link.onLink;

function DocumentSearchPage(properties: SearchPageMobile.Attributes) {

    const {queryParams, table : [rows, count, links, schema]} = properties

    let search = useForm(properties.search);

    const loader = new class extends Pageable.Loader {
        async onLoad(query: Pageable.Query, callback: Pageable.Callback) {
            const urlBuilder = new URL("/service/documents", window.location.origin)
            const searchParams = urlBuilder.searchParams;

            searchParams.set("index", query.index.toString())
            searchParams.set("limit", query.limit.toString())

            if (search.text) {
                searchParams.set("text", search.text)
                searchParams.set("sort", "score:asc")
            }

            const response = await fetch(urlBuilder.toString())

            if (response.ok) {
                let [mapped, size, links] = mapTable(await response.json());
                callback(mapped, size)
            } else {
                process(response)
            }
        }
    }

    function onSubmit(name: string, form: any) {
        loader.fire()
    }

    return (
        <div className={"search-page"}>
            <div className={"center-horizontal"}>
                <div style={{display: "flex", flexWrap: "wrap-reverse", gap: "24px", alignItems: "baseline"}}>
                    <List autoload={false} loader={loader} style={{minWidth: "360px", maxWidth: "800px", width: "100%"}} initialData={() => [rows, count]}>
                        <List.Item>
                            {
                                ({row}: { row: Document }) => (
                                    <div className={"selected"}
                                         onClick={() => navigate(`/documents/document/${row.id}`)}>
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
                    <div className={"search-box"} style={{minWidth: "360px", maxWidth : "800px"}}>
                        <div>
                            <div style={{display : "flex", alignItems : "center", justifyContent : "space-between", gap : "12px"}}>
                                <h2>Search</h2>
                                {
                                    onLink(links, "create", (link) => (
                                        <Link key={link.url} value={link.url} className={"material-icons"}>
                                            edit_note
                                        </Link>
                                    ))
                                }
                            </div>
                            <SchemaForm value={search} onSubmit={onSubmit}>
                                <SchemaInput name={"text"}/>
                                <div style={{display : "flex", justifyContent : "flex-end"}}>
                                    {
                                        onLink(links, "search", (link) => (
                                            <Button name={"search"}>{link.title}</Button>
                                        ))
                                    }
                                </div>
                            </SchemaForm>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

namespace SearchPageMobile {
    export interface Attributes {
        queryParams: Router.QueryParams
        search : DocumentSearch
        table : [Document[], number, LinkContainerObject, ObjectDescriptor]
    }
}

export default DocumentSearchPage