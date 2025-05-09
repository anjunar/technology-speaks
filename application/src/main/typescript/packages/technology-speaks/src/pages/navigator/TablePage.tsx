import "./TablePage.css"
import React, {useContext, useEffect, useMemo, useState} from "react"
import {AbstractEntity, ActiveObject, DateDuration, Link, LinkContainerObject, mapTable, Router, SchemaTable} from "react-ui-simplicity";
import Loader = SchemaTable.Loader;
import Query = SchemaTable.Query;
import Callback = SchemaTable.Callback;
import navigate = Router.navigate;
import QueryParams = Router.QueryParams;
import {process} from "../../App"
import Search from "./Search";

function TablePage(properties : TableView.Attributes) {

    const { queryParams } = properties

    const [links, setLinks] = useState<LinkContainerObject>({})

    const [search, setSearch] = useState<ActiveObject>(null)

    const onSearch = useMemo(() => {
        return {form : { form : null }}
    }, []);

    const url = "service" + atob(queryParams.link || "")

    const loader = useMemo(() => {
        return new (class extends Loader {
            async onLoad(query : Query, callback : Callback) {
                const urlBuilder = new URL(url, window.location.origin)
                let searchParams = urlBuilder.searchParams;

                searchParams.append("index", query.index.toString())
                searchParams.append("limit", query.limit.toString())

                if (onSearch.form.form?.text) {
                    searchParams.append("text", onSearch.form.form.text)
                }

                if (query.filter instanceof Array) {
                    query.filter
                        .filter(filter => filter.value)
                        .forEach(filter => {
                            if (filter.value instanceof Array) {
                                for (const item of filter.value) {
                                    if (item instanceof AbstractEntity) {
                                        searchParams.append(filter.property, item.id)
                                    }
                                }
                            } else {
                                if (filter.value instanceof AbstractEntity) {
                                    searchParams.append(filter.property, filter.value.id)
                                } else {
                                    if (filter.value instanceof DateDuration) {
                                        searchParams.append(filter.property, filter.value.toString())
                                    } else {
                                        searchParams.append(filter.property, filter.value)

                                    }
                                }

                            }
                        })
                }

                if (query.sort instanceof Array) {
                    query.sort
                        .filter(order => order.value !== "none")
                        .map(order => order.property + ":" + order.value)
                        .forEach(order => searchParams.append("sort", order))
                }

                let response = await fetch(urlBuilder.toString())

                if (response.ok) {
                    let [mapped, size, links, schema, search] = mapTable(await response.json());
                    setLinks(links || {})
                    setSearch(search)
                    callback(mapped, size, schema)
                } else {
                    process(response)
                }
            }
        })()
    }, []);

    const onRowClick = (row : any) => {
        let linkModel = Object.values(row.$links as LinkContainerObject).find((link) => link.rel === "read")

        if (linkModel) {
            navigate(`/navigator/form?link=${btoa(linkModel.url)}`)
        }
    }

    function onSearchSubmit(search : ActiveObject) {
        onSearch.form = { form : search }
        loader.fire()
    }

    return (
        <div>
            <div>
                {url}
                <br />
                {Object.values(links).map(link => (
                    <Link
                        style={{ margin: "5px" }}
                        key={link.rel}
                        value={`/navigator/${link.linkType}?link=${btoa(link.url)}`}
                    >
                        {link.title}
                    </Link>
                ))}
            </div>
            {
                search && <Search value={search} submit={onSearchSubmit}/>
            }
            <div style={{overflow : "auto", width : "100%"}}>
                <SchemaTable loader={loader} onRowClick={(row : any) => onRowClick(row)} />
            </div>
        </div>
    )
}

namespace TableView {
    export interface Attributes {
       queryParams : QueryParams
    }
}

export default TablePage