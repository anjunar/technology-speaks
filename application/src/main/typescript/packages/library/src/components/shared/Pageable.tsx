import React, {MouseEventHandler, useEffect, useState} from "react"

function withPageable(Component : any, parameters : withPageable.Attributes)  {

    const {autoload = true, value, children, limit = 5, onRowClick, onChange, loader, initialData, ...rest} = parameters

    return function Pageable() {

        const [rows, count] = initialData ? initialData() : [[], 0]

        const [window, setWindow] = useState(rows)
        const [index, setIndex] = useState(0)
        const [size, setSize] = useState(count)

        const [loading, setLoading] = useState(autoload)

        const skipPrevious = () => {
            let newIndex = 0
            setIndex(newIndex)
            load({index : newIndex, limit}, () => {})
        }

        const arrowLeft = () => {
            let newIndex = index - limit
            setIndex(newIndex)
            load({index : newIndex, limit}, () => {})
        }

        const arrowRight = () => {
            let newIndex = index + limit
            setIndex(newIndex)
            load({index : newIndex, limit}, () => {})
        }

        const skipNext = () => {
            let number = Math.round(size / limit)
            let newIndex = (number - 1) * limit
            setIndex(newIndex)
            load({index : newIndex, limit}, () => {})
        }

        function load(query : withPageable.Query, callback : () => void) {
            loader.onLoad(query, (rows, size) => {
                setSize(size)
                setWindow(rows)
                callback()
            })
        }

        if (loader) {
            loader.listener = () => {
                load({index : index, limit : limit}, () => {})
            }
        } else {
            throw new Error("Loader is missing")
        }

        useEffect(() => {
            if (autoload) {
                load({index : index, limit : limit}, () => {setLoading(false)})
            }
        }, [])

        if (loading) {
            return <div className={"center"}><h2>Loading...</h2></div>
        }

        return (
            <Component
                onRowClick={onRowClick}
                index={index}
                size={size}
                limit={limit}
                window={window}
                load={load}
                loader={loader}
                initialData={initialData}
                skipPrevious={skipPrevious}
                arrowLeft={arrowLeft}
                arrowRight={arrowRight}
                skipNext={skipNext}
                value={value}
                onChange={onChange}
                {...rest}
            >
                {children}
            </Component>
        )
    }
}

namespace withPageable {
    export interface Attributes {
        autoload? : boolean
        onRowClick? : MouseEventHandler<HTMLDivElement>
        loader : Loader
        initialData? : () => [any[], number]
        limit? : number
        children : React.ReactNode
        value? : any[]
        onChange? : (value : any[]) => void
    }
}

namespace withPageable {
    export interface Query {
        index : number
        limit : number
        value? : string
    }

    export interface Callback {
        (rows : any[], size : number) : void
    }

    export abstract class Loader {
        listener : () => void
        abstract onLoad(query : Query, callback : Callback) : void
        fire() {
            if (this.listener) {
                this.listener();
            }
        }
    }

}

export default withPageable