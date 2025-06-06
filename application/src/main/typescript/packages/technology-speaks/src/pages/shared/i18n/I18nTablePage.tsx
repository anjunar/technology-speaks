import React from 'react';
import I18nSearch from "../../../domain/shared/i18n/I18nSearch";
import {mapTable, Router, SchemaTable} from "react-ui-simplicity";
import {process} from "../../../App";
import I18n from "../../../domain/shared/i18n/I18n";
import navigate = Router.navigate;

export function I18nTablePage(properties: I18nTablePage.Attributes) {

    const {search} = properties

    const loader = new class extends SchemaTable.Loader {
        async onLoad(query: SchemaTable.Query, callback: SchemaTable.Callback) {
            const urlBuilder = new URL("/service/shared/i18ns", window.location.origin)
            let searchParams = urlBuilder.searchParams;

            searchParams.set("index", query.index.toString())
            searchParams.set("limit", query.limit.toString())

            for (const sort of query.sort || []) {
                if (sort.value !== "none") {
                    searchParams.append("sort", `${sort.property}:${sort.value}`)
                }
            }

            const response = await fetch(urlBuilder.toString(),)
            if (response.ok) {
                const [rows, size, links, schema] = mapTable(await response.json())
                callback(rows, size, schema)
            } else {
                process(response)
            }
        }
    }

    function onRowClick(i18n: I18n) {
        let link = i18n.$links?.["read"]
        if (link) {
            navigate(link.url)
        }
    }

    return (
        <div className={"i18n-table-page"}>
            <SchemaTable loader={loader} onRowClick={onRowClick} limit={10}>
                <SchemaTable.Head>
                    <SchemaTable.Head.Cell property={"text"}/>
                    <SchemaTable.Head.Cell property={"translations"}/>
                </SchemaTable.Head>
                <SchemaTable.Body>
                    <SchemaTable.Body.Cell>
                        {({row, index}: { row: I18n, index: number }) => <div key={row.id}>{row.text}</div>}
                    </SchemaTable.Body.Cell>
                    <SchemaTable.Body.Cell>
                        {({row, index}: { row: I18n, index: number }) => <div key={row.id}>{row.translations.map(translation => translation.locale + ":" + translation.text ).join("\n")}</div>}
                    </SchemaTable.Body.Cell>
                </SchemaTable.Body>
            </SchemaTable>
        </div>
    )
}

namespace I18nTablePage {
    export interface Attributes {
        search : I18nSearch
    }
}

export default I18nTablePage;