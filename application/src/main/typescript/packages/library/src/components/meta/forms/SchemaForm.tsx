import "./SchemaForm.css"
import React, {createContext, CSSProperties, useMemo} from "react"
import Form from "../../inputs/form/Form"
import {AsyncValidator, Error, FormModel, Validator} from "../../shared/Model";
import LinkContainerObject from "../../../domain/container/LinkContainerObject";
import NodeDescriptor from "../../../domain/descriptors/NodeDescriptor";
import Validable from "../../../domain/descriptors/Validable";

export const SchemaFormContext = createContext<(name: string) => NodeDescriptor & Validable>(null)

function SchemaForm(properties: SchemaForm.Attributes) {

    const {
        children,
        onSubmit,
        validators,
        onInput = () => {},
        onErrors,
        value,
        links,
        ...rest
    } = properties

    const node = useMemo(() => {
        return (name: string) => {
            return value.$meta(name) as NodeDescriptor & Validable
        }
    }, [])

    const onAction = (name: string, form: FormModel) => {
        if (name.endsWith("force")) {
            onSubmit(name, form)
        } else {
            if (form.errors.every((error: any) => error.errors.length === 0)) {
                onSubmit(name, form)
            }
        }
    }

    const asyncValidators = []
    if (links?.validate) {
        let link = value.links.validate;

        asyncValidators.push(new class Server implements AsyncValidator {

            type = "server"

            validate(value: any): Promise<void> {
                return new Promise((resolve, reject) => {
                    fetch("/service" + link.url, {
                        body: JSON.stringify(value.form.$data),
                        method: link.method,
                        headers: {"content-type": "application/json"}
                    })
                        .then(response => {
                            if (response.ok) {
                                resolve()
                            } else {
                                response
                                    .json()
                                    .then(response => reject(response))
                            }
                        })
                });
            }
        })
    }

    return (
        <SchemaFormContext.Provider value={node}>
            <Form
                onInput={() => onInput()}
                value={value}
                onSubmit={onAction}
                onErrors={onErrors}
                validators={validators}
                asyncValidators={asyncValidators}
                {...rest}
            >
                {children}
            </Form>
        </SchemaFormContext.Provider>
    )
}

namespace SchemaForm {
    export interface Attributes {
        children: React.ReactNode
        onSubmit?: any
        onInput?: any
        onErrors?: (errors: Error[]) => void
        value?: any
        links? : LinkContainerObject
        style?: CSSProperties,
        validators?: Validator[]
        className?: string
    }
}

export default SchemaForm