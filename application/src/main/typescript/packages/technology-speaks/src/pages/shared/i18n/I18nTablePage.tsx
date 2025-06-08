import React, {useState} from 'react';
import I18nSearch from "../../../domain/shared/i18n/I18nSearch";
import {
    Button, CollectionDescriptor,
    Link,
    LinkContainerObject,
    mapTable, ObjectDescriptor,
    Router,
    SchemaForm,
    SchemaInput,
    SchemaTable, TableObject,
    useForm
} from "react-ui-simplicity";
import {process} from "../../Root";
import I18n from "../../../domain/shared/i18n/I18n";
import navigate = Router.navigate;
import onLink = Link.onLink;

export function I18nTablePage(properties: I18nTablePage.Attributes) {

    const {queryParams, search, table : [rows, count, links, schema]} = properties

    const domain = useForm(search);

    const loader = new class extends SchemaTable.Loader {
        async onLoad(query: SchemaTable.Query, callback: SchemaTable.Callback) {
            const urlBuilder = new URL("/service/shared/i18ns", window.location.origin)
            let searchParams = urlBuilder.searchParams;

            let index = queryParams["index"] as string || query.index.toString();
            searchParams.set("index", index)
            searchParams.set("limit", query.limit.toString())
            window.history.pushState({}, "", `/shared/i18ns/search?index=${query.index}`)

            for (const sort of query.sort || []) {
                if (sort.value !== "none") {
                    searchParams.append("sort", `${sort.property}:${sort.value}`)
                }
            }

            const response = await fetch(urlBuilder.toString(),)
            if (response.ok) {
                const [rows, size, links, schema] = mapTable(await response.json())
                callback(rows, Number.parseInt(index), size, schema)
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

    function onSubmit(name: string, form: any) {
        loader.fire()
    }

    return (
        <div className={"i18n-table-page"}>
            <div className={"center-horizontal"}>
                <div className={"responsive-column"}>
                    <SchemaForm value={domain} onSubmit={onSubmit}>
                        <SchemaInput name={"text"}/>
                        <div style={{display : "flex", justifyContent : "flex-end"}}>
                            {
                                onLink(links, "search", (link) => (
                                    <Button name={"search"}>{link.title}</Button>
                                ))
                            }
                        </div>
                    </SchemaForm>
                    <SchemaTable loader={loader}
                                 onRowClick={onRowClick}
                                 limit={10}
                                 initialData={() => [rows, count, schema]}
                                 style={{width : "100%"}}>
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
            </div>
        </div>
    )
}

namespace I18nTablePage {
    export interface Attributes {
        queryParams: Router.QueryParams
        search : I18nSearch
        table : [I18n[], number, LinkContainerObject, ObjectDescriptor]
    }
}

export default I18nTablePage;