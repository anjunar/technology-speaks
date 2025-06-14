import './ChatPage.css'
import React, {useLayoutEffect, useRef, useState} from 'react';
import { remark } from 'remark'
import rehypePrism from "rehype-prism-plus";
import remarkRehype from "remark-rehype";
import rehypeStringify from "rehype-stringify";
import remarkGfm from "remark-gfm";
import {v4} from "uuid";

export function ChatPage(properties: ChatPage.Attributes) {

    const {} = properties

    const [buffer, setBuffer] = useState("")

    const [htmlBuffer, setHtmlBuffer] = useState("")

    const [open, setOpen] = useState(true)

    const scrollRef = useRef<HTMLDivElement>(null);

    const eventSourceRef = useRef<EventSource>(null);

    async function markdownToHtml(markdownText) {
        const file = await remark()
            .use(remarkRehype)
            .use(rehypePrism)
            .use(remarkGfm)
            .use(rehypeStringify)
            .process(markdownText)

        setHtmlBuffer(file.toString())
    }

    function onKeyUp(event : React.KeyboardEvent<HTMLTextAreaElement>) {
        let value = event.currentTarget.value;

        function startChatStream(session : string) : EventSource {
            let eventSource = new EventSource(`/service/chat?text=${encodeURIComponent(value)}&session=${session}`);
            setOpen(false)

            eventSource.onmessage = (e) => {
                let data = JSON.parse(e.data).text;

                if (data === "!Done!") {
                    eventSource.close()
                    setOpen(true)
                } else {
                    setBuffer((prev) => {
                        const next = prev + data

                        requestAnimationFrame(() => {
                            if (scrollRef.current) {
                                scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
                            }
                        });

                        return next;
                    });
                }
            }
            return eventSource;
        }

        if (event.key === "Enter") {
            event.preventDefault()

            const session = v4()

            let eventSource = startChatStream(session);
            eventSourceRef.current = eventSource;

            eventSource.onerror = (e) => {
                console.error(e)
                eventSource.close()

                setTimeout(() => {
                    eventSource = startChatStream(session)
                    eventSourceRef.current = eventSource;
                }, 3000);
            }
        }
    }

    useLayoutEffect(() => {
        markdownToHtml(buffer)
    }, [buffer]);

    return (
        <div className={"chat-page"}>
            <div className={"center-horizontal"}>
                <div ref={scrollRef} style={{minWidth : "360px", maxWidth: "800px", width : "100%", height : "100%", overflowY : "auto", padding : "20px"}}>
                    <div>
                        <h1>Chat</h1>
                    </div>
                    <div dangerouslySetInnerHTML={{__html : htmlBuffer}} style={{whiteSpace : "pre-wrap"}}></div>
                    <div style={{backgroundColor : "var(--color-background-tertiary)"}}>
                        {
                            open ? (<textarea placeholder={"Message"} onKeyUp={onKeyUp} style={{width : "100%", height : "100px"}}/>) : (<button onClick={() => {eventSourceRef.current.close(); setOpen(true)}}>Close</button>)
                        }
                    </div>
                </div>
            </div>
        </div>
    )
}

namespace ChatPage {
    export interface Attributes {
    }
}

export default ChatPage;