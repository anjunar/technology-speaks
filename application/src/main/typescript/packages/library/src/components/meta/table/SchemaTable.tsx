import React, {CSSProperties, useState} from "react"
import Table from "../../lists/table/Table"
import SchemaFactory from "./SchemaFactory";
import Image from "../../inputs/upload/image/Image";
import ObjectDescriptor from "../../../domain/descriptors/ObjectDescriptor";
import NodeDescriptor from "../../../domain/descriptors/NodeDescriptor";
import CollectionDescriptor from "../../../domain/descriptors/CollectionDescriptor";
import {Temporal, TemporalAccessor, TemporalAmount} from "@js-joda/core";
import Validable from "../../../domain/descriptors/Validable";

function SchemaTable(properties: SchemaTable.Attributes) {

    const {loader, onRowClick, selectable, name, style} = properties

    const [schema, setSchema] = useState(null)

    function toArray(schema: ObjectDescriptor): any[] {
        if (schema) {
            return Object.entries((schema.properties.rows as CollectionDescriptor).items.properties || {})
        } else {
            return []
        }
    }

    const tableLoader = new (class extends SchemaTable.Loader {
        onLoad(query: any, callback: any) {
            loader.onLoad(query, (rows, size, loadedSchema) => {
                    if (schema && loadedSchema) {
                        if (JSON.stringify(schema) !== JSON.stringify(loadedSchema)) {
                            setSchema(loadedSchema)
                        }
                    } else {
                        setSchema(loadedSchema)
                    }
                    callback(rows, size)
                }
            )
        }
    })()

    const renderCellContent = (object: any, key: string, property: any) => {
        if (property.$type === "CollectionDescriptor") {
            let naming: any[] = [];
            if (property.items.properties) {
                naming = Object.entries(property.items.properties)
                    .filter(([key, node]: [key: string, node: any]) => node.name)
                    .map(([key, node]) => key)
            } else {
                let flatMap = property.items.oneOf.flatMap((item: any) => item.properties);
                // TODO : UGLY!
                naming = Object.entries(flatMap[0])
                    .filter(([key, node]: [key: string, node: any]) => node.name)
                    .map(([key, node]) => key)

            }

            return object[key]
                .map((object: any) =>
                    Object.keys(object)
                        .filter(key => naming.indexOf(key) > -1)
                        .map(key => object[key])
                )
                .join(" ")
        }

        if (property.$type === "ObjectDescriptor") {
            if (property.widget === "image" && object[key]) {
                return (<Image style={{width: "32px", height: "32px"}} value={object[key]} disabled={true}/>)
            }
            let naming = Object.entries(property.properties || {})
                .filter(([key, node]: [key: string, node: any]) => node.name)
                .map(([key, node]) => key)

            if (object[key]) {
                return Object.entries(object[key])
                    .filter(([key, object]) => {
                        return naming.indexOf(key) > -1
                    })
                    .map(([key, object]) => {
                        if (object instanceof Temporal) {
                            // @ts-ignore
                            return object.toJSON()
                        }
                        return object
                    })
                    .join(" ")
            }

            return ""
        }

        let objectElement = object[key]

        if (objectElement instanceof TemporalAmount || objectElement instanceof TemporalAccessor) {
            // @ts-ignore
            return objectElement.toJSON()
        }

        if (typeof objectElement === "boolean") {
            return objectElement ? "true" : "false"
        }

        return objectElement
    }

    return (
        <Table className={"table"} loader={tableLoader} onRowClick={onRowClick} selectable={selectable} name={name} style={style}>
            <Table.Filter>
                {toArray(schema).map(([key, value]: [key: string, value: NodeDescriptor & Validable]) => (
                    <Table.Filter.Cell key={key}>
                        {({data}) => <SchemaFactory schema={value} value={data.value}
                                                    onChange={(event: any) => data.change(event.target.value)}/>}
                    </Table.Filter.Cell>
                ))}
            </Table.Filter>
            <Table.Head>
                {toArray(schema).map(([key, value]) => (
                    <Table.Head.Cell key={key} property={key}>
                        {value.title}
                    </Table.Head.Cell>
                ))}
            </Table.Head>
            <Table.Body>
                {toArray(schema).map(([key, value]) => (
                    <Table.Body.Cell key={key}>
                        {({row, index}) => <div>{renderCellContent(row, key, value)}</div>}
                    </Table.Body.Cell>
                ))}
            </Table.Body>
        </Table>
    )
}

namespace SchemaTable {
    export interface Attributes {
        loader: Loader
        onRowClick?: any
        selectable?: boolean
        name?: string
        style? : CSSProperties
    }

    export abstract class Loader {
        listener: any

        abstract onLoad(query: Query, callback: Callback): void

        fire() {
            if (this.listener) {
                this.listener();
            }
        }
    }

    export interface Query {
        index: number
        limit: number
        filter : any
        sort : any
    }

    export interface Callback {
        (rows: any[], size: number, schema: ObjectDescriptor): void
    }
}

export default SchemaTable