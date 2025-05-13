import React, {useContext, useEffect, useRef, useState} from "react"
import {MarkDownContext} from "../../MarkDown";

function decodeBase64(result: string) {
    let base64 = /data:(\w+)\/(\w+);base64,((?:[A-Za-z0-9+\/]{4})*(?:[A-Za-z0-9+\/]{4}|[A-Za-z0-9+\/]{3}=|[A-Za-z0-9+\/]{2}={2}))/g
    let regexResult = base64.exec(result)
    if (regexResult) {
        let type = regexResult[1]
        let subType = regexResult[2]
        let data = regexResult[3]
        return {type, subType, data}
    }
    throw new Error("Cannot decode to Base64")
}

function ImageButton(properties: ImageButton.Attributes) {

    const {children, title} = properties

    const inputRef = useRef<HTMLInputElement>(null);

    const [disabled, setDisabled] = useState(true)

    const {model, textAreaRef, cursor} = useContext(MarkDownContext)

    function onClick() {

        let input = inputRef.current;
        let textArea = textAreaRef.current;

        let selectionStart = textArea.selectionStart;
        let selectionEnd = textArea.selectionEnd

        input.addEventListener("change", (event) => {
            if (input.files) {
                let file = input.files[0]
                let reader = new FileReader()
                reader.onload = e => {
                    if (reader.result) {

                        const {type, subType, data} = decodeBase64(reader.result as string)

                        model.store.files.push({
                            name: file.name,
                            type: type,
                            subType : subType,
                            data: data,
                            lastModified: file.lastModified
                        })

                        let pre = textArea.value.substring(0, selectionStart);
                        let post = textArea.value.substring(selectionEnd);

                        textArea.value = `${pre}![Picture](${file.name})${post}`

                        const event = new Event('input', {bubbles: true, cancelable: true})

                        textArea.dispatchEvent(event);

                        textArea.focus()

                    }
                }
                reader.readAsDataURL(file)
            }
        })

        input.click()
    }

    useEffect(() => {
        if (cursor !== null) {
            setDisabled(cursor.length > 0)
        }
    }, [cursor]);


    return (
        <div className={"image-button"}>
            <input ref={inputRef} type={"file"} style={{display: "none"}}/>
            <button disabled={disabled} title={title} className={"material-icons"} onClick={onClick}>{children}</button>
        </div>
    )
}

namespace ImageButton {
    export interface Attributes {
        children: React.ReactNode
        title : string
    }
}

export default ImageButton