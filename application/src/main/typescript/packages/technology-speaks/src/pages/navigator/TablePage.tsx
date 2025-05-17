import "./TablePage.css"
import React, {useContext, useEffect, useMemo, useState} from "react"
import {AbstractEntity, AbstractSearch, ActiveObject, DateDuration, JSONDeserializer, JSONSerializer, Link, LinkContainerObject, mapTable, Router, SchemaTable} from "react-ui-simplicity";
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

    const [search, setSearch] = useState<AbstractSearch>(null)

    const url = "service" + atob(queryParams.link || "")

    const body = atob(queryParams.body || "")

    const loader = useMemo(() => {
        return new (class extends Loader {
            async onLoad(query : Query, callback : Callback) {
                const urlBuilder = new URL(url, window.location.origin)

                let body : any = null

                if (search) {
                    search.index = query.index
                    search.limit = query.limit
                    body = JSONSerializer(search)
                } else {
                    body = {
                        index : 0,
                        limit : 10
                    }
                }

                const response = await fetch(urlBuilder.toString(), {method : "POST", body : JSON.stringify(body), headers : {"content-type" : "application/json"}})

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
    }, [search]);

    const onRowClick = (row : any) => {
        let linkModel = Object.values(row.$links as LinkContainerObject).find((link) => link.rel === "read")

        if (linkModel) {
            navigate(`/navigator/form?link=${btoa(linkModel.url)}&body=${btoa(JSON.stringify(JSONSerializer(linkModel.body)))}`)
        }
    }

    function onSearchSubmit(search : ActiveObject) {
        loader.fire()
    }

    return (
        <div>
            <div>
                {url}
                <br/>
                {body}
                <br/>
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